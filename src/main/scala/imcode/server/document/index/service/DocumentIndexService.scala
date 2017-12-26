package imcode.server.document.index.service

import java.lang.Long

import com.imcode._
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.response.QueryResponse

trait DocumentIndexService {

  def query(solrQuery: SolrQuery): QueryResponse

  /**
   * Updates index asynchronously.
   *
   * @param request index update request.
   */
  def update(request: IndexUpdateOp)

  /**
   * Attempts to run a new index rebuild task if one is not already running.
   *
   * @return current or a new rebuild task.
   */
  def rebuild(): IndexRebuildTask

  /**
   * @return None if index is not empty or Some(attempt to rebuild).
   */
  def rebuildIfEmpty(): Option[IndexRebuildTask] = {
    if (count == 0L) Some(rebuild()) else None
  }

  // todo: optimize
  def count: Long = query(new SolrQuery("*:*")).getResults.getNumFound

  /**
   * @return current rebuild task
   */
  def currentRebuildTaskOpt(): Option[IndexRebuildTask]

  def shutdown()
}


object DocumentIndexService {
  implicit def toManagedResource[R <: DocumentIndexService] = new ManagedResource[R] {
    def close(resource: R) {
      resource.shutdown()
    }

    override def toString = "ManagedResource[_ <: DocumentIndexService]"
  }
}
