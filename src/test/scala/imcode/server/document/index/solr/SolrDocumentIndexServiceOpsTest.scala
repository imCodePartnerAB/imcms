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
      ops.mkSolrInputDocs(DocFX.vacantId) |> { docs =>
        assertTrue("No docs", docs.isEmpty)
      }
    }

    "create a singleton Seq of SolrInputDocument" in {
      ops.mkSolrInputDocs(1001, Seq(LanguageFX.mkEnglish)) |> { docs =>
        assertTrue("Single docs", docs.length == 1)
      }
    }

    "create a Seq of SolrInputDocument" in {
      ops.mkSolrInputDocs(1001, LanguageFX.mkLanguages) |> { docs =>
        assertTrue("Docs count", docs.length == 2)
      }
    }
  }
}