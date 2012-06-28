package imcode.server.document.index

import com.imcode._
import scala.reflect.BeanProperty
import scala.collection.JavaConverters._
import com.imcode.imcms.mapping.DocumentMapper
import com.imcode.Log4jLoggerSupport
import org.apache.solr.client.solrj.impl.{BinaryRequestWriter, HttpSolrServer}
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.core.CoreContainer
import java.io.File
import imcode.server.document.DocumentDomainObject
import imcode.server.user.UserDomainObject
import org.apache.solr.common.SolrInputDocument
import scala.actors.{DaemonActor, Actor, TIMEOUT}
import java.util.concurrent.locks.ReentrantLock
import scala.collection.SeqView
import java.util.concurrent.atomic.AtomicReference
import org.apache.solr.client.solrj.{SolrQuery, SolrServer}
import java.util.concurrent.{LinkedBlockingQueue, BlockingQueue, Callable, Executors, Future => JFuture}
import java.util.LinkedList


// Threads and resource (solrServer/s) management
trait SolrDocumentIndexService extends DocumentIndex with Log4jLoggerSupport {
  @BeanProperty var documentMapper: DocumentMapper = _
  @BeanProperty var ops: SolrDocumentIndexServiceOps = _

  protected def solrServerReader: SolrServer with SolrServerShutdown
  protected def solrServerWriter: SolrServer with SolrServerShutdown

  private object indexUpdate {
    import Actor._

    sealed trait Event
    case class AddDocToIndex(doc: DocumentDomainObject) extends Event
    case class AddDocsToIndex(docId: Int) extends Event
    case class DeleteDocFromIndex(doc: DocumentDomainObject) extends Event
    case class DeleteDocsFromIndex(docId: Int) extends Event

    private val events = new LinkedBlockingQueue[Event]//(1000)
    private val eventsDispatcher = actor {
      react {
        // add DeleteXXX to the end of queue
        case event: Event =>
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

    def submitEvent(event: Event) { eventsDispatcher ! event}

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
                case AddDocsToIndex(docId) => ops.addDocToIndex(solrServerWriter, docId)
                case DeleteDocFromIndex(doc) => ops.deleteDocFromIndex(solrServerWriter, doc)
                case DeleteDocsFromIndex(docId) => ops.deleteDocFromIndex(solrServerWriter, docId)
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
  }

  def search(query: DocumentQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
    try {
      val queryResponse = solrServerReader.query(new SolrQuery(query.toString))

      java.util.Collections.emptyList()
    } catch {
      case e =>
        logger.error("Search error", e)
        java.util.Collections.emptyList()
      }
  }

  def rebuild() {
    indexUpdate.requestRebuild()
  }

  def indexDocument(docId: Int) {
    indexUpdate.submitEvent(indexUpdate.AddDocsToIndex(docId))
  }

  def indexDocument(document: DocumentDomainObject) {
    indexUpdate.submitEvent(indexUpdate.AddDocToIndex(document))
  }

  def removeDocument(docId: Int) {
    indexUpdate.submitEvent(indexUpdate.DeleteDocsFromIndex(docId))
  }

  def removeDocument(document: DocumentDomainObject) {
    indexUpdate.submitEvent(indexUpdate.DeleteDocFromIndex(document))
  }

  def shutdown() {
    solrServerReader.shutdown()
    solrServerWriter.shutdown()
  }
}


class EmbeddedSolrDocumentIndexService(solrHome: File) extends SolrDocumentIndexService {
  protected val solrServerReader = SolrServerFactory.createEmbeddedSolrServer(solrHome)
  protected val solrServerWriter = SolrServerFactory.createEmbeddedSolrServer(solrHome)
}

// solrReaderUrl: String, solrWriterUrl: String
class RemoteSolrDocumentIndexService(solrUrl: String) extends SolrDocumentIndexService {
  protected val solrServerReader = SolrServerFactory.createHttpSolrServer(solrUrl)
  protected val solrServerWriter = SolrServerFactory.createHttpSolrServer(solrUrl)

  private val reindexExecutor = Executors.newSingleThreadExecutor()
  private val reindexTask = new AtomicReference[JFuture[_]]

  def reindexTaskOpt(): Option[JFuture[_]] = Option(reindexTask.get())
}