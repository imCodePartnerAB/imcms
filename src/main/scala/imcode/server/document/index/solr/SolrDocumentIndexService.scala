package imcode.server.document.index.solr

import com.imcode._
import scala.reflect.BeanProperty
import scala.collection.JavaConverters._
import com.imcode.imcms.mapping.DocumentMapper
import com.imcode.Log4jLoggerSupport
import java.io.File
import imcode.server.document.DocumentDomainObject
import imcode.server.user.UserDomainObject
import org.apache.solr.common.SolrInputDocument
import scala.actors.{DaemonActor, Actor, TIMEOUT}
import java.util.concurrent.atomic.AtomicReference
import org.apache.solr.client.solrj.{SolrQuery, SolrServer}
import java.util.concurrent.{LinkedBlockingQueue, BlockingQueue, Callable, Executors, Future => JFuture}
import java.util.LinkedList
import imcode.server.document.index.DocumentIndex

abstract class SolrDocumentIndexService extends DocumentIndex with Log4jLoggerSupport {
  @BeanProperty var ops: SolrDocumentIndexServiceOps = _

  protected val indexUpdate: IndexUpdate

  protected trait IndexUpdate {
    sealed trait Event
    case class AddDocToIndex(doc: DocumentDomainObject) extends Event
    case class AddDocsToIndex(docId: Int) extends Event
    case class DeleteDocFromIndex(doc: DocumentDomainObject) extends Event
    case class DeleteDocsFromIndex(docId: Int) extends Event

    def submitEvent(event: Event)
    def requestRebuild(): JFuture[_] // IndexRebuild { def task(): Option[JFuture]; def }
  }

  def rebuild() {
    indexUpdate.requestRebuild()
  }

  def indexDocument(docId: Int) {
    indexUpdate.submitEvent(indexUpdate.AddDocsToIndex(docId))
  }

  def indexDocument(document: DocumentDomainObject) {
    indexUpdate.submitEvent(indexUpdate.AddDocToIndex(document))
  }

  def removeDocument(docId: Int) {
    indexUpdate.submitEvent(indexUpdate.DeleteDocsFromIndex(docId))
  }

  def removeDocument(document: DocumentDomainObject) {
    indexUpdate.submitEvent(indexUpdate.DeleteDocFromIndex(document))
  }

  //def getCurrentRebuild() = ...

  def shutdown()
}