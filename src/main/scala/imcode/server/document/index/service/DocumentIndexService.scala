package imcode.server.document.index.service

import com.imcode.Log4jLoggerSupport
import org.apache.solr.common.params.SolrParams
import org.apache.solr.client.solrj.response.QueryResponse
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject

/**
 * requestXXX methods are expected to execute asynchronously.
 */
abstract class DocumentIndexService extends Log4jLoggerSupport {
  def query(solrParams: SolrParams): QueryResponse
  def search(solrParams: SolrParams, searchingUser: UserDomainObject): Iterator[DocumentDomainObject]
  def requestIndexUpdate(request: IndexUpdateRequest)
  def requestIndexRebuild(): Option[IndexRebuildTask]
  def indexRebuildTask(): Option[IndexRebuildTask]
  def shutdown()
}
