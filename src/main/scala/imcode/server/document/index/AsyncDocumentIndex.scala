package imcode.server.document.index

import com.imcode._
import imcode.server.user.UserDomainObject
import imcode.server.document.DocumentDomainObject
import com.imcode.Log4jLoggerSupport
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{Future, Executors, LinkedBlockingQueue}

// todo: rebuild on timeout (IndexingSchedulePeriodInMinutes) ???
class AsyncDocumentIndex(docIndex: DocumentIndex) extends DocumentIndex with Log4jLoggerSupport {

  private trait DocOp
  private case class IndexDoc(docId: Int) extends DocOp
  private case class RemoveDoc(docId: Int) extends DocOp

  private val docOps = new LinkedBlockingQueue[DocOp]
  private val docOpsService = Executors.newSingleThreadExecutor()
  private val docRebuildService = Executors.newSingleThreadExecutor()
  private val docRebuildTaskRef = new AtomicReference[Future[_]]

  docOpsService.submit(new Runnable {
    def run() {
      while(true) {
        try {
          docOps.take() match {
            case IndexDoc(docId) => docIndex.indexDocument(docId)
            case RemoveDoc(docId) => docIndex.removeDocument(docId)
          }
        } catch {
          case _: InterruptedException => return
          case e => //todo: log and ignore
        }
      }
    }
  })

  def search(query: DocumentQuery, searchingUser: UserDomainObject) = docIndex.search(query, searchingUser)

  def indexDocument(docId: Int) {
    docOps.offer(IndexDoc(docId)) match {
      case true =>
      case _ =>
    }
  }

  def removeDocument(docId: Int) {
    docOps.offer(RemoveDoc(docId)) match {
      case true =>
      case _ =>
    }
  }


  def indexDocument(document: DocumentDomainObject) {
    indexDocument(document.getId)
  }

  def removeDocument(document: DocumentDomainObject) {
    removeDocument(document.getId)
  }

  def rebuild(): Unit = synchronized {
    docRebuildTaskRef.get() match {
      case future if future == null || future.isDone =>
        docRebuildService.submit(new Runnable {
          def run() {
            docIndex.rebuild()
          }
        }) |> docRebuildTaskRef.set

      case _ => // ignore
    }
  }
}