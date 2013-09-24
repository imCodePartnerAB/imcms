package imcode.server.document.index.service

import com.imcode._
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.SolrQuery
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import scala.util.{Failure, Success, Try}

/**
 *
 */
trait DocumentIndexService extends Log4jLoggerSupport {

  def query(solrQuery: SolrQuery): Try[QueryResponse]

  def search(solrQuery: SolrQuery, searchingUser: UserDomainObject): Try[JList[DocumentDomainObject]]

  /**
   * Updates index asynchronously.
   *
   * @param request index update request.
   */
  def update(request: IndexUpdateOp)

  /**
   * Attempts to run a new index rebuild task if one is not already running.
   *
   * @return current or new rebuild task.
   */
  def rebuild(): Try[IndexRebuildTask]

  /**
   * @return None if index is not empty or Some(attempt to rebuild).
   */
  final def rebuildIfEmpty(): Option[Try[IndexRebuildTask]] = {
    query(new SolrQuery("*:*")) match {
      case Success(queryResponse) => if (!queryResponse.getResults.isEmpty) None else Some(rebuild())
      case Failure(throwable) => Some(Failure(throwable))
    }
  }

  /**
   * @return current rebuild task
   */
  def currentRebuildTaskOpt(): Option[IndexRebuildTask]

  def shutdown()
}


object DocumentIndexService {
  implicit def toManagedResource[R <: DocumentIndexService] = new ManagedResource[R] {
    def close(resource: R) { resource.shutdown() }
    override def toString = "ManagedResource[_ <: DocumentIndexService]"
  }
}
