package imcode.server.document.index.solr

import com.imcode._
import com.imcode.imcms.mapping.DocumentMapper
import scala.collection.SeqView
import scala.collection.JavaConverters._
import org.apache.solr.common.SolrInputDocument
import com.imcode.imcms.api.I18nLanguage
import imcode.server.document.index.DocumentIndex
import java.util.Date
import org.apache.solr.common.util.DateUtil
import java.lang.{InterruptedException, Thread}
import org.apache.solr.client.solrj.{SolrQuery, SolrServer}

/**
 * SOLr document index operations.
 *
 * The instance of this class is thread save.
 */
// todo: ??? mkXXX wrap any exception into indexCreate exception for distinguishing from SolrException ???
// todo: ??? implement parallel indexing ???
class SolrDocumentIndexServiceOps(documentMapper: DocumentMapper, documentIndexer: DocumentIndexer) extends Log4jLoggerSupport {
   // todo: refactor out
  type MetaId = Int

  def mkSolrInputDocs(metaId: Int): Seq[SolrInputDocument] =
    mkSolrInputDocs(metaId, documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala)


  def mkSolrInputDocs(metaId: Int, languages: Seq[I18nLanguage]): Seq[SolrInputDocument] =
    {
      for {
        language <- languages
        doc <- Option(documentMapper.getDefaultDocument(metaId, language))
      } yield documentIndexer.index(doc)
    } |>> { solrInputDocs =>
      if (logger.isTraceEnabled) {
        logger.trace("created %d solrInputDoc(s) with metaId: %d and language(s): %s.".format(
          solrInputDocs.length, metaId, languages.mkString(", "))
        )
      }
    }


  // consider using other data structure
  def mkSolrInputDocsView(): SeqView[(MetaId, Seq[SolrInputDocument]), Seq[_]] =
    documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala |> { languages =>
      documentMapper.getAllDocumentIds.asScala.view.map(metaId => metaId.toInt -> mkSolrInputDocs(metaId, languages))
    }


  def mkSolrDocsDeleteQuery(metaId: Int): String = "%s:%d".format(DocumentIndex.FIELD__META_ID, metaId)


  def search(solrServer: SolrServer, query: String) = solrServer.query(new SolrQuery(query))


  def addDocsToIndex(solrServer: SolrServer, metaId: Int) {
    mkSolrInputDocs(metaId) |> { solrInputDocs =>
      if (solrInputDocs.nonEmpty) {
        solrServer.add(solrInputDocs.asJava)
        solrServer.commit()
        logger.trace("added %d solrInputDoc(s) with metaId %d into the index.".format(solrInputDocs.length, metaId))
      }
    }
  }


  // todo: ??? return affected count ???
  def deleteDocsFromIndex(solrServer: SolrServer, metaId: Int): Unit = mkSolrDocsDeleteQuery(metaId) |> { deleteQuery =>
    solrServer.deleteByQuery(deleteQuery)
    solrServer.commit()
  }

  @throws(classOf[InterruptedException])
  def rebuildIndex(solrServer: SolrServer)(progressCallback: SolrDocumentIndexRebuild.Progress => Unit) {
    import SolrDocumentIndexRebuild.Progress

    val docsView = mkSolrInputDocsView()
    val docsCount = docsView.length
    val rebuildStartDt = new Date

    progressCallback(Progress(docsCount, 0))

    for (((metaId, solrInputDocs), docNo) <- docsView.zip(Stream.from(1)); if solrInputDocs.nonEmpty) {
      if (Thread.currentThread().isInterrupted) {
        solrServer.rollback()
        throw new InterruptedException()
      }
      solrServer.add(solrInputDocs.asJava)
      progressCallback(Progress(docsCount, docNo))
    }

    solrServer.deleteByQuery("timestamp:{* TO %s}".format(DateUtil.getThreadLocalDateFormat.format(rebuildStartDt)))
    solrServer.commit()
  }
}