package imcode.server.document.index.solr

import scala.actors.Actor._
import java.util.concurrent.{LinkedBlockingQueue}
import imcode.server.user.UserDomainObject
import com.imcode._
import imcode.server.document.DocumentDomainObject
import org.apache.solr.client.solrj.{SolrServer, SolrQuery}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import java.lang.{Thread, InterruptedException, IllegalStateException}

/**
 * Coordinates index update and rebuild requests.
 *
 * Makes sure that update and rebuild operations are never run in parallel.
 */
class ManagedSolrDocumentIndexService(
    solrServerReader: SolrServer with SolrServerShutdown,
    solrServerWriter: SolrServer with SolrServerShutdown,
    ops: SolrDocumentIndexServiceOps) extends SolrDocumentIndexService {

  private val lock = new AnyRef
  private val shutdownRef = new AtomicBoolean(false)
  private val indexRebuildThreadRef = new AtomicReference[Thread]
  private val indexUpdateThreadRef = new AtomicReference[Thread]
  private val indexUpdateOps = new LinkedBlockingQueue[SolrDocumentIndexService.IndexUpdateOp]//(1000)

  private val requestsHandler = actor {
    react {
      // add DeleteXXX to the end of queue as they are more lightweight
      // FATAL ERROR
      case op: SolrDocumentIndexService.IndexUpdateOp =>
        if (!indexUpdateOps.offer(op)) {
          // log events query is full, unable to process
          // request reindex
        }

      case 'rebuild => startIndexRebuildThread()

      case _ =>
    }
  }


  private def notTerminated(thread: Thread) = thread != null && thread.getState != Thread.State.TERMINATED
  private def interruptAndAwaitTermination(thread: Thread) {
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


  def requestIndexUpdate(op: SolrDocumentIndexService.IndexUpdateOp) {
    requestsHandler ! op
  }


  def requestIndexRebuild() {
    requestsHandler ! 'rebuild
  }

  /**
   * Creates and starts new index-rebuild-thread if there is no already running one.
   *
   * An existing index-update-thread is stopped before rebuilding happens and a new index-update-thread is started
   * as soon as running index-rebuild-thread is terminated.
   *
   * Any exception terminates index-rebuild-thread.
   */
  // todo: publish task as a part of thread execution
  private def startIndexRebuildThread(): Unit = lock.synchronized {
    (shutdownRef.get(), indexRebuildThreadRef.get()) match {
      case (true, _) =>
      case (_, existingIndexRebuildThread) if notTerminated(existingIndexRebuildThread) =>
      case _ =>
        new Thread { currentIndexRebuildThread =>
          setName("solr-document-index-rebuild-" + System.nanoTime())

          override def run() {
            stopIndexUpdateThread()
            indexUpdateOps.clear()

            try {
              ops.rebuildIndexInterruptibly(solrServerWriter) { _ =>
                // update progress
              }
            } catch {
              case e: InterruptedException =>

              case e =>
                publish(EmbeddedSolrDocumentIndexService.IndexError(ManagedSolrDocumentIndexService.this, e))
                // publish index rebuild error
            } finally {
              new Thread {
                override def run() {
                  currentIndexRebuildThread.join()
                  startIndexUpdateThread()
                }
              }.start()
            }
          }
        } |>> indexRebuildThreadRef.set |>> { _.start() }
    }
  }


  private def stopIndexRebuildThread(): Unit = lock.synchronized {
    for (thread <- Option(indexRebuildThreadRef.get())) {
      thread.interrupt()
      thread.join()
    }
  }


  /**
   * Creates and starts new index-update-thread if there is no already running index-update or index-rebuild thread.
   *
   * Any exception or interruption terminates index-update-thread.
   * However, as its final action, index-update-thread submits start of a new index-update-thread .
   */
  private def startIndexUpdateThread(): Unit = lock.synchronized {
    (shutdownRef.get(), indexRebuildThreadRef.get(), indexUpdateThreadRef.get()) match {
      case (true, _, _) => // terminated
      case (_, existingIndexRebuildThread, _) if notTerminated(existingIndexRebuildThread) =>
      case (_, _, existingIndexUpdateThread) if notTerminated(existingIndexUpdateThread) =>
      case _ =>
        new Thread { currentIndexUpdateThread =>
          setName("solr-document-index-update-" + System.nanoTime())

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

              case e =>
                publish(EmbeddedSolrDocumentIndexService.IndexError(ManagedSolrDocumentIndexService.this, e))
                // publish index update error
            } finally {
              new Thread {
                override def run() {
                  currentIndexUpdateThread.join()
                  startIndexUpdateThread()
                }
              }.start()
            }
          }
        } |>> indexUpdateThreadRef.set |>> { _.start() }
    }
  }


  private def stopIndexUpdateThread(): Unit = lock.synchronized {
    for (thread <- Option(indexUpdateThreadRef.get())) {
      thread.interrupt()
      thread.join()
    }
  }


  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
    try {
      val queryResponse = solrServerReader.query(new SolrQuery(query.toString))

      java.util.Collections.emptyList()
    } catch {
      case e =>
        logger.error("Search error", e)
        publish(EmbeddedSolrDocumentIndexService.IndexError(ManagedSolrDocumentIndexService.this, e))
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


  def shutdown(): Unit = lock.synchronized {
    if (shutdownRef.compareAndSet(false, true)) {
      // stop worker threads
      solrServerReader.shutdown()
      solrServerWriter.shutdown()
        // todo: stop actor
    }
  }
}