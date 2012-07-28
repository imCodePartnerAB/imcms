package imcode.server.document.index.solr

import com.imcode.{when => _, _}
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpec}
import imcode.server.document.index.{DocIndexingMocksSetup}
import com.imcode.imcms.test.fixtures.{DocFX, LanguageFX}
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.client.solrj.SolrServer
import org.mockito.Mockito.{mock => _, _}
import org.mockito.Matchers._
import org.scalatest.mock.MockitoSugar._
import com.imcode.imcms.test._
import java.lang.{InterruptedException, Thread}

@RunWith(classOf[JUnitRunner])
class SolrDocumentIndexServiceOpsTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  val ops: SolrDocumentIndexServiceOps = {
    val ms = new DocIndexingMocksSetup

    ms.addDocuments(DocFX.mkTextDocs(DocFX.DefaultId, 10))

    new SolrDocumentIndexServiceOps(ms.docIndexer.documentMapper, ms.docIndexer)
  }

  // ??? printed to out ???
  "SolrDocumentIndexServiceOps" should {
    "create an empty Seq of SolrInputDocument" in {
      ops.mkSolrInputDocs(DocFX.VacantId) |> { solrInputDocs =>
        assertTrue("No solrInputDocs", solrInputDocs.isEmpty)
      }
    }

    "create a singleton Seq of SolrInputDocument" in {
      ops.mkSolrInputDocs(1001, Seq(LanguageFX.mkEnglish)) |> { solrInputDocs =>
        assertTrue("Single solrInputDoc", solrInputDocs.length == 1)
      }
    }

    "create a Seq of SolrInputDocument" in {
      ops.mkSolrInputDocs(1001, LanguageFX.mkLanguages) |> { solrInputDocs =>
        assertTrue("solrInputDocs count", solrInputDocs.length == 2)
      }
    }

    "create a view of tuples (DocId -> Seq[SolrInputDocument])" in {
      ops.mkSolrInputDocsView() |> { view =>
        assertEquals("solrInputDocs sequences in view", 10, view.length)

        for {
          (docId, solrInputDocsSeq) <- view
        } {
          assertEquals("solrInputDocs in seq", 2, solrInputDocsSeq.length)
        }
      }
    }

    "produce SOLr delete query for every document meta id" in {
      for (metaId <- DocFX.DefaultId until (DocFX.DefaultId + 10)) {
        expect("meta_id:"+metaId, "valid SOLr delete query") {
          ops.mkSolrDocsDeleteQuery(metaId)
        }
      }
    }
  }

  // ??? printed to out ???
  "running index rebuild" should {
    "index all docs" in {
      import SolrDocumentIndexRebuild._
      val solrServerMock = mock[SolrServer]
      var progress = Vector.empty[Progress]

      ops.rebuildIndex(solrServerMock){p => progress :+= p; Thread.sleep(2000)}

      assertEquals("progress callablck invokation count", 11, progress.length)
      assertTrue("progress callback value is incremented on every call starting from 0",
        progress.zipWithIndex.forall { case (Progress(10, indexed), expectedIndexed) => indexed == expectedIndexed }
      )
    }

    "index half of docs when running thread is interrupted and throw an InterruptedException" in {
      import SolrDocumentIndexRebuild._

      val solrServerMock = mock[SolrServer]
      var progress = Vector.empty[Progress]

      intercept[InterruptedException] {
        ops.rebuildIndex(solrServerMock) {
          case p@Progress(10, indexedDocsCount) =>
            progress :+= p
            if (indexedDocsCount == 5) throw new InterruptedException()
        }
      }

      // clear interrupted flag
      Thread.interrupted()

      assertEquals("progress callablck invokation count", 6, progress.length)
      assertTrue("progress callback value is incremented on every call starting from 0",
        progress.zipWithIndex.forall { case (Progress(10, indexed), expectedIndexed) => indexed == expectedIndexed }
      )
    }
  }
}