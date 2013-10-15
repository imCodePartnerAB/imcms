package imcode.server.document.index.service.impl

import com.imcode._
import com.imcode.imcms.api.DocumentLanguage
import com.imcode.imcms.mapping.DocumentMapper

import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.index.DocumentIndex
import _root_.imcode.server.document.index.service.IndexRebuildProgress

import scala.collection.SeqView
import scala.collection.JavaConverters._

import java.util.Date
import java.net.URLDecoder

import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.util.DateUtil
import org.apache.solr.client.solrj.{SolrQuery, SolrServer}
import org.apache.solr.client.solrj.response.QueryResponse
import imcode.server.document.textdocument.TextDocumentDomainObject


/**
 * Document index service low level operations.
 *
 * An instance of this class is thread save.
 */
// todo: document search might return doc which is not present in db (deleted) - return stub instead
class DocumentIndexServiceOps(documentMapper: DocumentMapper, documentIndexer: DocumentIndexer) extends Log4jLoggerSupport {

  type DocId = Int


  @throws(classOf[SolrInputDocumentCreateException])
  private def withExceptionWrapper[A](body: => A): A = {
    try {
      body
    } catch {
      case e: Exception => throw new SolrInputDocumentCreateException(e)
    }
  }


  @throws(classOf[SolrInputDocumentCreateException])
  def mkSolrInputDocs(docId: Int): Seq[SolrInputDocument] = withExceptionWrapper {
    mkSolrInputDocs(docId, documentMapper.getImcmsServices.getDocumentI18nSupport.getLanguages.asScala)
  }


  @throws(classOf[SolrInputDocumentCreateException])
  def mkSolrInputDocs(docId: Int, languages: Seq[DocumentLanguage]): Seq[SolrInputDocument] = withExceptionWrapper {
    val solrInputDocs = for {
      language <- languages
      doc <- documentMapper.getDefaultDocument[DocumentDomainObject](docId, language).asOption
    } yield documentIndexer.index(doc)


    if (logger.isTraceEnabled) {
      logger.trace("Created %d solrInputDoc(s) with docId: %d and language(s): %s.".format(
        solrInputDocs.length, docId, languages.mkString(", "))
      )
    }

    solrInputDocs
  }


  @throws(classOf[SolrInputDocumentCreateException])
  def mkSolrInputDocsView(): SeqView[(DocId, Seq[SolrInputDocument]), Seq[_]] = {
    documentMapper.getImcmsServices.getDocumentI18nSupport.getLanguages.asScala |> { languages =>
      documentMapper.getAllDocumentIds.asScala.view.map(docId => docId.toInt -> mkSolrInputDocs(docId, languages))
    }
  }


  def mkSolrDocsDeleteQuery(docId: Int): String = "%s:%d".format(DocumentIndex.FIELD__META_ID, docId)


  def search(solrServer: SolrServer, solrQuery: SolrQuery): JList[DocumentDomainObject] = {
    if (logger.isDebugEnabled) {
      logger.debug("Searching SOLr index using query: %s.".format(URLDecoder.decode(solrQuery.toString, "UTF-8")))
    }

    val solrDocs = solrServer.query(solrQuery).getResults
    new java.util.AbstractList[DocumentDomainObject] {
      val docs = scala.collection.mutable.Map.empty[Int, DocumentDomainObject]

      override def get(i: Int): DocumentDomainObject = {
        docs.getOrElseUpdate(i, {
          val solrDoc = solrDocs.get(i)
          val docId = solrDoc.getFieldValue(DocumentIndex.FIELD__META_ID).toString.toInt
          val languageCode = solrDoc.getFieldValue(DocumentIndex.FIELD__LANGUAGE_CODE).toString

          documentMapper.getDefaultDocument[DocumentDomainObject](docId, languageCode) match {
            case null => new TextDocumentDomainObject(docId) |>> { doc =>
              val language = Option(documentMapper.getImcmsServices.getDocumentI18nSupport.getByCode(languageCode)).getOrElse {
                documentMapper.getImcmsServices.getDocumentI18nSupport.getDefaultLanguage
              }

              doc.setLanguage(language)
            }

            case doc => doc
          }
        })
      }

      override val size: Int = solrDocs.size()
    }
  }


  def query(solrServer: SolrServer, solrQuery: SolrQuery): QueryResponse = {
    if (logger.isDebugEnabled) {
      logger.debug("Searching using SOLr query: %s.".format(URLDecoder.decode(solrQuery.toString, "UTF-8")))
    }

    solrServer.query(solrQuery)
  }


  @throws(classOf[SolrInputDocumentCreateException])
  def addDocsToIndex(solrServer: SolrServer, docId: Int) {
    mkSolrInputDocs(docId) |> { solrInputDocs =>
      if (solrInputDocs.nonEmpty) {
        solrServer.add(solrInputDocs.asJava)
        solrServer.commit()
        logger.trace("added %d solrInputDoc(s) with docId %d into the index.".format(solrInputDocs.length, docId))
      }
    }
  }


  def deleteDocsFromIndex(solrServer: SolrServer, docId: Int): Unit = mkSolrDocsDeleteQuery(docId) |> { deleteQuery =>
    solrServer.deleteByQuery(deleteQuery)
    solrServer.commit()
  }


  @throws(classOf[InterruptedException])
  @throws(classOf[SolrInputDocumentCreateException])
  def rebuildIndex(solrServer: SolrServer)(progressCallback: IndexRebuildProgress => Unit) {
    logger.debug("Rebuilding index.")
    val docsView = mkSolrInputDocsView()
    val docsCount = docsView.length
    val rebuildStartDt = new Date
    val rebuildStartMills = rebuildStartDt.getTime

    progressCallback(IndexRebuildProgress(rebuildStartMills, rebuildStartMills, docsCount, 0))

    for {
      ((docId, solrInputDocs), docNo) <- docsView.zip(Stream.from(1)); if solrInputDocs.nonEmpty
    } {
      if (Thread.interrupted()) {
        solrServer.rollback()
        throw new InterruptedException()
      }

      solrServer.add(solrInputDocs.asJava)
      logger.debug(s"Added input docs [$solrInputDocs] to index.")
      progressCallback(IndexRebuildProgress(rebuildStartMills, new Date().getTime, docsCount, docNo))
    }

    logger.debug("Deleting old documents from index.")
    solrServer.deleteByQuery("timestamp:{* TO %s}".format(DateUtil.getThreadLocalDateFormat.format(rebuildStartDt)))
    solrServer.commit()
    logger.debug("Index rebuild is complete.")
  }
}