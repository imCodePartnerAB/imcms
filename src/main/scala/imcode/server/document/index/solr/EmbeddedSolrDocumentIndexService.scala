package imcode.server.document.index.solr

import _root_.com.imcode._
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import java.util.Collections
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import org.apache.solr.common.params.SolrParams

/**
 * Delegates all invocations to the ManagedSolrDocumentIndexService instance.
 * In case of an indexing error replaces managed instance with new one and re-indexes documents.
 */
class EmbeddedSolrDocumentIndexService(solrHome: String, serviceOps: SolrDocumentIndexServiceOps)
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
            newManagedService(recreateDataDir = true) |> { newService =>
              managedServiceRef.set(newService)
              newService.requestIndexRebuild()
            }
          }
        }
      }
    }
  }

  newManagedService(recreateDataDir = false) |> managedServiceRef.set


  def search(solrParams: SolrParams, searchingUser: UserDomainObject): JList[DocumentDomainObject] =
    managedServiceRef.get().search(solrParams, searchingUser)


  def requestIndexUpdate(request: SolrDocumentIndexService.IndexUpdateRequest) {
    managedServiceRef.get().requestIndexUpdate(request)
  }


  def requestIndexRebuild(): Option[SolrDocumentIndexService.IndexRebuildTask] = managedServiceRef.get().requestIndexRebuild()

  def indexRebuildTask(): Option[SolrDocumentIndexService.IndexRebuildTask] = managedServiceRef.get().indexRebuildTask()

  def shutdown(): Unit = lock.synchronized {
    if (shutdownRef.compareAndSet(false, true)) {
      shutdownManagedService(managedServiceRef.getAndSet(NoOpSolrDocumentIndexService))
    }
  }


  private def newManagedService(recreateDataDir: Boolean): ManagedSolrDocumentIndexService = {
    val solrServer = SolrServerFactory.createEmbeddedSolrServer(solrHome, recreateDataDir)

    new ManagedSolrDocumentIndexService(solrServer, solrServer, serviceOps, managedServiceErrorHandler)
  }


  private def shutdownManagedService(service: SolrDocumentIndexService) {
    if (service ne NoOpSolrDocumentIndexService) {
      service.shutdown()
    }
  }
}