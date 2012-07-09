package imcode.server.document.index.solr

import com.imcode._
import com.imcode.Log4jLoggerSupport
import imcode.server.document.DocumentDomainObject
import imcode.server.user.UserDomainObject
import org.apache.solr.client.solrj.{SolrQuery}
import scala.swing.Publisher
import java.util.concurrent.Future

/**
 * Defines interface for SOLr based Document Index Service.
 * requestXXX methods are expected to execute asynchronously.
 */
// todo: remove publisher, use monitor for events publishing
abstract class SolrDocumentIndexService extends Publisher with Log4jLoggerSupport {
  def requestIndexUpdate(op: SolrDocumentIndexService.IndexUpdateOp)
  def requestIndexRebuild()
  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] // ??? move searching user into wrapper ???
  def getMonitor(): SolrDocumentIndexServiceMonitor = ???
  def shutdown()
}


object SolrDocumentIndexService {
  sealed trait IndexUpdateOp
  case class AddDocsToIndex(metaId: Int) extends IndexUpdateOp
  case class DeleteDocsFromIndex(metaId: Int) extends IndexUpdateOp

  //case class AddDocToIndex(doc: DocumentDomainObject) extends IndexUpdateOp
  //case class DeleteDocFromIndex(doc: DocumentDomainObject) extends IndexUpdateOp
}


trait SolrDocumentIndexServiceMonitor {
  // indexRebuildActivity: opt SolrDocumentIndexRebuildActivity|Monitor
  def indexRebuild(): SolrDocumentIndexRebuild
}


// SolrDocumentIndexRebuildMonitor
abstract class SolrDocumentIndexRebuild {
  //val future: Future[_]
  //def progress(): SolrDocumentIndexRebuild.Progress
}


object SolrDocumentIndexRebuild {
  // startTimeMillis: Long, currentTimeMillis: Long,
  case class Progress(totalDocsCount: Int, indexedDocsCount: Int)
}