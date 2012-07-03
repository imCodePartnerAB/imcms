package imcode.server.document.index.solr

import com.imcode._
import com.imcode.imcms.mapping.DocumentMapper
import scala.collection.SeqView
import scala.collection.JavaConverters._
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.client.solrj.SolrServer
import com.imcode.imcms.api.I18nLanguage

/**
 * The instance of this class is thread save.
 */
// todo: ??? mkXXX wrap any exception into indexCreate exception for distinguishing from SolrException ???
// todo: ??? implement parallel indexing ???
class SolrDocumentIndexServiceOps(documentMapper: DocumentMapper, documentIndexer: DocumentIndexer) {

  type DocId = Int

  def mkSolrInputDocs(docId: Int): Seq[SolrInputDocument] =
    mkSolrInputDocs(docId, documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala)

  def mkSolrInputDocs(docId: Int, languages: Seq[I18nLanguage]): Seq[SolrInputDocument] =
    for {
      language <- languages
      doc <- Option(documentMapper.getDefaultDocument(docId, language))
    } yield documentIndexer.index(doc)


  def mkSolrInputDocs(): SeqView[(DocId, Seq[SolrInputDocument]), Seq[_]] =
    documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala |> { languages =>
      documentMapper.getAllDocumentIds.asScala.view.map(docId => docId.toInt -> mkSolrInputDocs(docId, languages))
    }

  def mkSolrDeleteQuery(docId: Int): String = null

  def search(solrServer: SolrServer, query: String) = null

  def addDocsToIndex(solrServer: SolrServer, docId: Int) {
    mkSolrInputDocs(docId) |> { solrInputDocs =>
      if (solrInputDocs.nonEmpty) {
        solrServer.add(solrInputDocs.asJava)
        solrServer.commit()
      }
    }
  }

  def deleteDocsFromIndex(solrServer: SolrServer, docId: Int) = null

  // ??? hard to maintain, use mkSolrDocs, followed by commit ???
  def rebuildIndex(solrServer: SolrServer) {
    val rebuildStartTime = System.currentTimeMillis()

    for ((docId, solrInputDocs) <- mkSolrInputDocs(); if solrInputDocs.nonEmpty) {
      solrServer.add(solrInputDocs.asJava)
    }

    solrServer.deleteByQuery("timestamp < rebuildStartTime")
    solrServer.commit()
  }
}