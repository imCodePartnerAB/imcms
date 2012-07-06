package imcode.server.document.index.solr

import scala.actors.Actor
import scala.actors.Actor._
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{Executors, Future =>JFuture, LinkedBlockingQueue}
import imcode.server.user.UserDomainObject
import com.imcode._
import imcode.server.document.DocumentDomainObject
import org.apache.solr.client.solrj.SolrQuery
import imcode.server.document.index.DocumentQuery
import imcode.server.document.index.solr.SolrDocumentIndexService.IndexUpdateOp

// todo: implement
// todo: ??? add read-only solrServerUrl - i.e. (solrUrl: String, solrUrlReadOnly: String)
class RemoteSolrDocumentIndexService(solrUrl: String, ops: SolrDocumentIndexServiceOps) extends SolrDocumentIndexService {

  private val serviceRef: AtomicReference[SolrDocumentIndexService] = new AtomicReference(newManagedService())

  // todo: replace requestIndexRebuild flag with enum values
  private def newManagedService(requestIndexRebuild: Boolean = false): ManagedSolrDocumentIndexService = {
    val solrServerReader = SolrServerFactory.createHttpSolrServer(solrUrl)
    val solrServerWriter = SolrServerFactory.createHttpSolrServer(solrUrl)

    new ManagedSolrDocumentIndexService(solrServerReader, solrServerWriter, ops) |>> { service =>
      listenTo(service)
      serviceRef.set(service)
      if (requestIndexRebuild) {
        service.requestIndexRebuild()
      }
    }
  }

  def requestIndexRebuild() = null

  def requestIndexUpdate(op: IndexUpdateOp) = null

  def search(query: SolrQuery, searchingUser: UserDomainObject) = null

  def shutdown() = null
}