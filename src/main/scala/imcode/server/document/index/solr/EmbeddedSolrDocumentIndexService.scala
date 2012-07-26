package imcode.server.document.index.solr

import java.io.File
import imcode.server.user.UserDomainObject
import com.imcode._
import imcode.server.document.DocumentDomainObject
import org.apache.solr.client.solrj.SolrQuery
import java.util.Collections
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}


/**
 * Delegates all invocations to the ManagedSolrDocumentIndexService instance.
 * In case of an indexing error replaces managed instance with new one and re-indexes documents.
 */
class EmbeddedSolrDocumentIndexService(solrHome: File, serviceOps: SolrDocumentIndexServiceOps)
  extends SolrDocumentIndexService {

  private val lock = new AnyRef
  private val shutdownRef: AtomicBoolean = new AtomicBoolean(false)
  private val managedServiceRef: AtomicReference[SolrDocumentIndexService] = new AtomicReference(NoOpSolrDocumentIndexService)
  private val managedServiceErrorHandler: ManagedSolrDocumentIndexService.ServiceError => Unit = {
    // swap target service on error
    case indexError: ManagedSolrDocumentIndexService.ServiceError => indexError.service |> { service =>
      lock.synchronized {
        if (managedServiceRef.compareAndSet(service, NoOpSolrDocumentIndexService)) {
          shutdownManagedService(service)

          if (!shutdownRef.get()) {
            newManagedService() |> { newService =>
              managedServiceRef.set(newService)
              newService.requestIndexRebuild()
            }
          }
        }
      }
    }
  }

  newManagedService() |> managedServiceRef.set


  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] =
    managedServiceRef.get().search(query, searchingUser)

  def requestIndexUpdate(request: SolrDocumentIndexService.IndexUpdateRequest) {
    managedServiceRef.get().requestIndexUpdate(request)
  }

  def requestIndexRebuild() {
    managedServiceRef.get().requestIndexRebuild()
  }

  def shutdown(): Unit = lock.synchronized {
    if (shutdownRef.compareAndSet(false, true)) {
      shutdownManagedService(managedServiceRef.getAndSet(NoOpSolrDocumentIndexService))
    }
  }


  private def newManagedService(): ManagedSolrDocumentIndexService = {
    val solrServerReader = SolrServerFactory.createEmbeddedSolrServer(solrHome)
    val solrServerWriter = SolrServerFactory.createEmbeddedSolrServer(solrHome)

    new ManagedSolrDocumentIndexService(solrServerReader, solrServerWriter, serviceOps, managedServiceErrorHandler)
  }


  private def shutdownManagedService(service: SolrDocumentIndexService) {
    if (service ne NoOpSolrDocumentIndexService) {
      service.shutdown()
    }
  }
}


/**
 * rebuild monitor - Service UNAVAILABLE | IDLE | Monitor -vs- Option[Monitor]
 */
object NoOpSolrDocumentIndexService extends SolrDocumentIndexService {

  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = Collections.emptyList()

  def requestIndexUpdate(request: SolrDocumentIndexService.IndexUpdateRequest) {}

  def requestIndexRebuild() {}

  def shutdown() {}
}