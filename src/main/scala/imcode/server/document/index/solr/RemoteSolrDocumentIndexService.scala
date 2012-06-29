package imcode.server.document.index.solr

import scala.actors.Actor
import scala.actors.Actor._
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{Executors, Future =>JFuture, LinkedBlockingQueue}
import imcode.server.user.UserDomainObject
import com.imcode._
import imcode.server.document.DocumentDomainObject
import org.apache.solr.client.solrj.SolrQuery
import imcode.server.document.index.DocumentQuery

// solrUrl: String, solrReadOnlyUrl: String
class RemoteSolrDocumentIndexService(solrUrl: String, ops: SolrDocumentIndexServiceOps) extends SolrDocumentIndexService {
  private val solrServerReader = SolrServerFactory.createHttpSolrServer(solrUrl)
  private val solrServerWriter = SolrServerFactory.createHttpSolrServer(solrUrl)

  private val events = new LinkedBlockingQueue[SolrDocumentIndexService.AlterIndexRequest]//(1000)
  private val eventsDispatcher = actor {
    react {
      // add DeleteXXX to the end of queue
      case event: SolrDocumentIndexService.AlterIndexRequest =>
        if (!events.offer(event)) {
          // log events query is full, unable to process
          // request reindex
        }

      case _ =>
    }
  }

  private val reindexTaskRef = new AtomicReference[JFuture[_]]
  private val eventHandlerTaskRef = new AtomicReference[JFuture[_]]
  private val executorService = Executors.newFixedThreadPool(2)

  def requestAlterIndex(event: SolrDocumentIndexService.AlterIndexRequest) { eventsDispatcher ! event}

  def requestRebuildIndex(): JFuture[_] = reindexTaskRef.synchronized {
    reindexTaskRef.get() match {
      case task if !(task == null || task.isDone) => task

      case _ =>
        executorService.submit(new Runnable {
          def run() {
            try {
              stopEventHandling()
              ops.rebuildIndex(solrServerWriter)
            } finally {
              startEventHandling()
            }
          }
        }) |>> reindexTaskRef.set
    }
  }

  def startEventHandling() {
    executorService.submit(new Runnable {
      def run() {
        while (!Thread.currentThread().isInterrupted) {
          try {
            events.poll() match {
              case SolrDocumentIndexService.AddDocToIndex(doc) => ops.addDocToIndex(solrServerWriter, doc)
              case SolrDocumentIndexService.AddDocsToIndex(docId) => ops.addDocsToIndex(solrServerWriter, docId)
              case SolrDocumentIndexService.DeleteDocFromIndex(doc) => ops.deleteDocFromIndex(solrServerWriter, doc)
              case SolrDocumentIndexService.DeleteDocsFromIndex(docId) => ops.deleteDocsFromIndex(solrServerWriter, docId)
            }
          } catch {
            case e: InterruptedException => Thread.currentThread().interrupt()
          }
        }
      }
    })
  }

  def stopEventHandling() {
    eventHandlerTaskRef.get() |> { task =>
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
        java.util.Collections.emptyList()
      }
  }

  def shutdown() {
    solrServerReader.shutdown()
    solrServerWriter.shutdown()
  }
}