package imcode.server.document.index.service

import com.imcode._
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.SolrQuery
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import scala.util.Try

/**
 *
 */
trait DocumentIndexService extends Log4jLoggerSupport {

  def query(solrQuery: SolrQuery): Try[QueryResponse]

  def search(solrQuery: SolrQuery, searchingUser: UserDomainObject): Try[JList[DocumentDomainObject]]

  def update(request: IndexUpdateOp)

  def rebuild(): Try[IndexRebuildTask]

  def rebuildIfEmpty(): Option[IndexRebuildTask] = {
    if (query(new SolrQuery("*:*")).get.getResults.isEmpty) Some(rebuild().get) else None
  }

  def currentRebuildTaskOpt(): Option[IndexRebuildTask]

  def shutdown()
}


object DocumentIndexService {
  implicit def toManagedResource[R <: DocumentIndexService] = new ManagedResource[R] {
    def close(resource: R) { resource.shutdown() }
    override def toString = "ManagedResource[_ <: DocumentIndexService]"
  }
}
