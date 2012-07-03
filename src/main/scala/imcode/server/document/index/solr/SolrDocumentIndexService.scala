package imcode.server.document.index.solr

import com.imcode._
import com.imcode.Log4jLoggerSupport
import imcode.server.document.DocumentDomainObject
import imcode.server.user.UserDomainObject
import java.util.concurrent.{Future => JFuture}
import org.apache.solr.client.solrj.{SolrQuery}
import scala.swing.Publisher

/**
 * Defines interface for SOLr based Document Index Service.
 * requestXXX methods are expected to execute asynchronously.
 */
abstract class SolrDocumentIndexService extends Publisher with Log4jLoggerSupport { // ??? publisher ???
  def requestIndexUpdate(op: SolrDocumentIndexService.IndexUpdateOp)
  def requestIndexRebuild(): JFuture[_] // ??? IndexRebuild { def task(): Option[JFuture]; def }
  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] // ??? move searching user into wrapper ???
  def shutdown() // ??? start/stop vs start/pause/shutdown ???
}


object SolrDocumentIndexService {
  sealed trait IndexUpdateOp
  case class AddDocsToIndex(metaId: Int) extends IndexUpdateOp
  case class DeleteDocsFromIndex(metaId: Int) extends IndexUpdateOp

  //case class AddDocToIndex(doc: DocumentDomainObject) extends IndexUpdateOp
  //case class DeleteDocFromIndex(doc: DocumentDomainObject) extends IndexUpdateOp
}