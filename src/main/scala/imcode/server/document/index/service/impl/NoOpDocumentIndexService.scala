package imcode.server.document.index.service.impl

import com.imcode._
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.index.service.{IndexRebuildTask, IndexUpdateRequest, DocumentIndexService}
import java.util.Collections
import org.apache.solr.common.params.SolrParams

object NoOpDocumentIndexService extends DocumentIndexService {

  def search(solrParams: SolrParams, searchingUser: UserDomainObject): JList[DocumentDomainObject] = Collections.emptyList()

  def requestIndexUpdate(request: IndexUpdateRequest) {}

  def requestIndexRebuild(): Option[IndexRebuildTask] = None

  def indexRebuildTask(): Option[IndexRebuildTask] = None

  def shutdown() {}
}
