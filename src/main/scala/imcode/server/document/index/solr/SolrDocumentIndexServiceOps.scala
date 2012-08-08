package imcode.server.document.index.solr

import com.imcode._
import com.imcode.imcms.mapping.DocumentMapper
import scala.collection.SeqView
import scala.collection.JavaConverters._
import org.apache.solr.common.SolrInputDocument
import com.imcode.imcms.api.I18nLanguage
import org.apache.solr.common.util.DateUtil
import java.lang.{InterruptedException, Thread}
import imcode.server.document.DocumentDomainObject
import imcode.server.document.index.{DocumentIndex}
import imcode.server.user.UserDomainObject
import org.apache.solr.client.solrj.{SolrServer}
import org.apache.solr.common.params.SolrParams
import java.util.Date

/**
 * SOLr document index operations.
 *
 * The instance of this class is thread save.
 */
class SolrDocumentIndexServiceOps(documentMapper: DocumentMapper, documentIndexer: DocumentIndexer) extends Log4jLoggerSupport {
  type MetaId = Int

  @throws(classOf[SolrInputDocumentCreateException])
  def withExceptionWrapper[A](body: => A): A =
    try {
      body
    } catch {
      case e: Throwable => throw new SolrInputDocumentCreateException(e)
    }

  @throws(classOf[SolrInputDocumentCreateException])
  def mkSolrInputDocs(metaId: Int): Seq[SolrInputDocument] = withExceptionWrapper {
    mkSolrInputDocs(metaId, documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala)
  }


  @throws(classOf[SolrInputDocumentCreateException])
  def mkSolrInputDocs(metaId: Int, languages: Seq[I18nLanguage]): Seq[SolrInputDocument] = withExceptionWrapper {
    val solrInputDocs = for {
      language <- languages
      doc <- Option(documentMapper.getDefaultDocument(metaId, language))
    } yield documentIndexer.index(doc)


    if (logger.isTraceEnabled) {
      logger.trace("created %d solrInputDoc(s) with metaId: %d and language(s): %s.".format(
        solrInputDocs.length, metaId, languages.mkString(", "))
      )
    }

    solrInputDocs
  }


  // consider using other data structure
  @throws(classOf[SolrInputDocumentCreateException])
  def mkSolrInputDocsView(): SeqView[(MetaId, Seq[SolrInputDocument]), Seq[_]] =
    documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala |> { languages =>
      documentMapper.getAllDocumentIds.asScala.view.map(metaId => metaId.toInt -> mkSolrInputDocs(metaId, languages))
    }


  def mkSolrDocsDeleteQuery(metaId: Int): String = "%s:%d".format(DocumentIndex.FIELD__META_ID, metaId)

  def search(solrServer: SolrServer, solrParams: SolrParams, searchingUser: UserDomainObject): JList[DocumentDomainObject] = {
    if (logger.isDebugEnabled) {
      logger.debug("Searching using solrParams: %s, searchingUser: %s.".format(solrParams, searchingUser))
    }

    val solrDocs = solrServer.query(solrParams).getResults

    new java.util.LinkedList[DocumentDomainObject] |>> { docs =>
      for (solrDocId <- 0.until(solrDocs.size)) {
        val solrDoc = solrDocs.get(solrDocId)
        val metaId = solrDoc.getFieldValue(DocumentIndex.FIELD__META_ID).asInstanceOf[Int]
        val languageCode = solrDoc.getFieldValue(DocumentIndex.FIELD__LANGUAGE).asInstanceOf[String]
        val doc = documentMapper.getDefaultDocument(metaId, languageCode)

        if (doc != null && searchingUser.canSearchFor(doc)) {
          docs.add(doc)
        }
      }
    }
  }


  @throws(classOf[SolrInputDocumentCreateException])
  def addDocsToIndex(solrServer: SolrServer, metaId: Int) {
    mkSolrInputDocs(metaId) |> { solrInputDocs =>
      if (solrInputDocs.nonEmpty) {
        solrServer.add(solrInputDocs.asJava)
        solrServer.commit()
        logger.trace("added %d solrInputDoc(s) with metaId %d into the index.".format(solrInputDocs.length, metaId))
      }
    }
  }


  def deleteDocsFromIndex(solrServer: SolrServer, metaId: Int): Unit = mkSolrDocsDeleteQuery(metaId) |> { deleteQuery =>
    solrServer.deleteByQuery(deleteQuery)
    solrServer.commit()
  }


  @throws(classOf[InterruptedException])
  @throws(classOf[SolrInputDocumentCreateException])
  def rebuildIndex(solrServer: SolrServer)(progressCallback: SolrDocumentIndexService.IndexRebuildProgress => Unit) {
    import SolrDocumentIndexService.IndexRebuildProgress

    val docsView = mkSolrInputDocsView()
    val docsCount = docsView.length
    val rebuildStartDt = new Date
    val rebuildStartMills = rebuildStartDt.getTime

    progressCallback(IndexRebuildProgress(rebuildStartMills, rebuildStartMills, docsCount, 0))

    for (((metaId, solrInputDocs), docNo) <- docsView.zip(Stream.from(1)); if solrInputDocs.nonEmpty) {
      if (Thread.currentThread().isInterrupted) {
        solrServer.rollback()
        throw new InterruptedException()
      }
      solrServer.add(solrInputDocs.asJava)
      progressCallback(IndexRebuildProgress(rebuildStartMills, new Date().getTime, docsCount, docNo))
    }

    solrServer.deleteByQuery("timestamp:{* TO %s}".format(DateUtil.getThreadLocalDateFormat.format(rebuildStartDt)))
    solrServer.commit()
  }
}