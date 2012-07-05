package imcode.server.document.index.solr

import com.imcode._
import com.imcode.Log4jLoggerSupport
import imcode.server.document.DocumentDomainObject
import imcode.server.user.UserDomainObject
import java.util.concurrent.{Future => JFuture}
import org.apache.solr.client.solrj.{SolrQuery}
import scala.swing.Publisher
import java.util.concurrent.atomic.AtomicReference
import java.util.Date

/**
 * Defines interface for SOLr based Document Index Service.
 * requestXXX methods are expected to execute asynchronously.
 */
abstract class SolrDocumentIndexService extends Publisher with Log4jLoggerSupport { // ??? publisher ???
  def requestIndexUpdate(op: SolrDocumentIndexService.IndexUpdateOp)
  def requestIndexRebuild()
  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] // ??? move searching user into wrapper ???
  def shutdown() // ??? start/stop vs start/pause/shutdown ???
  def monitor(): SolrDocumentIndexServiceMonitor = ???
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

  def rebuildTask: Option[JFuture[RebuildTaskState]]

  def rebuildTaskState: RebuildTaskState
}


object SolrDocumentIndexServiceMonitor {
  //object RebuildTask { ???
  case class Progress(total: Int, indexed: Int) // doc: DocumentDomainObject ??? ProgressSnapshot

  sealed trait RebuildTaskState
  case object Idle extends RebuildTaskState
  case class Running(startedDt: Date, total: Int, indexed: Int) extends RebuildTaskState
  case class Cancelled(startedDt: Date, cancelledDt: Date, total: Int, indexed: Int) extends RebuildTaskState
  case class Failed(startedDt: Date, failedDt: Date, failure: Throwable, total: Int, indexed: Int) extends RebuildTaskState
  case class Finished(startedDt: Date, finishedDt: Date, total: Int)
}