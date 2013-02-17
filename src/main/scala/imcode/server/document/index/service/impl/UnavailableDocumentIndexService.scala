package imcode.server.document.index.service.impl

import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.index.service.{IndexRebuildTask, IndexUpdateRequest, DocumentIndexService}
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.SolrQuery

object UnavailableDocumentIndexService extends DocumentIndexService {

  override def query(solrQuery: SolrQuery): QueryResponse = throw new IllegalStateException("Service unavailable")

  override def search(solrQuery: SolrQuery, searchingUser: UserDomainObject): Iterator[DocumentDomainObject] = throw new IllegalStateException("Service unavailable")

  override def requestIndexUpdate(request: IndexUpdateRequest): Unit = throw new IllegalStateException("Service unavailable")

  override def requestIndexRebuild(): IndexRebuildTask = throw new IllegalStateException("Service unavailable")

  override def currentIndexRebuildTaskOpt(): Option[IndexRebuildTask] = throw new IllegalStateException("Service unavailable")

  override def shutdown() {}
}
