package imcode.server.document.index.service.impl

import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.index.service.{IndexRebuildTask, IndexUpdateRequest, DocumentIndexService}
import org.apache.solr.common.params.SolrParams
import org.apache.solr.client.solrj.response.QueryResponse

object NoOpDocumentIndexService extends DocumentIndexService {

  def query(solrParams: SolrParams): QueryResponse = new QueryResponse()

  override def search(solrParams: SolrParams, searchingUser: UserDomainObject): Iterator[DocumentDomainObject] = Iterator.empty

  override def requestIndexUpdate(request: IndexUpdateRequest) {}

  override def requestIndexRebuild(): Option[IndexRebuildTask] = None

  override def indexRebuildTask(): Option[IndexRebuildTask] = None

  override def shutdown() {}
}
