package imcode.server.document.index.solr

import com.imcode._
import scala.collection.JavaConverters._
import com.imcode.imcms.mapping.DocumentMapper
import com.imcode.Log4jLoggerSupport
import java.io.File
import imcode.server.document.DocumentDomainObject
import imcode.server.user.UserDomainObject
import org.apache.solr.common.SolrInputDocument
import scala.actors.{DaemonActor, Actor, TIMEOUT}
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{LinkedBlockingQueue, BlockingQueue, Callable, Executors, Future => JFuture}
import java.util.LinkedList
import scala.reflect.BeanProperty
import imcode.server.document.index.{DocumentQuery, DocumentIndex}
import org.apache.solr.client.solrj.{SolrQuery, SolrServer}


abstract class SolrDocumentIndexService extends Log4jLoggerSupport {
  sealed trait AlterRequest
  case class AddDocToIndex(doc: DocumentDomainObject) extends AlterRequest
  case class AddDocsToIndex(docId: Int) extends AlterRequest
  case class DeleteDocFromIndex(doc: DocumentDomainObject) extends AlterRequest
  case class DeleteDocsFromIndex(docId: Int) extends AlterRequest

  @BeanProperty
  var ops: SolrDocumentIndexServiceOps = _

  def requestAlter(request: AlterRequest)
  def requestRebuild(): JFuture[_] // ??? IndexRebuild { def task(): Option[JFuture]; def }
  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject]
  def shutdown() // ???
}