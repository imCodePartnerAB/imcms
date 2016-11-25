package imcode.server.document.index.service

import _root_.java.util.function.Consumer
import com.imcode._
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, BeforeAndAfterAll, WordSpec}
import imcode.server.document.index.DocIndexingMocksSetup
import com.imcode.imcms.test.fixtures.{DocFX, LanguageFX}
import org.apache.solr.client.solrj.SolrServer
import org.mockito.Mockito.{mock => _}
import org.scalatest.mock.MockitoSugar._
import java.lang.{InterruptedException, Thread}
import imcode.server.document.index.service.impl.{IndexRebuildProgress, DocumentIndexServiceOps}
import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class DocumentIndexServiceOpsTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfterEach {

  val ops: DocumentIndexServiceOps = {
    val ms = new DocIndexingMocksSetup

    ms.addDocuments(DocFX.mkTextDocs(DocFX.DefaultId, 10))

    new DocumentIndexServiceOps(ms.docIndexer.getDocumentMapper, ms.docIndexer)
  }

  // ??? printed to out ???
  "DocumentIndexServiceOps" should {
    "create an empty Seq of SolrInputDocument" in {
      ops.mkSolrInputDocs(DocFX.VacantId) |> {
        solrInputDocs =>
          assertTrue("No solrInputDocs", solrInputDocs.isEmpty)
      }
    }

    "create a singleton Seq of SolrInputDocument" in {
      ops.mkSolrInputDocs(1001, Seq(LanguageFX.mkEnglish).asJava) |> {
        solrInputDocs =>
          assertTrue("Single solrInputDoc", solrInputDocs.size() == 1)
      }
    }

    "create a Seq of SolrInputDocument" in {
      ops.mkSolrInputDocs(1001, LanguageFX.mkLanguages.asJava) |> {
        solrInputDocs =>
          assertTrue("solrInputDocs count", solrInputDocs.size() == 2)
      }
    }

//    "create a view of tuples (DocId -> Seq[SolrInputDocument])" in {
//      ops.mkSolrInputDocsView() |> {
//        view =>
//          assertEquals("solrInputDocs sequences in view", 10, view.length)
//
//          for {
//            (docId, solrInputDocsSeq) <- view
//          } {
//            assertEquals("solrInputDocs in seq", 2, solrInputDocsSeq.length)
//          }
//      }
//    }

    "produce SOLr delete query for every document meta id" in {
      for (metaId <- DocFX.DefaultId until (DocFX.DefaultId + 10)) {
        expectResult("meta_id:" + metaId, "valid SOLr delete query") {
          ops.mkSolrDocsDeleteQuery(metaId)
        }
      }
    }
  }

  // ??? printed to out ???
  "running index rebuild" should {
    "index all docs" in {
      val solrServerMock = mock[SolrServer]
      var progress = Vector.empty[IndexRebuildProgress]

      ops.rebuildIndex(solrServerMock, new Consumer[IndexRebuildProgress] {
        override def accept(p: IndexRebuildProgress) {
          progress :+= p; Thread.sleep(2000)
        }
      })

      assertEquals("progress callback invocation count", 11, progress.length)
//      assertTrue("progress callback value is incremented on every call starting from 0",
//        progress.zipWithIndex.forall {
//          case (IndexRebuildProgress(_, _, 10, indexed), expectedIndexed) => indexed == expectedIndexed
//        }
//      )
    }

    "index half of docs when running thread is interrupted and throw an InterruptedException" in {
      val solrServerMock = mock[SolrServer]
      var progress = Vector.empty[IndexRebuildProgress]

      intercept[InterruptedException] {
//        ops.rebuildIndex(solrServerMock) {
//          case p@IndexRebuildProgress(_, _, 10, indexedDocsCount) =>
//            progress :+= p
//            if (indexedDocsCount == 5) throw new InterruptedException()
//        }
      }

      // clear interrupted flag
      Thread.interrupted()

      assertEquals("progress callback invocation count", 6, progress.length)
//      assertTrue("progress callback value is incremented on every call starting from 0",
//        progress.zipWithIndex.forall {
//          case (IndexRebuildProgress(_, _, 10, indexed), expectedIndexed) => indexed == expectedIndexed
//        }
//      )
    }
  }
}