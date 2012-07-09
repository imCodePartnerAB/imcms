package imcode.server.document.index.solr

import scala.actors.Actor._
import java.util.concurrent.{LinkedBlockingQueue}
import imcode.server.user.UserDomainObject
import com.imcode._
import imcode.server.document.DocumentDomainObject
import org.apache.solr.client.solrj.{SolrServer, SolrQuery}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import java.lang.{InterruptedException, IllegalStateException, Thread}

/**
 *
 */
class ManagedSolrDocumentIndexService(
    solrServerReader: SolrServer with SolrServerShutdown,
    solrServerWriter: SolrServer with SolrServerShutdown,
    ops: SolrDocumentIndexServiceOps) extends SolrDocumentIndexService {

  private val indexUpdateOps = new LinkedBlockingQueue[SolrDocumentIndexService.IndexUpdateOp]//(1000)
  private val indexUpdateOpsRegistrator = actor {
    react {
      // add DeleteXXX to the end of queue as they are more lightweight
      // FATAL ERROR
      case op: SolrDocumentIndexService.IndexUpdateOp =>
        if (!indexUpdateOps.offer(op)) {
          // log events query is full, unable to process
          // request reindex
        }

      case _ =>
    }
  }

  private val shutdownRef = new AtomicBoolean(false)
  private val indexWriteLock = new AnyRef // mutex/sem
  private val indexRebuildThreadRef = new AtomicReference[Thread]
  private val indexUpdateThreadRef = new AtomicReference[Thread]


  def requestIndexUpdate(op: SolrDocumentIndexService.IndexUpdateOp) {
    indexUpdateOpsRegistrator ! op
  }


  def requestIndexRebuild() {
    new Thread {
      override def run() {
        shutdownRef.synchronized {
          shutdownRef.get() match {
            case true =>
            case _ => startIndexRebuildThread()
          }
        }
      }
    }.start()
  }

  // todo: publish task as a part of thread execution
  private def startIndexRebuildThread(): Unit = indexWriteLock.synchronized {
    PartialFunction.condOpt(indexRebuildThreadRef.get()) {
      case thread if thread == null || thread.getState == Thread.State.TERMINATED =>
        new Thread {
          override def run() {
            try {
              stopIndexUpdateThread()

              ops.rebuildIndexInterruptibly(solrServerWriter) { _ =>
                // update progress
              }

              startIndexUpdateThread()
            } catch {
              case e: InterruptedException =>
                // startIndexUpdateTask()
                //Thread.currentThread().interrupt()
                //throw e

              case e =>
                publish(EmbeddedSolrDocumentIndexService.IndexError(ManagedSolrDocumentIndexService.this, e))
            }
          }
        } |>> indexRebuildThreadRef.set |>> { _.start() }
    }
  }


  private def stopIndexRebuildThread(): Unit = indexWriteLock.synchronized {
    for (thread <- Option(indexRebuildThreadRef.get())) {
      thread.interrupt()
      thread.join()
    }
  }


  private def startIndexUpdateThread(): Unit = indexWriteLock.synchronized {
    new Thread {
      override def run() {
        while (!isInterrupted()) {
          try {
            indexUpdateOps.poll() match {
              case SolrDocumentIndexService.AddDocsToIndex(docId) => ops.addDocsToIndex(solrServerWriter, docId)
              case SolrDocumentIndexService.DeleteDocsFromIndex(docId) => ops.deleteDocsFromIndex(solrServerWriter, docId)
            }
          } catch {
            case e: InterruptedException =>

            case e =>
              publish(EmbeddedSolrDocumentIndexService.IndexError(ManagedSolrDocumentIndexService.this, e))
          }
        }
      }
    } |>> { _.start() } |>> indexUpdateThreadRef.set
  }


  private def stopIndexUpdateThread(): Unit = indexWriteLock.synchronized {
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


  def shutdown() {
    shutdownRef.synchronized {
      if (shutdownRef.compareAndSet(false, true)) {

        solrServerReader.shutdown()
        solrServerWriter.shutdown()
          // todo: stop actor
      }
    }
  }
}