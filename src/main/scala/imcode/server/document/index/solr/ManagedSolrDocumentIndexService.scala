package imcode.server.document.index.solr

import scala.actors.Actor._
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{Executors, Future => JFuture, LinkedBlockingQueue}
import imcode.server.user.UserDomainObject
import com.imcode._
import imcode.server.document.DocumentDomainObject
import org.apache.solr.client.solrj.{SolrServer, SolrQuery}

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

  private val indexRebuildTaskRef = new AtomicReference[JFuture[_]]
  private val indexUpdateTaskRef = new AtomicReference[JFuture[_]]
  private val executorService = Executors.newFixedThreadPool(2)

  def requestIndexUpdate(op: SolrDocumentIndexService.IndexUpdateOp) {
    indexUpdateOpsRegistrator ! op
  }

  def requestIndexRebuild(): Unit = indexRebuildTaskRef.synchronized {
    indexRebuildTaskRef.get() match {
      case task if !(task == null || task.isDone) => task

      case _ =>
        executorService.submit(new Runnable {
          def run() {
            try {
              cancelIndexUpdateTask()
              ops.rebuildIndex(solrServerWriter, null) |> { indexRebuild =>
                // publish indexRebuild
                indexRebuild.task.get()
              }
            } catch {
              case e: InterruptedException =>
                Thread.currentThread().interrupt()
                throw e

              case e =>
                publish(EmbeddedSolrDocumentIndexService.IndexError(ManagedSolrDocumentIndexService.this, e))
                throw e
            } finally {
              startIndexUpdateTask()
            }
          }
        }) |>> indexRebuildTaskRef.set
    }
  }

  private def startIndexUpdateTask() {
    executorService.submit(new Runnable {
      def run() {
        while (!Thread.currentThread().isInterrupted) {
          try {
            indexUpdateOps.poll() match {
              case SolrDocumentIndexService.AddDocsToIndex(docId) => ops.addDocsToIndex(solrServerWriter, docId)
              case SolrDocumentIndexService.DeleteDocsFromIndex(docId) => ops.deleteDocsFromIndex(solrServerWriter, docId)
            }
          } catch {
            case e: InterruptedException =>
              Thread.currentThread().interrupt()
              throw e

            case e =>
              publish(EmbeddedSolrDocumentIndexService.IndexError(ManagedSolrDocumentIndexService.this, e))
              throw e
          }
        }
      }
    })
  }

  private def cancelIndexUpdateTask() {
    indexUpdateTaskRef.get() |> { task =>
      if (task != null && !task.isDone) {
        task.cancel(true)
        scala.util.control.Exception.allCatch.opt {
          task.get()
        }
      }
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


  def start() {
    indexUpdateOpsRegistrator.start()
    startIndexUpdateTask()
  }


  def shutdown() {
    solrServerReader.shutdown()
    solrServerWriter.shutdown()
    executorService.shutdownNow()
    // todo: stop actor
  }
}