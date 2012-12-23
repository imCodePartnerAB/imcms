package imcode.server.document.index.service.impl

import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.index.service.{IndexRebuildTask, IndexUpdateRequest, DocumentIndexService}
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.SolrQuery

object NoOpDocumentIndexService extends DocumentIndexService {

  def query(solrQuery: SolrQuery): QueryResponse = new QueryResponse()

  override def search(solrQuery: SolrQuery, searchingUser: UserDomainObject): Iterator[DocumentDomainObject] = Iterator.empty

  override def requestIndexUpdate(request: IndexUpdateRequest) {}

  override def requestIndexRebuild(): Option[IndexRebuildTask] = None

  override def indexRebuildTask(): Option[IndexRebuildTask] = None

  override def shutdown() {}
}
