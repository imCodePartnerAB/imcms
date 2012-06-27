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
import org.apache.solr.client.solrj.{SolrQuery, SolrServer}
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{BlockingQueue, Callable, Executors, LinkedBlockingQueue, Future => JFuture}
import java.util.concurrent.locks.ReentrantLock
import scala.collection.SeqView


// DocumentSolrOps
class DocumentIndexingOps(documentMapper: DocumentMapper, documentIndexer: DocumentIndexer) {

  type DocId = Int
  type SolrDeleteQuery = String

  def mkSolrInputDoc(doc: DocumentDomainObject): SolrInputDocument = documentIndexer.index(doc)

  def mkSolrInputDocs(docId: Int): Seq[SolrInputDocument] =
    for {
      language <- documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala
      doc <- Option(documentMapper.getDefaultDocument(docId, language))
    } yield mkSolrInputDoc(doc)

  def mkSolrDeleteQuery(doc: DocumentDomainObject): SolrDeleteQuery = null

  def mkSolrDeleteQueries(docId: Int): Seq[SolrDeleteQuery] = null

  def mkSolrInputDocs(): SeqView[(DocId, Seq[SolrInputDocument]), Seq[_]] =
    documentMapper.getAllDocumentIds.asScala.view.map(docId => docId.toInt -> mkSolrInputDocs(docId))

  //def query(solrServer: => SolrServer, user: UserDomainObject) = null
  //def index(solrServer: => SolrServer, docId: Int) = null
  //def delete(solrServer: => SolrServer, docId: Int) = null
}


// Threads and resource (solrServer/s) management
trait SolrDocumentIndexService extends DocumentIndex with Log4jLoggerSupport {
  @BeanProperty var documentMapper: DocumentMapper = _
  @BeanProperty var documentIndexingOps: DocumentIndexingOps = _

  protected def solrServerReader: SolrServer with SolrServerShutdown
  protected def solrServerWriter: SolrServer with SolrServerShutdown

  def search(query: DocumentQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
    try {
      val queryResponse = solrServerReader.query(new SolrQuery(query.getQuery.toString))

      java.util.Collections.emptyList()
    } catch {
      case e =>
        logger.error("Search error", e)
        java.util.Collections.emptyList()
      }
  }

  def rebuild() {
    for {
      docId <- documentMapper.getAllDocumentIds.iterator().asScala
    } {
      indexDocument(docId)
    }

    solrServerWriter.commit()
  }

  def indexDocument(docId: Int) {
    try {
      documentIndexingOps.mkSolrInputDocs(docId) match {
        case solrInputDocs if solrInputDocs.nonEmpty =>
          solrServerWriter.add(solrInputDocs.asJava)
          solrServerWriter.commit()

        case _ =>
      }
    } catch {
      case e => throw new IndexException(e)
    }
  }

  def indexDocument(document: DocumentDomainObject) {
    if (document != null) {
      try {
          documentIndexingOps.mkSolrInputDoc(document) |> { solrInputDoc =>
            solrServerWriter.add(solrInputDoc)
            solrServerWriter.commit()
          }
      } catch {
        case e => throw new IndexException(e)
      }
    }
  }

  def removeDocument(docId: Int) {
    documentIndexingOps.mkSolrDeleteQueries(docId) match {
      case deleteQueries if deleteQueries.nonEmpty =>
        deleteQueries foreach solrServerWriter.deleteByQuery
        solrServerWriter.commit()

      case _ =>
    }
  }

  def removeDocument(document: DocumentDomainObject) {
    if (document != null) {
      try {
        solrServerWriter.deleteByQuery("meta_id:" + document.getId)
        solrServerWriter.commit()
      } catch {
        case e => throw new IndexException(e)
      }
    }
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
}