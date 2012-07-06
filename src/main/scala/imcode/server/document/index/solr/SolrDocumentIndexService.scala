package imcode.server.document.index.solr

import com.imcode._
import com.imcode.Log4jLoggerSupport
import imcode.server.document.DocumentDomainObject
import imcode.server.user.UserDomainObject
import org.apache.solr.client.solrj.{SolrQuery}
import scala.swing.Publisher
import java.util.Date
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{Future, Future => JFuture}

/**
 * Defines interface for SOLr based Document Index Service.
 * requestXXX methods are expected to execute asynchronously.
 */
// todo: remove publisher, use monitor for events publishing
abstract class SolrDocumentIndexService extends Publisher with Log4jLoggerSupport {
  def requestIndexUpdate(op: SolrDocumentIndexService.IndexUpdateOp)
  def requestIndexRebuild()
  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] // ??? move searching user into wrapper ???
  def shutdown()
  def getMonitor(): SolrDocumentIndexServiceMonitor = ???
}


object SolrDocumentIndexService {
  sealed trait IndexUpdateOp
  case class AddDocsToIndex(metaId: Int) extends IndexUpdateOp
  case class DeleteDocsFromIndex(metaId: Int) extends IndexUpdateOp

  //case class AddDocToIndex(doc: DocumentDomainObject) extends IndexUpdateOp
  //case class DeleteDocFromIndex(doc: DocumentDomainObject) extends IndexUpdateOp
}


trait SolrDocumentIndexServiceMonitor {
  import SolrDocumentIndexServiceMonitor._

  // private val rebuildTaskRef: AtomicReference[JFuture[RebuildTaskState]]
  // private val rebuildTaskStateRef: AtomicReference[RebuildTaskState]

  def rebuildTask: Option[JFuture[RebuildState]]

  def rebuildTaskState: RebuildState
}


class SolrDocumentIndexServiceMonitorImpl extends SolrDocumentIndexServiceMonitor with Log4jLoggerSupport {
  import SolrDocumentIndexServiceMonitor._

  private val rebuildTaskRef: AtomicReference[JFuture[RebuildState]] = new AtomicReference(null)
  private val rebuildTaskStateRef: AtomicReference[RebuildState] = new AtomicReference(Idle)

  def rebuildTask: Option[JFuture[RebuildState]] = Option(rebuildTaskRef.get())

  def rebuildTaskState: RebuildState = rebuildTaskStateRef.get()

  def setRebuildTask(task: JFuture[RebuildState]) {
    rebuildTaskRef.set(task)
    // publish asyncronously
  }

  def setRebuildTaskState(state: RebuildState) {
    rebuildTaskStateRef.set(state)
    // publish asyncronously
  }


  def indexRebuildState: (Option[JFuture[_], ])
}


object SolrDocumentIndexServiceMonitor {
  //object RebuildTask { ???
  case class Progress(total: Int, indexed: Int) // doc: DocumentDomainObject ??? ProgressSnapshot

  sealed trait RebuildState
  case object Idle extends RebuildState
  case class Running(startedDt: Date, total: Int, indexed: Int) extends RebuildState
  case class Cancelled(startedDt: Date, cancelledDt: Date, total: Int, indexed: Int) extends RebuildState
  case class Failed(startedDt: Date, failedDt: Date, failure: Throwable, total: Int, indexed: Int) extends RebuildState
  case class Finished(startedDt: Date, finishedDt: Date, total: Int)
}