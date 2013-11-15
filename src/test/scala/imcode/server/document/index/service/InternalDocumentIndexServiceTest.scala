package imcode.server.document.index.service

import com.imcode._
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterEach, BeforeAndAfterAll, WordSpec}
import org.mockito.Mockito.{mock => _}
import com.imcode.imcms.test._
import com.imcode.imcms.test.fixtures.{CategoryFX, DocFX, UserFX}
import org.apache.solr.client.solrj.SolrQuery
import _root_.imcode.server.document.index.DocIndexingMocksSetup
import _root_.imcode.server.document.index.service.impl.{DocumentIndexServiceOps, InternalDocumentIndexService}
import scala.util.Failure

@RunWith(classOf[JUnitRunner])
class InternalDocumentIndexServiceTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfterEach {

  //TestSetup.init()

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
        val docs = service.query(new SolrQuery("*:*").setRows(Integer.MAX_VALUE)).get.getResults
        assertTrue("No docs", docs.isEmpty)

        for (metaId <- DocFX.DefaultId until (DocFX.DefaultId + 10)) {
          service.update(AddDocToIndex(metaId))
        }

        Thread.sleep(1000)
      }

      using(new InternalDocumentIndexService(TestSetup.solr.home, ops)) { service =>
        val docs = service.query(new SolrQuery("*:*").setRows(Integer.MAX_VALUE)).get.getResults
        assertEquals("Found docs", 20, docs.size)

        for (metaId <- DocFX.DefaultId until (DocFX.DefaultId + 10)) {
          val docs = service.query(new SolrQuery("meta_id:" + metaId)).get.getResults
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
        val docs = service.query(new SolrQuery("*:*").setRows(Integer.MAX_VALUE)).get.getResults
        assertEquals("Found docs", 20, docs.size)

        for (metaId <- DocFX.DefaultId until (DocFX.DefaultId + 10)) {
          val docs = service.query(new SolrQuery(s"meta_id:$metaId")).get.getResults
          assertEquals("Found docs", 2, docs.size)
        }
      }
    }

    "recreate and rebuild index when index is corrupted" in {
      TestSetup.solr.recreateHome()

      using(new InternalDocumentIndexService(TestSetup.solr.home, ops)) { service =>
        val docs = service.query(new SolrQuery("*:*").setRows(Integer.MAX_VALUE)).get.getResults
        assertTrue("No docs", docs.isEmpty)

        for (metaId <- DocFX.DefaultId until (DocFX.DefaultId + 10)) {
          service.update(AddDocToIndex(metaId))
          if (metaId == DocFX.DefaultId + 5) {
            TestSetup.solr.recreateHome()
          }
        }

        Thread.sleep(1000)

        val docs2 = service.query(new SolrQuery("*:*").setRows(Integer.MAX_VALUE)).get.getResults
        assertEquals("Found docs", 20, docs2.size)
      }
    }

    "read failure test" in {
      TestSetup.solr.recreateHome()

      using(new InternalDocumentIndexService(TestSetup.solr.home, ops)) { service =>
        service.rebuild().get |> { task =>
          task.future.get()
        }

        service.query(new SolrQuery("*:*").setRows(Integer.MAX_VALUE)).get.getResults |> { docs =>
          assertEquals("Found docs", 20, docs.size)
        }

        //Test.solr.recreateHome()

        service.query(new SolrQuery("nnn:uuu").setRows(Integer.MAX_VALUE)) match {
          case Failure(error) =>
          case _ =>
        }
      }
    }
  }
}

