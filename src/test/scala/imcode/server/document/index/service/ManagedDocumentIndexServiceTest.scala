package imcode.server.document.index.service

import com.imcode._
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
import org.mockito.invocation.InvocationOnMock
import imcode.server.document.index.service.impl.{DocumentIndexServiceOps, ManagedSolrDocumentIndexService}

@RunWith(classOf[JUnitRunner])
class ManagedSolrDocumentIndexServiceTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  Test.initLogging()

  val ops: DocumentIndexServiceOps = {
    val ms = new DocIndexingMocksSetup

    ms.addDocuments(DocFX.mkTextDocs(DocFX.DefaultId, 10))

    new DocumentIndexServiceOps(ms.docIndexer.documentMapper, ms.docIndexer)
  }


  "ManagedSolrDocumentIndexService" should {
    "update (write) all documents with provided ids into the solr index" in {
      val solrServerReader = mock[SolrServer]
      val solrServerWriter = mock[SolrServer]
      val service = new ManagedSolrDocumentIndexService(solrServerReader, solrServerWriter, ops, _ => ())

      try {
        1001 to 1010 foreach {
          id =>
            service.requestIndexUpdate(AddDocToIndex(id))
        }

        Thread.sleep(1000)
      } finally {
        service.shutdown()
      }

      verify(solrServerWriter, times(10)).add(anyCollectionOf(classOf[SolrInputDocument]))
    }

    "update (write) documents with provided ids to the solr index untill failure" in {
      val solrServerReader = mock[SolrServer]
      val solrServerWriter = mock[SolrServer]
      val opsMock = mock[DocumentIndexServiceOps]
      var serviceErrors = Vector.empty[ManagedSolrDocumentIndexService.ServiceFailure]
      val service = new ManagedSolrDocumentIndexService(solrServerReader, solrServerWriter, opsMock, serviceErrors :+= _)

      when(opsMock.addDocsToIndex(any(classOf[SolrServer]), anyInt())).thenAnswer {
        invocation: InvocationOnMock => invocation.getArguments match {
          case Array(solrServer: SolrServer, docId: JInteger) =>
            if (docId > 1005) throw new RuntimeException("failed to index document " + docId)
            else ops.addDocsToIndex(solrServer, docId)
        }
      }

      try {
        1001 to 1010 foreach {
          id =>
            service.requestIndexUpdate(AddDocToIndex(id))
        }

        Thread.sleep(1000)
      } finally {
        service.shutdown()
      }

      verify(solrServerWriter, times(5)).add(anyCollectionOf(classOf[SolrInputDocument]))

      assertEquals("Errors count reported during indexing", 1, serviceErrors.length)
      assertTrue("Error is an instance of IndexUpdateFailure", serviceErrors.head.isInstanceOf[ManagedSolrDocumentIndexService.IndexUpdateFailure])
    }

    "rebuild (write) all documents with provided ids into the solr index" in {
      val solrServerReader = mock[SolrServer]
      val solrServerWriter = mock[SolrServer]
      var serviceErrors = Vector.empty[ManagedSolrDocumentIndexService.ServiceFailure]
      val service = new ManagedSolrDocumentIndexService(solrServerReader, solrServerWriter, ops, serviceErrors :+= _)

      try {
        service.requestIndexRebuild()

        Thread.sleep(1000)

        1001 to 1010 foreach {
          id =>
            service.requestIndexUpdate(AddDocToIndex(id))
        }

        Thread.sleep(1000)
      } finally {
        service.shutdown()
      }

      verify(solrServerWriter, times(20)).add(anyCollectionOf(classOf[SolrInputDocument]))
    }
  }
}
