package imcode.server.document.index.service

import com.imcode._
import com.imcode.Log4jLoggerSupport
import org.apache.solr.common.params.SolrParams
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject

/**
 * requestXXX methods are expected to execute asynchronously.
 */
abstract class DocumentIndexService extends Log4jLoggerSupport {
  def search(solrParams: SolrParams, searchingUser: UserDomainObject): JList[DocumentDomainObject]
  def requestIndexUpdate(request: IndexUpdateRequest)
  def requestIndexRebuild(): Option[IndexRebuildTask]
  def indexRebuildTask(): Option[IndexRebuildTask]
  def shutdown()
}
