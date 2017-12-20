package imcode.server.document.index.service.impl

import java.util.Optional
import java.util.concurrent._
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import java.util.function.Consumer

import _root_.imcode.server.document.index.service._
import com.imcode._
import com.imcode.imcms.api.ServiceUnavailableException
import com.imcode.util.Threads
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.{SolrQuery, SolrServer}

import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

/**
 * Implements all DocumentIndexService functionality.
 * Ensures that update and rebuild operations never run concurrently.
 *
 * Indexing errors are handled asynchronously as ManagedSolrDocumentIndexService.IndexError events.
 * On index write (update or rebuild) failure the service stops processing write requests.
 *
 * The business-logic (like rebuild scheduling and index recovery) is implemented on higher levels.
 */
class ManagedDocumentIndexService(
    solrServerReader: SolrServer,
    solrServerWriter: SolrServer,
    serviceOps: DocumentIndexServiceOps,
    failureHandler: ServiceFailure => Unit) extends DocumentIndexService {

  private val lock = new AnyRef
  private val shutdownRef = new AtomicBoolean(false)
  private val indexRebuildThreadRef = new AtomicReference[Thread]
  private val indexUpdateThreadRef = new AtomicReference[Thread]
  private val indexUpdateRequests = new LinkedBlockingQueue[IndexUpdateOp](1000)
  private val indexWriteFailureRef = new AtomicReference[ServiceFailure]
  private val indexRebuildTaskRef = new AtomicReference[IndexRebuildTask]

  /**
   * Creates and starts new index-rebuild-thread if there is no already running one.
   * Any exception or an interruption terminates index-rebuild-thread.
   *
   * An existing index-update-thread is stopped before rebuilding happens and a new index-update-thread is started
   * immediately after running index-rebuild-thread is terminated without errors.
   */
  override def rebuild(): IndexRebuildTask = {
    logger.info("attempting to start new document-index-rebuild thread.")

    (shutdownRef.get, indexWriteFailureRef.get, indexRebuildThreadRef.get, indexRebuildTaskRef.get()) match {
      case (shutdown@true, _, _, _) =>
        val errorMsg = "new document-index-rebuild thread can not be started - service is shut down."
        logger.error(errorMsg)
        throw new ServiceUnavailableException(errorMsg)

      case (_, indexWriteFailure, _, _) if indexWriteFailure != null =>
        logger.error(s"new document-index-rebuild thread can not be started - previous index write attempt has failed with error [$indexWriteFailure].")
        throw indexWriteFailure.getException

      case (_, _, indexRebuildThread, indexRebuildTask) if Threads.notTerminated(indexRebuildThread) =>
        logger.info(s"new document-index-rebuild thread can not be started - document-index-rebuild thread [$indexRebuildThread] is already running.")
        indexRebuildTask

      case _ =>
        startNewIndexRebuildThread()
    }
  }


  private def startNewIndexRebuildThread(): IndexRebuildTask = {
    new IndexRebuildTask {
      val progressRef = new AtomicReference[IndexRebuildProgress]
      val futureTask = new FutureTask[Unit](Threads.mkCallable {
        serviceOps.rebuildIndex(
          solrServerWriter,
          new Consumer[IndexRebuildProgress] {
            override def accept(p: IndexRebuildProgress): Unit = progressRef.set(p)
          }
        )
      })

      override def progress(): Optional[IndexRebuildProgress] = Optional.ofNullable(progressRef.get())

      override def future(): Future[_] = futureTask

    } |>> indexRebuildTaskRef.set |>> { indexRebuildTaskImpl =>
      new Thread { indexRebuildThread =>
        private def submitStartNewIndexUpdateThread(): Unit = Threads.spawnDaemon {
          indexRebuildThread.join()
          startNewIndexUpdateThread()
        }

        override def run() {
          try {
            interruptIndexUpdateThreadAndAwaitTermination()
            indexUpdateRequests.clear()
            indexRebuildTaskImpl.futureTask |> { futureTask =>
              futureTask.run()
              futureTask.get()
            }

            submitStartNewIndexUpdateThread()
          } catch {
            case _: InterruptedException =>
              logger.debug(s"document-index-rebuild thread [$indexRebuildThread] was interrupted")
              submitStartNewIndexUpdateThread()

            case _: CancellationException =>
              logger.debug(s"document-index-rebuild task was cancelled. document-index-rebuild thread: [$indexRebuildThread].")
              submitStartNewIndexUpdateThread()

            case e: ExecutionException =>
              val cause = e.getCause
              val writeFailure = new ServiceFailure(ManagedDocumentIndexService.this, cause, ServiceFailure.Type.REBUILD)
              logger.error(s"document-index-rebuild task has failed. document-index-rebuild thread: [$indexRebuildThread].", cause)
              indexWriteFailureRef.set(writeFailure)
              Threads.spawnDaemon {
                failureHandler(writeFailure)
              }
          } finally {
            logger.info(s"document-index-rebuild thread [$indexRebuildThread] is about to terminate.")
          }
        }
      } |>> indexRebuildThreadRef.set |>> { indexRebuildThread =>
        indexRebuildThread.setDaemon(true)
        indexRebuildThread.setName(s"document-index-rebuild-${indexRebuildThread.getId}")
        indexRebuildThread.start()
        logger.info(s"new document-index-rebuild thread [$indexRebuildThread] has been started")
      }
    }
  }


  /**
   * Creates and starts new index-update-thread if there is no already running index-update or index-rebuild thread.
   * Any exception or an interruption terminates index-update-thread.
   *
   * As the final action, index-update-thread submits start of a new index-update-thread .
   */
  private def startNewIndexUpdateThread(): Unit = lock.synchronized {
    logger.info("attempting to start new document-index-update thread.")

    (shutdownRef.get, indexWriteFailureRef.get, indexRebuildThreadRef.get, indexUpdateThreadRef.get) match {
      case (shutdown@true, _, _, _) =>
        logger.error("new document-index-update thread can not be started - service is shut down.")

      case (_, indexWriteFailure, _, _) if indexWriteFailure != null =>
        logger.error(s"new document-index-update thread can not be started - previous index write attempt has failed [$indexWriteFailure].")

      case (_, _, indexRebuildThread, _) if Threads.notTerminated(indexRebuildThread) =>
        logger.info(s"new document-index-update thread can not be started while document-index-rebuild thread [$indexRebuildThread] is running.")

      case (_, _, _, indexUpdateThread) if Threads.notTerminated(indexUpdateThread) =>
        logger.info(s"new document-index-update thread can not be started - document-index-update thread [$indexUpdateThread] is already running.")

      case _ =>
        new Thread { indexUpdateThread =>
          override def run() {
            try {
              while (true) {
                indexUpdateRequests.take() match {
                  case AddDocToIndex(docId) => serviceOps.addDocsToIndex(solrServerWriter, docId)
                  case DeleteDocFromIndex(docId) => serviceOps.deleteDocsFromIndex(solrServerWriter, docId)
                }
              }
            } catch {
              case _: InterruptedException =>
                logger.debug(s"document-index-update thread [$indexUpdateThread] was interrupted")

              case NonFatal(e) =>
                val writeFailure = new ServiceFailure(ManagedDocumentIndexService.this, e, ServiceFailure.Type.UPDATE)
                logger.error(s"error in document-index-update thread [$indexUpdateThread].", e)
                indexWriteFailureRef.set(writeFailure)
                Threads.spawnDaemon {
                  failureHandler(writeFailure)
                }
            } finally {
              logger.info(s"document-index-update thread [$indexUpdateThread] is about to terminate.")
            }
          }
        } |>> indexUpdateThreadRef.set |>> { indexUpdateThread =>
          indexUpdateThread.setDaemon(true)
          indexUpdateThread.setName(s"document-index-update-${indexUpdateThread.getId}")
          indexUpdateThread.start()
          logger.info(s"new document-index-update thread [$indexUpdateThread] has been started")
        }
    }
  }


  private def interruptIndexUpdateThreadAndAwaitTermination() {
    Threads.interruptAndAwaitTermination(indexUpdateThreadRef.get)
  }


  private def interruptIndexRebuildThreadAndAwaitTermination() {
    Threads.interruptAndAwaitTermination(indexRebuildThreadRef.get)
  }


  override def update(request: IndexUpdateOp) {
    Threads.spawnDaemon {
      lock.synchronized {
        if (!shutdownRef.get()) {
          if (indexUpdateRequests.offer(request)) {
            startNewIndexUpdateThread()
          } else {
            logger.error(s"Can't submit index update request [$request], requests query is full.")
            // ??? handle ???
            // serviceErrorHandler(IndexUpdateQueryFull)
          }
        }
      }
    }
  }


  override def query(solrQuery: SolrQuery): Try[QueryResponse] = Try(serviceOps.query(solrServerReader, solrQuery)) |>> {
    case _: Success[_] =>
    case Failure(e) =>
      logger.error(s"Search error. solrQuery: $solrQuery", e)
      Threads.spawnDaemon {
        failureHandler(new ServiceFailure(ManagedDocumentIndexService.this, e, ServiceFailure.Type.SEARCH))
      }
  }


  override def shutdown(): Unit = lock.synchronized {
    if (shutdownRef.compareAndSet(false, true)) {
      logger.info("Attempting to shut down the service.")
      try {
        interruptIndexUpdateThreadAndAwaitTermination()
        interruptIndexRebuildThreadAndAwaitTermination()

        try {
          solrServerReader.shutdown()
        } catch {
          case e: Exception => logger.warn("An error occurred while shutting down SolrServer reader.", e)
        }

        try {
          solrServerWriter.shutdown()
        } catch {
          case e: Exception => logger.warn("An error occurred while shutting down SolrServer writer.", e)
        }

        logger.info("Service has been shut down.")
      } catch {
        case e: Exception =>
          logger.warn("An error occurred while shutting down the service.", e)
          throw e
      }
    }
  }


  override def currentRebuildTaskOpt(): Option[IndexRebuildTask] = indexRebuildTaskRef.get.asOption
}