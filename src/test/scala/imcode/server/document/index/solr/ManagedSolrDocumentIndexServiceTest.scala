package imcode.server.document.index.solr

import com.imcode.{when => _, _}
import org.junit.Assert._
import org.mockito.Mockito.{mock => _, _}
import org.mockito.Matchers._
import org.scalatest.mock.MockitoSugar._
import com.imcode.imcms.test._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpec}
import imcode.server.document.index.DocIndexingMocksSetup
import com.imcode.imcms.test.fixtures.DocFX
import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.mockito.invocation.InvocationOnMock
import scala.swing.Reactor
import imcode.server.document.index.solr.SolrDocumentIndexRebuild.Progress

@RunWith(classOf[JUnitRunner])
class ManagedSolrDocumentIndexServiceTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  Test.initLogging()

  val ops: SolrDocumentIndexServiceOps = {
    val ms = new DocIndexingMocksSetup

    ms.addDocuments(DocFX.mkTextDocs(DocFX.DefaultId, 10))

    new SolrDocumentIndexServiceOps(ms.docIndexer.documentMapper, ms.docIndexer)
  }


  "ManagedSolrDocumentIndexService" should {
    "update (write) all documents with provided ids into the solr index" in {
      val solrServerReader = mock[SolrServerWithShutdown]
      val solrServerWriter = mock[SolrServerWithShutdown]
      val service = new ManagedSolrDocumentIndexService(solrServerReader, solrServerWriter, ops)

      try {
        1001 to 1010 foreach { id =>
          service.requestIndexUpdate(SolrDocumentIndexService.AddDocsToIndex(id))
        }

        Thread.sleep(1000)
      } finally {
        service.shutdown()
      }

      verify(solrServerWriter, times(10)).add(anyCollectionOf(classOf[SolrInputDocument]))
    }

    "update (write) all but failed-to-index documents with provided ids to the solr index" in {
      val solrServerReader = mock[SolrServerWithShutdown]
      val solrServerWriter = mock[SolrServerWithShutdown]
      val opsMock = mock[SolrDocumentIndexServiceOps]
      val service = new ManagedSolrDocumentIndexService(solrServerReader, solrServerWriter, opsMock)
      val indexErrorsReactor = new Reactor {
        var errors = Vector.empty[ManagedSolrDocumentIndexService.IndexError]

        reactions += {
          case e: ManagedSolrDocumentIndexService.IndexError => errors :+= e
        }

        listenTo(service)
      }

      when(opsMock.addDocsToIndex(any(classOf[SolrServer]), anyInt())).thenAnswer {
        invocation: InvocationOnMock => invocation.getArguments match {
          case Array(solrServer: SolrServer, docId: JInteger) =>
            if (docId % 2 == 0) throw new RuntimeException("failed to index document " + docId)
            else ops.addDocsToIndex(solrServer, docId)
        }
      }

      try {
        1001 to 1010 foreach { id =>
          service.requestIndexUpdate(SolrDocumentIndexService.AddDocsToIndex(id))
        }

        Thread.sleep(1000)
      } finally {
        service.shutdown()
      }

      verify(solrServerWriter, times(5)).add(anyCollectionOf(classOf[SolrInputDocument]))

      assertEquals("Errors count reported during indexing", 5, indexErrorsReactor.errors.length)
    }

    "rebuild (write) all documents with provided ids into the solr index" in {
      val solrServerReader = mock[SolrServerWithShutdown]
      val solrServerWriter = mock[SolrServerWithShutdown]
      val service = new ManagedSolrDocumentIndexService(solrServerReader, solrServerWriter, ops)

      try {
        service.requestIndexRebuild()

        Thread.sleep(1000)

        1001 to 1010 foreach { id =>
          service.requestIndexUpdate(SolrDocumentIndexService.AddDocsToIndex(id))
        }

        Thread.sleep(1000)
      } finally {
        service.shutdown()
      }

      verify(solrServerWriter, times(20)).add(anyCollectionOf(classOf[SolrInputDocument]))
    }
  }
}



class SolrServerWithShutdown extends EmbeddedSolrServer(null, null) with SolrServerShutdown
