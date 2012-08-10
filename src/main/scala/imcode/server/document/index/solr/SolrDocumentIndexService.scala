package imcode.server.document.index.solr

import com.imcode._
import com.imcode.Log4jLoggerSupport
import org.apache.solr.common.params.SolrParams
import imcode.server.user.UserDomainObject
import imcode.server.document.DocumentDomainObject
import java.util.concurrent.Future
import java.util.Collections

/**
 * SOLr based Document Index Service.
 * requestXXX methods are expected to execute asynchronously.
 */
abstract class SolrDocumentIndexService extends Log4jLoggerSupport {
  def search(solrParams: SolrParams, searchingUser: UserDomainObject): JList[DocumentDomainObject]
  def requestIndexUpdate(request: SolrDocumentIndexService.IndexUpdateRequest)
  def requestIndexRebuild(): Option[SolrDocumentIndexService.IndexRebuildTask] // LEFT[UNAVAILABLE MSG] RIGHT[INDEX_REBUILD_TASK]
  def indexRebuildTask(): Option[SolrDocumentIndexService.IndexRebuildTask]
  def shutdown()
}


object SolrDocumentIndexService {
  trait IndexRebuildTask {
    def future(): Future[_]
    def progress(): Option[SolrDocumentIndexService.IndexRebuildProgress]
  }

  case class IndexRebuildProgress(startTimeMillis: Long, currentTimeMillis: Long, totalDocsCount: Int, indexedDocsCount: Int)

  sealed trait IndexUpdateRequest
  case class AddDocsToIndex(docId: Int) extends IndexUpdateRequest
  case class DeleteDocsFromIndex(docId: Int) extends IndexUpdateRequest
}


object NoOpSolrDocumentIndexService extends SolrDocumentIndexService {

  def search(solrParams: SolrParams, searchingUser: UserDomainObject): JList[DocumentDomainObject] = Collections.emptyList()

  def requestIndexUpdate(request: SolrDocumentIndexService.IndexUpdateRequest) {}

  def requestIndexRebuild(): Option[SolrDocumentIndexService.IndexRebuildTask] = None

  def indexRebuildTask(): Option[SolrDocumentIndexService.IndexRebuildTask] = None

  def shutdown() {}
}