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
import scala.swing.{Reactor}
import scala.swing.event.Event

/**
 *
 */
object NoOpSolrDocumentIndexService extends SolrDocumentIndexService {

  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = Collections.emptyList()

  def requestIndexUpdate(op: SolrDocumentIndexService.IndexUpdateOp) {}

  def requestIndexRebuild() {}

  def shutdown() {}
}


/**
 * Delegates all invocations to the self-managed instance of EmbeddedSolrDocumentIndexService.
 * In case of an indexing error replaces target instance with a new instance.
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
            deafTo(service)
            serviceRef.set(NoOpSolrDocumentIndexService)
            service.shutdown()
            // wait till shutdown
            serviceRef.set(newService(requestIndexRebuild = true))
          case _ =>
        }
      }
  }

  // todo: replace requestIndexRebuild flag with enum values
  private def newService(requestIndexRebuild: Boolean = false): EmbeddedSolrDocumentIndexService =
    new EmbeddedSolrDocumentIndexService(solrHome, ops) |>> { service =>
      listenTo(service)
      serviceRef.set(service)
      if (requestIndexRebuild) {
        service.requestIndexRebuild()
      }
    }

  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] =
    serviceRef.get().search(query, searchingUser)

  def requestIndexUpdate(op: SolrDocumentIndexService.IndexUpdateOp) {
    serviceRef.get().requestIndexUpdate(op)
  }

  def requestIndexRebuild() {
    serviceRef.get().requestIndexRebuild()
  }

  def shutdown() {
    serviceRef.get().shutdown()
  }
}


object EmbeddedSolrDocumentIndexService {
  case class IndexError(publisher: SolrDocumentIndexService, error: Throwable) extends Event
}


/**
 *
 */
class EmbeddedSolrDocumentIndexService(solrHome: File, ops: SolrDocumentIndexServiceOps) extends SolrDocumentIndexService {
  private val solrServerReader = SolrServerFactory.createEmbeddedSolrServer(solrHome)
  private val solrServerWriter = SolrServerFactory.createEmbeddedSolrServer(solrHome)

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

  def requestIndexRebuild(): JFuture[_] = indexRebuildTaskRef.synchronized {
    indexRebuildTaskRef.get() match {
      case task if !(task == null || task.isDone) => task

      case _ =>
        executorService.submit(new Runnable {
          def run() {
            try {
              cancelIndexUpdateTask()
              ops.rebuildIndex(solrServerWriter)
            } catch {
              case e: InterruptedException =>
                Thread.currentThread().interrupt()
                throw e

              case e =>
                publish(EmbeddedSolrDocumentIndexService.IndexError(EmbeddedSolrDocumentIndexService.this, e))
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
              publish(EmbeddedSolrDocumentIndexService.IndexError(EmbeddedSolrDocumentIndexService.this, e))
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
        publish(EmbeddedSolrDocumentIndexService.IndexError(EmbeddedSolrDocumentIndexService.this, e))
        java.util.Collections.emptyList()
    }
  }


  def start() {
    indexUpdateOpsRegistrator.start()
    startIndexUpdateTask()
  }


  // ??? stop ???
  def shutdown() {
    solrServerReader.shutdown()
    solrServerWriter.shutdown()
    executorService.shutdownNow()
    // stop actor
  }
}