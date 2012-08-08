package imcode.server.document.index.solr

import _root_.com.imcode._
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import org.apache.solr.client.solrj.{SolrServer}
import java.lang.{InterruptedException, Thread}
import org.apache.solr.common.params.SolrParams
import java.util.concurrent.atomic.{AtomicReference, AtomicBoolean}
import imcode.server.document.index.solr.SolrDocumentIndexService.IndexRebuildTask
import java.util.concurrent._

/**
 * Implements all SolrDocumentIndexService functionality.
 * Ensures that update and rebuild never run concurrently.
 *
 * Indexing (ops) errors are wrapped and published asynchronously as ManagedSolrDocumentIndexService.IndexError events.
 * On index write failure (either update or rebuild) the service stops processing index write requests.
 *
 * The business-logic, (like rebuild scheduling or index recovery) should be implemented on higher levels.
 */
class ManagedSolrDocumentIndexService(
    solrServerReader: SolrServer with SolrServerShutdown,
    solrServerWriter: SolrServer with SolrServerShutdown,
    serviceOps: SolrDocumentIndexServiceOps,
    serviceErrorHandler: ManagedSolrDocumentIndexService.ServiceError => Unit) extends SolrDocumentIndexService {

  private val lock = new AnyRef
  private val shutdownRef = new AtomicBoolean(false)
  private val indexRebuildThreadRef = new AtomicReference[Thread]
  private val indexUpdateThreadRef = new AtomicReference[Thread]
  private val indexUpdateRequests = new LinkedBlockingQueue[SolrDocumentIndexService.IndexUpdateRequest](1000)
  private val indexWriteErrorRef = new AtomicReference[ManagedSolrDocumentIndexService.IndexWriteError]
  private val indexRebuildTaskRef = new AtomicReference[SolrDocumentIndexService.IndexRebuildTask]


  /**
   * Creates and starts new index-rebuild-thread if there is no already running one.
   * Any exception or an interruption terminates index-rebuild-thread.
   *
   * An existing index-update-thread is stopped before rebuilding happens and a new index-update-thread is started
   * immediately after running index-rebuild-thread is terminated without errors.
   */
  def requestIndexRebuild(): Option[SolrDocumentIndexService.IndexRebuildTask] = lock.synchronized {
    logger.info("attempting to start new document-index-rebuild thread.")

    (shutdownRef.get(), indexWriteErrorRef.get(), indexRebuildThreadRef.get(), indexRebuildTask()) match {
      case (shutdown@true, _, _, currentIndexRebuildTask) =>
        logger.info("new document-index-rebuild thread can not be started - service is shut down.")
        currentIndexRebuildTask

      case (_, indexWriteError, _, currentIndexRebuildTask) if indexWriteError != null =>
        logger.info("new document-index-rebuild thread can not be started - previous index write attempt has failed with error [%s]."
          .format(indexWriteError))
        currentIndexRebuildTask

      case (_, _, indexRebuildThread, currentIndexRebuildTask) if Threads.notTerminated(indexRebuildThread) =>
        logger.info("new document-index-rebuild thread can not be started - document-index-rebuild thread [%s] is allready running."
          .format(indexRebuildThread))
        currentIndexRebuildTask

      case _ =>
        new IndexRebuildTask {
          val progressRef = new AtomicReference[SolrDocumentIndexService.IndexRebuildProgress]
          val futureTask = new FutureTask[Unit](Threads.mkCallable {
            serviceOps.rebuildIndex(solrServerWriter) { progress =>
              progressRef.set(progress)
            }
          })

          def progress(): Option[SolrDocumentIndexService.IndexRebuildProgress] = Option(progressRef.get())

          def future(): Future[_] = futureTask
        } |>> indexRebuildTaskRef.set |>> { indexRebuildTaskImpl =>
          new Thread { indexRebuildThread =>
            override def run() {
              try {
                interruptIndexUpdateThreadAndAwaitTermination()
                indexUpdateRequests.clear()
                indexRebuildTaskImpl.futureTask.run()
                if (!indexRebuildTaskImpl.futureTask.isCancelled) {
                  try {
                    indexRebuildTaskImpl.futureTask.get()
                  } catch {
                    case e: ExecutionException => throw e.getCause
                    case e => throw e
                  }
                }

                Threads.spawnDaemon {
                  indexRebuildThread.join()
                  startNewIndexUpdateThread()
                }
              } catch {
                case _: InterruptedException =>
                  logger.trace("document-index-rebuild thread [%s] was interrupted".format(indexRebuildThread))
                  Threads.spawnDaemon {
                    indexRebuildThread.join()
                    startNewIndexUpdateThread()
                  }

                case e =>
                  val writeError = ManagedSolrDocumentIndexService.IndexRebuildError(ManagedSolrDocumentIndexService.this, e)
                  logger.error("Error in document-index-rebuild thread [%s].".format(indexRebuildThread), e)
                  indexWriteErrorRef.set(writeError)
                  Threads.spawnDaemon {
                    serviceErrorHandler(writeError)
                  }
              } finally {
                logger.info("document-index-rebuild thread [%s] is about to terminate.".format(indexRebuildThread))
              }
            }
          } |>> indexRebuildThreadRef.set |>> { indexRebuildThread =>
            indexRebuildThread.setName("document-index-rebuild-" + indexRebuildThread.getId)
            indexRebuildThread.start()
            logger.info("new document-index-rebuild thread [%s] has been started".format(indexRebuildThread))
          }
        } |> opt
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

    (shutdownRef.get(), indexWriteErrorRef.get(), indexRebuildThreadRef.get(), indexUpdateThreadRef.get()) match {
      case (shutdown@true, _, _, _) =>
        logger.info("new document-index-update thread can not be started - service is shut down.")

      case (_, indexWriteError, _, _) if indexWriteError != null =>
        logger.info("new document-index-update thread can not be started - previous index write attempt has failed [%s]."
          .format(indexWriteError))

      case (_, _, indexRebuildThread, _) if Threads.notTerminated(indexRebuildThread) =>
        logger.info("new document-index-update thread can not be started while document-index-rebuild thread [%s] is running."
          .format(indexRebuildThread))

      case (_, _, _, indexUpdateThread) if Threads.notTerminated(indexUpdateThread) =>
        logger.info("new document-index-update thread can not be started - document-index-update thread [%s] is allready running."
          .format(indexUpdateThread))

      case _ =>
        new Thread { indexUpdateThread =>
          override def run() {
            try {
              while (true) {
                indexUpdateRequests.take() match {
                  case SolrDocumentIndexService.AddDocsToIndex(docId) => serviceOps.addDocsToIndex(solrServerWriter, docId)
                  case SolrDocumentIndexService.DeleteDocsFromIndex(docId) => serviceOps.deleteDocsFromIndex(solrServerWriter, docId)
                }
              }
            } catch {
              case e: InterruptedException =>
                logger.trace("document-index-update thread [%s] was interrupted".format(indexUpdateThread))

              case e =>
                val writeError = ManagedSolrDocumentIndexService.IndexUpdateError(ManagedSolrDocumentIndexService.this, e)
                logger.error("error in document-index-update thread [%s].".format(indexUpdateThread), e)
                indexWriteErrorRef.set(writeError)
                Threads.spawnDaemon {
                  serviceErrorHandler(writeError)
                }
            } finally {
              logger.info("document-index-update thread [%s] is about to terminate.".format(indexUpdateThread))
            }
          }
        } |>> indexUpdateThreadRef.set |>> { indexUpdateThread =>
          indexUpdateThread.setName("document-index-update-" + indexUpdateThread.getId)
          indexUpdateThread.start()
          logger.info("new document-index-update thread [%s] has been started".format(indexUpdateThread))
        }
    }
  }


  private def interruptIndexUpdateThreadAndAwaitTermination() {
    Threads.interruptAndAwaitTermination(indexUpdateThreadRef.get())
  }


  private def interruptIndexRebuildThreadAndAwaitTermination() {
    Threads.interruptAndAwaitTermination(indexRebuildThreadRef.get())
  }


  def requestIndexUpdate(request: SolrDocumentIndexService.IndexUpdateRequest) {
    Threads.spawnDaemon {
      shutdownRef.get() match {
        case true =>
          logger.warn("Can't submit index update request [%s], server is shut down.".format(request))

        case _ =>
          // publish query is full???
          if (indexUpdateRequests.offer(request)) startNewIndexUpdateThread()
          else logger.error("Can't submit index update request [s], requests query is full.".format(request))
      }
    }
  }


  def search(solrParams: SolrParams, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
    try {
      serviceOps.search(solrServerReader, solrParams, searchingUser)
    } catch {
      case e =>
        logger.error("Search error. solrParams: %s, searchingUser: %s".format(solrParams, searchingUser), e)
        Threads.spawnDaemon {
          serviceErrorHandler(ManagedSolrDocumentIndexService.IndexSearchError(ManagedSolrDocumentIndexService.this, e))
        }
        java.util.Collections.emptyList()
    }
  }


  def shutdown(): Unit = lock.synchronized {
    if (shutdownRef.compareAndSet(false, true)) {
      logger.info("Attempting to shut down the service.")
      try {
        interruptIndexUpdateThreadAndAwaitTermination()
        interruptIndexRebuildThreadAndAwaitTermination()

        solrServerReader.shutdown()
        solrServerWriter.shutdown()
        logger.info("Service has been shut down.")
      } catch {
        case e =>
          logger.error("An error occured while shutting down the service.", e)
          throw e
      }
    }
  }

  def indexRebuildTask(): Option[SolrDocumentIndexService.IndexRebuildTask] = Option(indexRebuildTaskRef.get)
}


object ManagedSolrDocumentIndexService {
  sealed abstract class ServiceError {
    val service: SolrDocumentIndexService
    val error: Throwable
  }

  abstract class IndexWriteError extends ServiceError

  case class IndexUpdateError(service: SolrDocumentIndexService, error: Throwable) extends IndexWriteError
  case class IndexRebuildError(service: SolrDocumentIndexService, error: Throwable) extends IndexWriteError
  case class IndexSearchError(service: SolrDocumentIndexService, error: Throwable) extends ServiceError
}


object Threads {

  def mkThread(runBody: => Unit): Thread =
    new Thread {
      override def run() {
        runBody
      }
    }

  def mkRunnable(runBody: => Unit): Runnable =
    new Runnable {
      def run() { runBody }
    }

  def mkCallable[A](callBody: => A): Callable[A]  =
    new Callable[A] {
      def call(): A = callBody
    }

  def spawn(runBody: => Unit): Thread = mkThread(runBody) |>> { t => t.start() }
  def spawnDaemon(runBody: => Unit): Thread = mkThread(runBody) |>> { t => t.setDaemon(true); t.start() }

  def terminated(thread: Thread): Boolean = thread == null || thread.getState == Thread.State.TERMINATED
  def notTerminated(thread: Thread): Boolean = !terminated(thread)

  def interruptAndAwaitTermination(thread: Thread) {
    if (thread != null) {
      thread.interrupt()
      thread.join()
    }
  }
}