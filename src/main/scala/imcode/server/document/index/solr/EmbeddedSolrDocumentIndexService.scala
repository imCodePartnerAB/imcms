package imcode.server.document.index.solr

import java.io.File
import scala.actors.Actor
import scala.actors.Actor._
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{Executors, Future => JFuture, LinkedBlockingQueue}
import imcode.server.user.UserDomainObject
import com.imcode._
import imcode.server.document.DocumentDomainObject
import org.apache.solr.client.solrj.SolrQuery

class EmbeddedSolrDocumentIndexService(solrHome: File) extends SolrDocumentIndexService {
  private val solrServerReader = SolrServerFactory.createEmbeddedSolrServer(solrHome)
  private val solrServerWriter = SolrServerFactory.createEmbeddedSolrServer(solrHome)

  private val events = new LinkedBlockingQueue[AlterRequest]//(1000)
  private val eventsDispatcher = actor {
    react {
      // add DeleteXXX to the end of queue
      case event: AlterRequest =>
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

  def requestAlter(event: AlterRequest) { eventsDispatcher ! event}

  def requestRebuild(): JFuture[_] = reindexTaskRef.synchronized {
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
              case AddDocToIndex(doc) => ops.addDocToIndex(solrServerWriter, doc)
              case AddDocsToIndex(docId) => ops.addDocsToIndex(solrServerWriter, docId)
              case DeleteDocFromIndex(doc) => ops.deleteDocFromIndex(solrServerWriter, doc)
              case DeleteDocsFromIndex(docId) => ops.deleteDocsFromIndex(solrServerWriter, docId)
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

