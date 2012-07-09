package imcode.server.document.index.solr

import com.imcode.{when => _, _}
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, WordSpec}
import imcode.server.document.index.{DocIndexingMocksSetup}
import com.imcode.imcms.test.fixtures.{DocFX, LanguageFX}
import org.apache.solr.common.SolrInputDocument
import java.util.concurrent.{ThreadFactory, Executors}
import java.lang.Thread
import org.apache.solr.client.solrj.SolrServer
import org.mockito.Mockito.{mock => _, _}
import org.mockito.Matchers._
import org.scalatest.mock.MockitoSugar._
import com.imcode.imcms.test._
import org.mockito.invocation.InvocationOnMock

@RunWith(classOf[JUnitRunner])
class SolrDocumentIndexServiceOpsTest extends WordSpec with BeforeAndAfterAll with BeforeAndAfter {

  object DaemonThreadFactory extends ThreadFactory {
    def newThread(r: Runnable): Thread = new Thread(r) |>> { _ setDaemon true}
  }

  val ops: SolrDocumentIndexServiceOps = {
    val ms = new DocIndexingMocksSetup

    ms.addDocuments(DocFX.mkTextDocs(DocFX.DefaultId, 10))

    new SolrDocumentIndexServiceOps(ms.docIndexer.documentMapper, ms.docIndexer)
  }

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

    "produce SOLr delete query for every document id" in pendingUntilFixed {
      for (docId <- DocFX.DefaultId until (DocFX.DefaultId + 10)) {
        expect("<undefined>", "valid SOLr delete query") {
          ops.mkSolrDeleteQuery(docId)
        }
      }
    }
  }


  "running index rebuild" should {
    "index all docs and accompilish with state eq Finished " in {
      import SolrDocumentIndexRebuild._

      val ste = Executors.newSingleThreadExecutor()
      scala.util.control.Exception.ultimately(ste.shutdown()) {
        val solrServerMock = mock[SolrServer]
        val solrDocumentIndexRebuild = ops.rebuildIndexInterruptibly(solrServerMock, ste)

        solrDocumentIndexRebuild.task.get()
        solrDocumentIndexRebuild.state() match {
          case Finished(Progress(startDt, currentDt, total, indexed)) =>
            assertEquals("total docs count should be 10", total, 10)
            assertEquals("total docs should match indexed docs count", total, indexed)

          case otherState => fail("Unexpected state: " + otherState)
        }
      }
    }

    "accompilish with state eq Cancelled when a task cancelled" in {
      import SolrDocumentIndexRebuild._

      val ste = Executors.newSingleThreadExecutor()
      scala.util.control.Exception.ultimately(ste.shutdown()) {
        val solrServerMock = mock[SolrServer]
        var solrDocumentIndexRebuild: SolrDocumentIndexRebuild = null
        // todo: introduce count = 5 before cancelling

        when(solrServerMock.add(anyCollectionOf(classOf[SolrInputDocument]))).thenAnswer { _: InvocationOnMock =>
          solrDocumentIndexRebuild.task.cancel(true)
          null
        }

        solrDocumentIndexRebuild = ops.rebuildIndexInterruptibly(solrServerMock, ste)

        intercept[java.util.concurrent.CancellationException] {
          solrDocumentIndexRebuild.task.get()
        }

        println("####### GETTING STATE")
        solrDocumentIndexRebuild.state() match {
          case Cancelled(Progress(startDt, currentDt, total, indexed)) =>
            //assertEquals("total docs count should be 10", total, 10)

          case otherState => fail("Unexpected state: " + otherState)
        }
      }
    }

  }
}