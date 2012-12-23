package imcode.server.document.index.service

import com.imcode.Log4jLoggerSupport
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.SolrQuery
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject

/**
 * requestXXX methods are expected to execute asynchronously.
 */
abstract class DocumentIndexService extends Log4jLoggerSupport {
  def query(solrQuery: SolrQuery): QueryResponse
  def search(solrQuery: SolrQuery, searchingUser: UserDomainObject): Iterator[DocumentDomainObject]
  def requestIndexUpdate(request: IndexUpdateRequest)
  def requestIndexRebuild(): Option[IndexRebuildTask]
  def indexRebuildTask(): Option[IndexRebuildTask]
  def shutdown()
}
