package imcode.server.document.index.solr

import com.imcode._
import com.imcode.Log4jLoggerSupport
import org.apache.solr.common.params.SolrParams
import org.apache.solr.client.solrj.{SolrResponse}
import org.apache.solr.client.solrj.response.QueryResponse
import imcode.server.document.index.DocumentQuery
import imcode.server.user.UserDomainObject
import imcode.server.document.DocumentDomainObject

/**
 * SOLr based Document Index Service.
 * requestXXX methods are expected to execute asynchronously.
 */
abstract class SolrDocumentIndexService extends Log4jLoggerSupport {
  def requestIndexUpdate(request: SolrDocumentIndexService.IndexUpdateRequest)
  def requestIndexRebuild()
  def search(query: DocumentQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject]
  //def search(solrParams: SolrParams): QueryResponse
  def getMonitor(): SolrDocumentIndexServiceMonitor = ???
  def shutdown()
}


object SolrDocumentIndexService {
  sealed trait IndexUpdateRequest
  case class AddDocsToIndex(metaId: Int) extends IndexUpdateRequest
  case class DeleteDocsFromIndex(metaId: Int) extends IndexUpdateRequest
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