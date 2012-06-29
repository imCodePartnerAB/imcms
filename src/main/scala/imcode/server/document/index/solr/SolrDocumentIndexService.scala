package imcode.server.document.index.solr

import com.imcode._
import com.imcode.Log4jLoggerSupport
import imcode.server.document.DocumentDomainObject
import imcode.server.user.UserDomainObject
import java.util.concurrent.{Future => JFuture}
import scala.reflect.BeanProperty
import org.apache.solr.client.solrj.{SolrQuery}
import scala.swing.Publisher

object SolrDocumentIndexService {
  sealed trait AlterIndexRequest
  case class AddDocToIndex(doc: DocumentDomainObject) extends AlterIndexRequest
  case class AddDocsToIndex(docId: Int) extends AlterIndexRequest
  case class DeleteDocFromIndex(doc: DocumentDomainObject) extends AlterIndexRequest
  case class DeleteDocsFromIndex(docId: Int) extends AlterIndexRequest
}

// todo: should be stateless trait; remove ops setter/getter
abstract class SolrDocumentIndexService extends Publisher with Log4jLoggerSupport {
  def requestAlterIndex(request: SolrDocumentIndexService.AlterIndexRequest)
  def requestRebuildIndex(): JFuture[_] // ??? IndexRebuild { def task(): Option[JFuture]; def }
  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject]
  def shutdown()
}