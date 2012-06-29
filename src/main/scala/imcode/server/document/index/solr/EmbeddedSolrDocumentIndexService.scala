package imcode.server.document.index.solr

import java.io.File
import scala.actors.Actor._
import java.util.concurrent.{Executors, Future => JFuture, LinkedBlockingQueue}
import imcode.server.user.UserDomainObject
import com.imcode._
import imcode.server.document.DocumentDomainObject
import org.apache.solr.client.solrj.SolrQuery
import java.util.concurrent.atomic.AtomicReference
import java.util.Collections
import scala.actors.{Actor}
import scala.swing.{Publisher, Reactor}
import scala.swing.event.Event


object NoOpSolrDocumentIndexService extends SolrDocumentIndexService {

  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = Collections.emptyList()

  def requestAlterIndex(request: SolrDocumentIndexService.AlterIndexRequest) {}
                                               // class - return real future?
  def requestRebuildIndex(): JFuture[_] = null // ??? IndexRebuild { def task(): Option[JFuture]; def }

  def shutdown() {}
}

/**
 * Delegates all invocations to the instance of EmbeddedSolrDocumentIndexService.
 * In case of an indexing error replaces target instance with new.
 */
class EmbeddedSolrDocumentIndexServiceProxy(solrHome: File, ops: SolrDocumentIndexServiceOps)
    extends SolrDocumentIndexService with Reactor {

  private val serviceRef: AtomicReference[SolrDocumentIndexService] = new AtomicReference(newService())

  reactions += {
    // swap target service
    case EmbeddedSolrDocumentIndexService.IndexError(publisher, error) =>
      serviceRef.synchronized {
        serviceRef.get() match {
          case service if service eq publisher =>
            serviceRef.set(NoOpSolrDocumentIndexService)
            deafTo(service)
            service.shutdown()
            // wait till shutdown
            serviceRef.set(newService(requestIndexRebuild = true))
          case _ =>
        }
      }
  }

  // todo: replace requestIndexRebuild flag with enum values
  private def newService(requestIndexRebuild: Boolean = false): EmbeddedSolrDocumentIndexService =
    new EmbeddedSolrDocumentIndexService(solrHome) |>> { service =>
      service.ops = ops
    } |>> { service =>
      listenTo(service)
      serviceRef.set(service)
      if (requestIndexRebuild) {
        service.requestRebuildIndex()
      }
    }

  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] =
    serviceRef.get().search(query, searchingUser)

  def requestAlterIndex(request: SolrDocumentIndexService.AlterIndexRequest) {
    serviceRef.get().requestAlterIndex(request)
  }

  def requestRebuildIndex(): JFuture[_] = serviceRef.get().requestRebuildIndex() // ??? IndexRebuild { def task(): Option[JFuture]; def }

  def shutdown() {
    serviceRef.get().shutdown()
  }
}


object EmbeddedSolrDocumentIndexService {
  case class IndexError(publisher: SolrDocumentIndexService, error: Throwable) extends Event
}


class EmbeddedSolrDocumentIndexService(solrHome: File, ops: SolrDocumentIndexServiceOps) extends SolrDocumentIndexService {
  private val solrServerReader = SolrServerFactory.createEmbeddedSolrServer(solrHome)
  private val solrServerWriter = SolrServerFactory.createEmbeddedSolrServer(solrHome)

  private val alterIndexRequests = new LinkedBlockingQueue[SolrDocumentIndexService.AlterIndexRequest]//(1000)
  private val alterIndexRequestsDispatcher = actor {
    react {
      // add DeleteXXX to the end of queue as they are more lightweight
      case request: SolrDocumentIndexService.AlterIndexRequest =>
        if (!alterIndexRequests.offer(request)) {
          // log events query is full, unable to process
          // request reindex
        }

      case _ =>
    }
  }

  private val reindexTaskRef = new AtomicReference[JFuture[_]]
  private val eventHandlerTaskRef = new AtomicReference[JFuture[_]]
  private val executorService = Executors.newFixedThreadPool(2)

  def requestAlterIndex(event: SolrDocumentIndexService.AlterIndexRequest) { alterIndexRequestsDispatcher ! event}

  def requestRebuildIndex(): JFuture[_] = reindexTaskRef.synchronized {
    reindexTaskRef.get() match {
      case task if !(task == null || task.isDone) => task

      case _ =>
        executorService.submit(new Runnable {
          def run() {
            try {
              stopAlterRequestsHandling()
              ops.rebuildIndex(solrServerWriter)
            } catch {
              case e =>
                throw e
                // repair: shutdown; recreate

            } finally {
              startAlterRequestHandling()
            }
          }
        }) |>> reindexTaskRef.set
    }
  }

  private def startAlterRequestHandling() {
    executorService.submit(new Runnable {
      def run() {
        while (!Thread.currentThread().isInterrupted) {
          try {
            alterIndexRequests.poll() match {
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

  private def stopAlterRequestsHandling() {
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

