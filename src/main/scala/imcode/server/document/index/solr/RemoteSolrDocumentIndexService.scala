package imcode.server.document.index.solr

import java.util.concurrent.atomic.AtomicReference
import imcode.server.user.UserDomainObject
import com.imcode._
import imcode.server.document.DocumentDomainObject
import org.apache.solr.client.solrj.SolrQuery
import imcode.server.document.index.DocumentQuery
import imcode.server.document.index.solr.SolrDocumentIndexService.IndexUpdateRequest
import org.apache.solr.common.params.SolrParams
import org.apache.solr.client.solrj.response.QueryResponse

// ??? todo: wait n seconds before plugging in a new ManagedServer | Ping ???
class RemoteSolrDocumentIndexService(solrReadUrl: String, solrWriteUrl: String, ops: SolrDocumentIndexServiceOps)
    extends SolrDocumentIndexService {

  private val serviceRef: AtomicReference[SolrDocumentIndexService] = new AtomicReference(newManagedService())

  // todo: replace requestIndexRebuild flag with enum values
  private def newManagedService(requestIndexRebuild: Boolean = false): ManagedSolrDocumentIndexService = {
    val solrServerReader = SolrServerFactory.createHttpSolrServer(solrReadUrl)
    val solrServerWriter = SolrServerFactory.createHttpSolrServer(solrWriteUrl)

    new ManagedSolrDocumentIndexService(solrServerReader, solrServerWriter, ops, _ => ())
  }

  def search(solrParams: SolrParams, searchingUser: UserDomainObject): JList[DocumentDomainObject] =
    serviceRef.get().search(solrParams, searchingUser)

  def requestIndexUpdate(request: SolrDocumentIndexService.IndexUpdateRequest) {
    serviceRef.get().requestIndexUpdate(request)
  }

  def requestIndexRebuild(): Option[SolrDocumentIndexService.IndexRebuildTask] = serviceRef.get().requestIndexRebuild()

  def shutdown() {
    serviceRef.get().shutdown()
  }

  def indexRebuildTask() = None
}