package imcode.server.document.index.service.impl

import _root_.imcode.server.document.index.service.{DocumentIndexService, IndexRebuildTask, IndexUpdateOp}
import com.imcode.imcms.api.ServiceUnavailableException
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.response.QueryResponse

import scala.util.{Failure, Try}

object UnavailableDocumentIndexService extends DocumentIndexService {

  override def query(solrQuery: SolrQuery): Try[QueryResponse] = Failure(new ServiceUnavailableException())

  override def update(request: IndexUpdateOp) {}

  override def rebuild(): IndexRebuildTask = throw new ServiceUnavailableException()

  override def currentRebuildTaskOpt(): Option[IndexRebuildTask] = None

  override def shutdown() {}
}
