package imcode.server.document.index.solr

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpec}
import imcode.server.document.index.{Docs, DocIndexingMocksSetup}
import com.imcode.imcms.test.fixtures.{DocFX, LanguageFX}
import org.apache.solr.common.SolrInputDocument

@RunWith(classOf[JUnitRunner])
class SolrDocumentIndexServiceOpsTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  val ops: SolrDocumentIndexServiceOps = {
    val ms = new DocIndexingMocksSetup
    ms.addDocument(Docs.textDocEn)

    new SolrDocumentIndexServiceOps(ms.docIndexer.documentMapper, ms.docIndexer)
  }

  "SolrDocumentIndexServiceOps" should {
    "create an empty Seq of SolrInputDocument" in {
      expect(Seq.empty[SolrInputDocument]) {
        ops.mkSolrInputDocs(DocFX.vacantId)
      }
    }

    "create a singleton Seq of SolrInputDocument" in {
      ops.mkSolrInputDocs(1001, Seq(LanguageFX.mkEnglish))
    }
  }
}