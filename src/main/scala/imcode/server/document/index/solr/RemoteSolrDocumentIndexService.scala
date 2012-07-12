package imcode.server.document.index.solr

import java.util.concurrent.atomic.AtomicReference
import imcode.server.user.UserDomainObject
import com.imcode._
import imcode.server.document.DocumentDomainObject
import org.apache.solr.client.solrj.SolrQuery
import imcode.server.document.index.DocumentQuery
import imcode.server.document.index.solr.SolrDocumentIndexService.IndexUpdateOp
import scala.swing.{Reactor, Publisher}

// ??? todo: wait n seconds before plugging in a new ManagedServer | Ping ???
class RemoteSolrDocumentIndexService(solrReadUrl: String, solrWriteUrl: String, ops: SolrDocumentIndexServiceOps)
    extends SolrDocumentIndexService with Reactor {

  private val serviceRef: AtomicReference[SolrDocumentIndexService with Publisher] = new AtomicReference(newManagedService())

  reactions += {
    // swap target service
    case ManagedSolrDocumentIndexService.IndexError(publisher, error) =>
      serviceRef.synchronized {
        serviceRef.get() match {
          case service if service eq publisher =>
            deafTo(service)
            serviceRef.set(NoOpSolrDocumentIndexService)
            service.shutdown()

            serviceRef.set(newManagedService(requestIndexRebuild = true))
          case _ =>
        }
      }
  }

  // todo: replace requestIndexRebuild flag with enum values
  private def newManagedService(requestIndexRebuild: Boolean = false): ManagedSolrDocumentIndexService = {
    val solrServerReader = SolrServerFactory.createHttpSolrServer(solrReadUrl)
    val solrServerWriter = SolrServerFactory.createHttpSolrServer(solrWriteUrl)

    new ManagedSolrDocumentIndexService(solrServerReader, solrServerWriter, ops) |>> { service =>
      listenTo(service)
      serviceRef.set(service)
      if (requestIndexRebuild) {
        service.requestIndexRebuild()
      }
    }
  }

  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] =
    serviceRef.get().search(query, searchingUser)

  def requestIndexUpdate(op: SolrDocumentIndexService.IndexUpdateOp) {
    serviceRef.get().requestIndexUpdate(op)
  }

  def requestIndexRebuild() {
    serviceRef.get().requestIndexRebuild()
  }

  def shutdown() {
    serviceRef.get().shutdown()
  }
}