package imcode.server.document.index.solr

import scala.collection.JavaConverters._
import com.imcode.{when => _, _}
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpec}
import imcode.server.document.index.{DocIndexingMocksSetup}
import org.apache.solr.common.SolrInputDocument
import java.lang.Thread
import org.apache.solr.client.solrj.SolrServer
import org.mockito.Mockito.{mock => _, _}
import org.mockito.Matchers._
import org.scalatest.mock.MockitoSugar._
import com.imcode.imcms.test._
import com.imcode.imcms.test.fixtures.{CategoryFX, DocFX, LanguageFX}

@RunWith(classOf[JUnitRunner])
class EmbeddedSolrDocumentIndexServiceTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  Test.initLogging()

  val ops: SolrDocumentIndexServiceOps = {
    val ms = new DocIndexingMocksSetup

    ms.addDocuments(DocFX.mkTextDocs(DocFX.DefaultId, 10))
    ms.addCategories(CategoryFX.mkCategories :_*)

    new SolrDocumentIndexServiceOps(ms.docIndexer.documentMapper, ms.docIndexer)
  }

  "EmbeddedSolrDocumentIndexService" should {
    "rebuild index" in {
      Test.solr.recreateHome()

      val service = new EmbeddedSolrDocumentIndexService(Test.solr.home, ops)
      try {
        service.requestIndexRebuild()

        Thread.sleep(3000)
      } finally {
        service.shutdown()
      }
    }
  }
}