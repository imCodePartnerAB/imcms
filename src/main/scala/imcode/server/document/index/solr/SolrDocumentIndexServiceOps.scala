package imcode.server.document.index.solr

import com.imcode._
import com.imcode.imcms.mapping.DocumentMapper
import imcode.server.document.DocumentDomainObject
import scala.collection.SeqView
import scala.collection.JavaConverters._
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.client.solrj.SolrServer

/**
 * The instance of this class is thread save.
 */
// todo: ??? mkXXX wrap any exception into indexCreate exception for distinguishing from SolrException ???
// todo: ??? implement parallel indexing ???
class SolrDocumentIndexServiceOps(documentMapper: DocumentMapper, documentIndexer: DocumentIndexer) {

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

  def addDocsToIndex(solrServer: SolrServer, docId: Int) {
    mkSolrInputDocs(docId) |> { solrInputDocs =>
      if (solrInputDocs.nonEmpty) {
        solrServer.add(solrInputDocs.asJava)
        solrServer.commit()
      }
    }
  }

  def addDocToIndex(solrServer: SolrServer, doc: DocumentDomainObject) {
    mkSolrInputDoc(doc) |> { solrInputDoc =>
      solrServer.add(solrInputDoc)
      solrServer.commit()
    }
  }

  def rebuildIndex(solrServer: SolrServer) {
    val rebuildStartTime = System.currentTimeMillis()

    for ((docId, solrInputDocs) <- mkSolrInputDocs(); if solrInputDocs.nonEmpty) {
      solrServer.add(solrInputDocs.asJava)
    }

    solrServer.deleteByQuery("timestamp < rebuildStartTime")
    solrServer.commit()
  }

  def deleteDocsFromIndex(solrServer: SolrServer, docId: Int) = null

  def deleteDocFromIndex(solrServer: SolrServer, doc: DocumentDomainObject) = null

  def search(solrServer: SolrServer, query: String) = null
}