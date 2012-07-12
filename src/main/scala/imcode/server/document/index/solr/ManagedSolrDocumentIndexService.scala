package imcode.server.document.index.solr

import java.util.concurrent.{LinkedBlockingQueue}
import imcode.server.user.UserDomainObject
import com.imcode._
import imcode.server.document.DocumentDomainObject
import org.apache.solr.client.solrj.{SolrServer, SolrQuery}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import java.lang.{Thread, InterruptedException}
import scala.swing.Publisher
import scala.swing.event.Event

/**
 * Implements all SolrDocumentIndexService functionality.
 * Coordinates index update and rebuild requests.
 * Ensures that update and rebuild operations are never run in parallel.
 *
 * Indexing (ops) errors are wrapped and published asynchronously as ManagedSolrDocumentIndexService.IndexError events.
 *
 * The business-logic, (like rebuild scheduling or index recovery) should be implemented on higher levels.
 */
class ManagedSolrDocumentIndexService(
    solrServerReader: SolrServer with SolrServerShutdown,
    solrServerWriter: SolrServer with SolrServerShutdown,
    ops: SolrDocumentIndexServiceOps) extends SolrDocumentIndexService with Publisher {

  private val lock = new AnyRef
  private val shutdownRef = new AtomicBoolean(false)
  private val indexRebuildThreadRef = new AtomicReference[Thread]
  private val indexUpdateThreadRef = new AtomicReference[Thread]
  private val indexUpdateOps = new LinkedBlockingQueue[SolrDocumentIndexService.IndexUpdateOp]//(1000)

  private def spawnDaemonThread(body: => Any): Thread =
    new Thread {
      setDaemon(true)
      override def run() {
        body
      }
    } |>> { _.start() }

  private def notTerminated(thread: Thread) = thread != null && thread.getState != Thread.State.TERMINATED
  private def interruptThreadAndAwaitTermination(thread: Thread) {
    if (thread != null) {
      thread.interrupt()
      try {
        thread.join()
      } catch {
        case e: InterruptedException =>
          Thread.currentThread().interrupt()
          throw e
      }
    }
  }


  /**
   * Creates and starts new index-rebuild-thread if there is no already running one.
   * Any exception or an interruption terminates index-rebuild-thread.
   *
   * An existing index-update-thread is stopped before rebuilding happens and a new index-update-thread is started
   * as soon as running index-rebuild-thread is terminated.
   *
   */
  // todo: ??? publish task as a part of thread execution ???
  private def startIndexRebuildThread(): Unit = lock.synchronized {
    logger.info("attempting to start new document-index-rebuild thread.")

    (shutdownRef.get(), indexRebuildThreadRef.get()) match {
      case (true, _) =>
        logger.info("new document-index-rebuild thread can not be started - service is shut down.")

      case (_, indexRebuildThread) if notTerminated(indexRebuildThread) =>
        logger.info("new document-index-rebuild thread can not be started - document-index-rebuild thread [%s] is allready running."
          .format(indexRebuildThread))

      case _ =>
        new Thread { indexRebuildThread =>
          setName("document-index-rebuild-" + getId)

          override def run() {
            stopIndexUpdateThreadAndAwaitTermination()
            indexUpdateOps.clear()

            try {
              ops.rebuildIndexInterruptibly(solrServerWriter) { _ =>
                // update progress
              }
            } catch {
              case _: InterruptedException =>
                logger.trace("document-index-rebuild thread [%s] has been interrupted".format(indexRebuildThread))

              case e =>
                logger.error("error in document-index-rebuild thread [%s].".format(indexRebuildThread), e)
                spawnDaemonThread {
                  publish(ManagedSolrDocumentIndexService.IndexError(ManagedSolrDocumentIndexService.this, e))
                  // publish index *rebuild* error ???
                }
            } finally {
              spawnDaemonThread {
                indexRebuildThread.join()
                startIndexUpdateThread()
              }

              logger.info("document-index-rebuild thread [%s] is about to terminate.".format(indexRebuildThread))
            }
          }
        } |>> indexRebuildThreadRef.set |>> { indexRebuildThread =>
          indexRebuildThread.start()
          logger.info("new document-index-rebuild thread [%s] has been started".format(indexRebuildThread))
        }
    }
  }


  /**
   * Creates and starts new index-update-thread if there is no already running index-update or index-rebuild thread.
   * Any exception or an interruption terminates index-update-thread.

   * As the final action, index-update-thread submits start of a new index-update-thread .
   */
  private def startIndexUpdateThread(): Unit = lock.synchronized {
    logger.info("attempting to start new document-index-update thread.")

    (shutdownRef.get(), indexRebuildThreadRef.get(), indexUpdateThreadRef.get()) match {
      case (true, _, _) =>
        logger.info("new document-index-update thread can not be started - service is shut down.")

      case (_, indexRebuildThread, _) if notTerminated(indexRebuildThread) =>
        logger.info("new document-index-update thread can not be started while document-index-rebuild thread [%s] is running."
          .format(indexRebuildThread))

      case (_, _, indexUpdateThread) if notTerminated(indexUpdateThread) =>
        logger.info("new document-index-update thread can not be started - document-index-update thread [%s] is allready running."
          .format(indexUpdateThread))

      case _ =>
        new Thread { indexUpdateThread =>
          setName("document-index-update-" + getId)

          override def run() {
            try {
              while (!isInterrupted()) {
                indexUpdateOps.take() match {
                  case SolrDocumentIndexService.AddDocsToIndex(docId) => ops.addDocsToIndex(solrServerWriter, docId)
                  case SolrDocumentIndexService.DeleteDocsFromIndex(docId) => ops.deleteDocsFromIndex(solrServerWriter, docId)
                }
              }
            } catch {
              case e: InterruptedException =>
                logger.trace("document-index-update thread [%s] has been interrupted".format(indexUpdateThread))

              case e =>
                logger.error("error in document-index-update thread [%s].".format(indexUpdateThread), e)
                spawnDaemonThread {
                  publish(ManagedSolrDocumentIndexService.IndexError(ManagedSolrDocumentIndexService.this, e))
                  // publish index update error
                }
            } finally {
              spawnDaemonThread {
                indexUpdateThread.join()
                startIndexUpdateThread()
              }

              logger.info("document-index-update thread [%s] is about to terminate.".format(indexUpdateThread))
            }
          }
        } |>> indexUpdateThreadRef.set |>> { indexUpdateThread =>
          indexUpdateThread.start()
          logger.info("new document-index-update thread [%s] has been started".format(indexUpdateThread))
        }
    }
  }


  private def stopIndexUpdateThreadAndAwaitTermination(): Unit = lock.synchronized {
    interruptThreadAndAwaitTermination(indexUpdateThreadRef.get())
  }


  private def stopIndexRebuildThreadAndAwaitTermination(): Unit = lock.synchronized {
    interruptThreadAndAwaitTermination(indexRebuildThreadRef.get())
  }


  def requestIndexUpdate(op: SolrDocumentIndexService.IndexUpdateOp) {
    spawnDaemonThread {
      if (!indexUpdateOps.offer(op)) {
        // log events query is full, unable to process
      }
    }
  }


  def requestIndexRebuild() {
    spawnDaemonThread {
      startIndexRebuildThread()
    }
  }


  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
    try {
      val queryResponse = solrServerReader.query(new SolrQuery(query.toString))

      java.util.Collections.emptyList()
    } catch {
      case e =>
        logger.error("Search error", e)
        // *search* error
        publish(ManagedSolrDocumentIndexService.IndexError(ManagedSolrDocumentIndexService.this, e))
        java.util.Collections.emptyList()
    }
  }


//  def start(): Unit = shutdownRef.synchronized {
//    shutdownRef.get() match {
//      case true => throw new IllegalStateException()
//      case _ =>
//        // check shutdown
//        indexUpdateOpsRegistrator.start()
//        startIndexUpdateThread()
//    }
//  }

  // move to index document and get rid of implicit start???
  startIndexUpdateThread()

  def shutdown(): Unit = lock.synchronized {
    if (shutdownRef.compareAndSet(false, true)) {
      logger.info("attempting to shut down the service.")
      try {
        stopIndexUpdateThreadAndAwaitTermination()
        stopIndexRebuildThreadAndAwaitTermination()

        solrServerReader.shutdown()
        solrServerWriter.shutdown()
        logger.info("service has been shut down.")
      } catch {
        case e =>
          logger.error("an error occured while shutting down the service.", e)
          throw e
      }
    }
  }
}


object ManagedSolrDocumentIndexService {
  case class IndexError(publisher: SolrDocumentIndexService, error: Throwable) extends Event
}