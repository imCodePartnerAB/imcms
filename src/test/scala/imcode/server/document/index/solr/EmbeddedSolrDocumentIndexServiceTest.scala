package imcode.server.document.index.solr

import com.imcode._
import scala.collection.JavaConverters._
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpec}
import org.apache.solr.common.SolrInputDocument
import java.lang.Thread
import org.mockito.Mockito.{mock => _, _}
import org.mockito.Matchers._
import org.scalatest.mock.MockitoSugar._
import com.imcode.imcms.test._
import com.imcode.imcms.test.fixtures.{CategoryFX, DocFX, LanguageFX, UserFX}
import org.apache.solr.client.solrj.{SolrQuery, SolrServer}
import _root_.imcode.server.document.index.{DocIndexingMocksSetup}

@RunWith(classOf[JUnitRunner])
class EmbeddedSolrDocumentIndexServiceTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  Test.initLogging()

  val ops: SolrDocumentIndexServiceOps = {
    val ms = new DocIndexingMocksSetup

    ms.addDocuments(DocFX.mkTextDocs(DocFX.DefaultId, 10))
    ms.addCategories(CategoryFX.mkCategories() :_*)

    new SolrDocumentIndexServiceOps(ms.docIndexer.documentMapper, ms.docIndexer)
  }

  "EmbeddedSolrDocumentIndexService" should {
    "index all documents" in {
      Test.solr.recreateHome()

      using(new EmbeddedSolrDocumentIndexService(Test.solr.home, ops)) { service =>
        val docs = service.search(new SolrQuery("*:*").setRows(Integer.MAX_VALUE), UserFX.mkSuperAdmin)
        assertTrue("No docs", docs.isEmpty)

        for (metaId <- DocFX.DefaultId until (DocFX.DefaultId + 10)) {
          service.requestIndexUpdate(SolrDocumentIndexService.AddDocsToIndex(metaId))
        }

        Thread.sleep(1000)
      }

      using(new EmbeddedSolrDocumentIndexService(Test.solr.home, ops)) { service =>
        val docs = service.search(new SolrQuery("*:*").setRows(Integer.MAX_VALUE), UserFX.mkSuperAdmin)
        assertEquals("Found docs", 20, docs.size())

        for (metaId <- DocFX.DefaultId until (DocFX.DefaultId + 10)) {
          val docs = service.search(new SolrQuery("meta_id:" + metaId), UserFX.mkSuperAdmin)
          assertEquals("Found docs", 2, docs.size())
        }
      }
    }

    "rebuild index" in {
      using(new EmbeddedSolrDocumentIndexService(Test.solr.home, ops)) { service =>
        service.requestIndexRebuild()

        Thread.sleep(1000)
      }


      using(new EmbeddedSolrDocumentIndexService(Test.solr.home, ops)) { service =>
        val docs = service.search(new SolrQuery("*:*").setRows(Integer.MAX_VALUE), UserFX.mkSuperAdmin)
        assertEquals("Found docs", 20, docs.size())

        for (metaId <- DocFX.DefaultId until (DocFX.DefaultId + 10)) {
          val docs = service.search(new SolrQuery("meta_id:"+metaId), UserFX.mkSuperAdmin)
          assertEquals("Found docs", 2, docs.size())
        }
      }
    }
  }
}

