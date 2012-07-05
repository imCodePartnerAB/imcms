package imcode.server.document.index.solr

import com.imcode._
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpec}
import imcode.server.document.index.{DocIndexingMocksSetup}
import com.imcode.imcms.test.fixtures.{DocFX, LanguageFX}
import org.apache.solr.common.SolrInputDocument

@RunWith(classOf[JUnitRunner])
class SolrDocumentIndexServiceOpsTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  val ops: SolrDocumentIndexServiceOps = {
    val ms = new DocIndexingMocksSetup

    ms.addDocuments(DocFX.mkTextDocs(DocFX.defaultId, 10))

    new SolrDocumentIndexServiceOps(ms.docIndexer.documentMapper, ms.docIndexer)
  }

  "SolrDocumentIndexServiceOps" should {
    "create an empty Seq of SolrInputDocument" in {
      ops.mkSolrInputDocs(DocFX.vacantId) |> { solrInputDocs =>
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
      ops.mkSolrInputDocs() |> { view =>
        assertEquals("solrInputDocs sequences in view", 10, view.length)

        for {
          (docId, solrInputDocsSeq) <- view
        } {
          assertEquals("solrInputDocs in seq", 2, solrInputDocsSeq.length)
        }
      }
    }

    "produce SOLr delete query for every document id" in pendingUntilFixed {
      for (docId <- DocFX.defaultId until (DocFX.defaultId + 10)) {
        expect("<undefined>", "valid SOLr delete query") {
          ops.mkSolrDeleteQuery(docId)
        }
      }
    }
  }
}