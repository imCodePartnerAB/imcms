package imcode.server.document.index.solr

import com.imcode._
import com.imcode.imcms.mapping.DocumentMapper
import scala.collection.SeqView
import scala.collection.JavaConverters._
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.client.solrj.SolrServer
import com.imcode.imcms.api.I18nLanguage
import java.util.concurrent._
import java.util.concurrent.atomic.{AtomicReference, AtomicBoolean}
import java.lang.{InterruptedException, Thread, Throwable}

/**
 * SOLr document index operations.
 *
 * The instance of this class is thread save.
 */
// todo: ??? mkXXX wrap any exception into indexCreate exception for distinguishing from SolrException ???
// todo: ??? implement parallel indexing ???
class SolrDocumentIndexServiceOps(documentMapper: DocumentMapper, documentIndexer: DocumentIndexer) extends Log4jLoggerSupport {
   // todo: refactor out
  type DocId = Int

  def mkSolrInputDocs(docId: Int): Seq[SolrInputDocument] =
    mkSolrInputDocs(docId, documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala)


  def mkSolrInputDocs(docId: Int, languages: Seq[I18nLanguage]): Seq[SolrInputDocument] =
    {
      for {
        language <- languages
        doc <- Option(documentMapper.getDefaultDocument(docId, language))
      } yield documentIndexer.index(doc)
    } |>> { solrInputDocs =>
      if (logger.isTraceEnabled) {
        logger.trace("made %d solrInputDocs with docId=%d and languages=%s.".format(
          solrInputDocs.length, docId, languages.mkString(","))
        )
      }
    }


  // consider using other data structure
  def mkSolrInputDocsView(): SeqView[(DocId, Seq[SolrInputDocument]), Seq[_]] =
    documentMapper.getImcmsServices.getI18nSupport.getLanguages.asScala |> { languages =>
      documentMapper.getAllDocumentIds.asScala.view.map(docId => docId.toInt -> mkSolrInputDocs(docId, languages))
    }


  def mkSolrDeleteQuery(docId: Int): String = ???


  def search(solrServer: SolrServer, query: String) = ??? // return


  def addDocsToIndex(solrServer: SolrServer, docId: Int) {
    mkSolrInputDocs(docId) |> { solrInputDocs =>
      if (solrInputDocs.nonEmpty) {
        solrServer.add(solrInputDocs.asJava)
        solrServer.commit()
        logger.trace("added %d solrInputDocs with docId %d into the index.".format(solrInputDocs.length, docId))
      }
    }
  }


  // todo: ??? return affected count ???
  def deleteDocsFromIndex(solrServer: SolrServer, docId: Int): Unit = mkSolrDeleteQuery(docId) |> { deleteQuery =>
    solrServer.deleteByQuery(deleteQuery)
    solrServer.commit()
  }


  /**
   *
   */
  def rebuildIndexInterruptibly(solrServer: SolrServer)(progressCallback: SolrDocumentIndexRebuild.Progress => Unit) {
    import SolrDocumentIndexRebuild.Progress

    val docsView = mkSolrInputDocsView()
    val docsCount = docsView.length

    progressCallback(Progress(docsCount, 0))

    for (((docId, solrInputDocs), docNo) <- docsView.zip(Stream.from(1)); if solrInputDocs.nonEmpty) {
      if (Thread.currentThread().isInterrupted) throw new InterruptedException

      solrServer.add(solrInputDocs.asJava)
      progressCallback(Progress(docsCount, docNo))
    }

    //solrServer.deleteByQuery("timestamp < rebuildStartTime")
    solrServer.commit()
  }
}