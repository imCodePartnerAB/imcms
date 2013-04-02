package imcode.server.document.index.service.impl

import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.index.service.{IndexRebuildTask, IndexUpdateOp, DocumentIndexService}
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.SolrQuery
import scala.util.{Try, Failure}
import com.imcode.imcms.api.ServiceUnavailableException

object UnavailableDocumentIndexService extends DocumentIndexService {

  override def query(solrQuery: SolrQuery): Try[QueryResponse] = Failure(new ServiceUnavailableException())

  override def search(solrQuery: SolrQuery, searchingUser: UserDomainObject): Try[com.imcode.JList[DocumentDomainObject]] = Failure(new ServiceUnavailableException())

  override def update(request: IndexUpdateOp) {}

  override def rebuild(): Try[IndexRebuildTask] = Failure(new ServiceUnavailableException())

  override def currentRebuildTaskOpt(): Option[IndexRebuildTask] = None

  override def shutdown() {}
}
