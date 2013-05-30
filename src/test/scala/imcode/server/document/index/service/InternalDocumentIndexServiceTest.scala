package imcode.server.document.index.service

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
import imcode.server.document.index.service.impl.{DocumentIndexServiceOps, InternalDocumentIndexService}
import scala.util.{Try, Failure, Success}

@RunWith(classOf[JUnitRunner])
class InternalDocumentIndexServiceTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  TestSetup.initLogging()

  val ops: DocumentIndexServiceOps = {
    val ms = new DocIndexingMocksSetup

    ms.addDocuments(DocFX.mkTextDocs(DocFX.DefaultId, 10))
    ms.addCategories(CategoryFX.mkCategories() :_*)

    new DocumentIndexServiceOps(ms.docIndexer.documentMapper, ms.docIndexer)
  }

  "InternalDocumentIndexService" should {
    "index all documents" in {
      TestSetup.solr.recreateHome()

      using(new InternalDocumentIndexService(TestSetup.solr.home, ops)) { service =>
        val docs = service.search(new SolrQuery("*:*").setRows(Integer.MAX_VALUE), UserFX.mkSuperAdmin).get
        assertTrue("No docs", docs.isEmpty)

        for (metaId <- DocFX.DefaultId until (DocFX.DefaultId + 10)) {
          service.update(AddDocToIndex(metaId))
        }

        Thread.sleep(1000)
      }

      using(new InternalDocumentIndexService(TestSetup.solr.home, ops)) { service =>
        val docs = service.search(new SolrQuery("*:*").setRows(Integer.MAX_VALUE), UserFX.mkSuperAdmin).get
        assertEquals("Found docs", 20, docs.size)

        for (metaId <- DocFX.DefaultId until (DocFX.DefaultId + 10)) {
          val docs = service.search(new SolrQuery("meta_id:" + metaId), UserFX.mkSuperAdmin).get
          assertEquals("Found docs", 2, docs.size)
        }
      }
    }

    "rebuild index" in {
      using(new InternalDocumentIndexService(TestSetup.solr.home, ops)) { service =>
        service.rebuild().get |> { task =>
          task.future.get()
        }
      }


      using(new InternalDocumentIndexService(TestSetup.solr.home, ops)) { service =>
        val docs = service.search(new SolrQuery("*:*").setRows(Integer.MAX_VALUE), UserFX.mkSuperAdmin).get
        assertEquals("Found docs", 20, docs.size)

        for (metaId <- DocFX.DefaultId until (DocFX.DefaultId + 10)) {
          val docs = service.search(new SolrQuery(s"meta_id:$metaId"), UserFX.mkSuperAdmin).get
          assertEquals("Found docs", 2, docs.size)
        }
      }
    }

    "recreate and rebuild index when index is corrupted" in {
      TestSetup.solr.recreateHome()

      using(new InternalDocumentIndexService(TestSetup.solr.home, ops)) { service =>
        val docs = service.search(new SolrQuery("*:*").setRows(Integer.MAX_VALUE), UserFX.mkSuperAdmin).get
        assertTrue("No docs", docs.isEmpty)

        for (metaId <- DocFX.DefaultId until (DocFX.DefaultId + 10)) {
          service.update(AddDocToIndex(metaId))
          if (metaId == DocFX.DefaultId + 5) {
            TestSetup.solr.recreateHome()
          }
        }

        Thread.sleep(1000)

        val docs2 = service.search(new SolrQuery("*:*").setRows(Integer.MAX_VALUE), UserFX.mkSuperAdmin).get
        assertEquals("Found docs", 20, docs2.size)
      }
    }

    "read failure test" in {
      TestSetup.solr.recreateHome()

      using(new InternalDocumentIndexService(TestSetup.solr.home, ops)) { service =>
        service.rebuild().get |> { task =>
          task.future.get()
        }

        service.search(new SolrQuery("*:*").setRows(Integer.MAX_VALUE), UserFX.mkSuperAdmin).get |> { docs =>
          assertEquals("Found docs", 20, docs.size)
        }

        //Test.solr.recreateHome()

        service.search(new SolrQuery("nnn:uuu").setRows(Integer.MAX_VALUE), UserFX.mkSuperAdmin) match {
          case Failure(error) =>
          case _ =>
        }
      }
    }
  }
}

