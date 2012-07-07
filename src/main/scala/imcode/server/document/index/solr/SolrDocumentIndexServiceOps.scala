package imcode.server.document.index.solr

import com.imcode._
import com.imcode.imcms.mapping.DocumentMapper
import scala.collection.SeqView
import scala.collection.JavaConverters._
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.client.solrj.SolrServer
import com.imcode.imcms.api.I18nLanguage
import java.lang.InterruptedException
import java.util.Date
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{ExecutorService, Future}

/**
 * SOLr document index operations.
 *
 * The instance of this class is thread save.
 */
// todo: ??? mkXXX wrap any exception into indexCreate exception for distinguishing from SolrException ???
// todo: ??? implement parallel indexing ???
class SolrDocumentIndexServiceOps(documentMapper: DocumentMapper, documentIndexer: DocumentIndexer) {
   // todo: refactor out
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


  def mkSolrDeleteQuery(docId: Int): String = ???


  def search(solrServer: SolrServer, query: String) = ??? // return


  def addDocsToIndex(solrServer: SolrServer, docId: Int) {
    mkSolrInputDocs(docId) |> { solrInputDocs =>
      if (solrInputDocs.nonEmpty) {
        solrServer.add(solrInputDocs.asJava)
        solrServer.commit()
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
  // todo: ??? one interrupt per execution (in the for loop) is enough ???
  def rebuildIndex(solrServer: SolrServer, executorService: ExecutorService): SolrDocumentIndexRebuild = {
    val rebuildStartDt = new Date
    val rebuildStateRef = new AtomicReference[SolrDocumentIndexRebuild.State](SolrDocumentIndexRebuild.Started(rebuildStartDt))

    executorService.submit(new Runnable() {
      def run() {
        // publish state
        try {
          // counter
          for ((docId, solrInputDocs) <- mkSolrInputDocs(); if solrInputDocs.nonEmpty) {
            if (Thread.currentThread().isInterrupted) throw new InterruptedException
            solrServer.add(solrInputDocs.asJava)

            // publish state
            if (Thread.currentThread().isInterrupted) throw new InterruptedException
          }

          solrServer.deleteByQuery("timestamp < rebuildStartTime")
          solrServer.commit()
        } catch {
          case e: InterruptedException =>
            // publish cancelled
            solrServer.rollback() // ???
            throw e

          case e =>
            // publish Failed
            solrServer.rollback() // ???
            throw e
        }
      }
    }) |> { future =>
      new SolrDocumentIndexRebuild {
        val task: Future[_] = future
        def state(): SolrDocumentIndexRebuild.State = rebuildStateRef.get()
      }
    }
  }
}


// SolrDocumentIndexRebuildMonitor
abstract class SolrDocumentIndexRebuild {
  val task: Future[_]
  def state(): SolrDocumentIndexRebuild.State
}


object SolrDocumentIndexRebuild {
  case class Progress(startDt: Date, stateDt: Date, total: Int, indexed: Int) // doc: DocumentDomainObject ??? ProgressSnapshot

  sealed trait State
  case class Started(startDt: Date) extends State
  case class Running(progress: Progress) extends State
  case class Cancelled(progress: Progress) extends State
  case class Failed(progress: Progress, failure: Throwable) extends State
  case class Finished(progress: Progress)
}