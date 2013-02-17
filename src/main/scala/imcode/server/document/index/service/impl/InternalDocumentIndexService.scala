package imcode.server.document.index.service.impl

import _root_.com.imcode._
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import imcode.server.document.index.service._
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.SolrQuery

/**
 * Delegates all invocations to the ManagedSolrDocumentIndexService instance.
 * In case of an indexing error replaces managed instance with new one and re-indexes documents.
 */
class InternalDocumentIndexService(solrHome: String, serviceOps: DocumentIndexServiceOps)
    extends DocumentIndexService {

  private val lock = new AnyRef
  private val shutdownRef: AtomicBoolean = new AtomicBoolean(false)
  private val serviceRef: AtomicReference[DocumentIndexService] = new AtomicReference(UnavailableDocumentIndexService)
  private val serviceErrorHandler: (ManagedSolrDocumentIndexService.ServiceFailure => Unit) = {
    case indexError if indexError.exception.isInstanceOf[SolrInputDocumentCreateException] =>
      // ignore
    case indexError =>
      // replace service
      indexError.service |> { service => lock.synchronized {
        if (serviceRef.compareAndSet(service, UnavailableDocumentIndexService)) {
          logger.info("Index error has occurred. Managed service instance have to be replaced.", indexError.exception)
          service.shutdown()

          if (shutdownRef.get) {
            logger.info("New managed service instance can not be created - service has been shout down.")
          } else {
            logger.info("Creating new instance of managed service. Data directory will be recreated.")
            newManagedService(recreateDataDir = true) |> { newService =>
              serviceRef.set(newService)
              newService.requestIndexRebuild()
            }

            logger.info("New managed service instance has been created.")
          }
        }
      }
    }
  }

  newManagedService(recreateDataDir = false) |> serviceRef.set


  private def newManagedService(recreateDataDir: Boolean): ManagedSolrDocumentIndexService = {
    val solrServer = SolrServerFactory.createEmbeddedSolrServer(solrHome, recreateDataDir)

    new ManagedSolrDocumentIndexService(solrServer, solrServer, serviceOps, serviceErrorHandler)
  }


  override def query(solrQuery: SolrQuery): QueryResponse = serviceRef.get.query(solrQuery)


  override def search(solrQuery: SolrQuery, searchingUser: UserDomainObject): Iterator[DocumentDomainObject] = {
    serviceRef.get.search(solrQuery, searchingUser)
  }


  override def requestIndexUpdate(request: IndexUpdateRequest): Unit = serviceRef.get.requestIndexUpdate(request)


  override def requestIndexRebuild(): Option[IndexRebuildTask] = serviceRef.get.requestIndexRebuild()


  override def currentIndexRebuildTaskOpt(): Option[IndexRebuildTask] = serviceRef.get.currentIndexRebuildTaskOpt()


  override def shutdown(): Unit = lock.synchronized {
    if (shutdownRef.compareAndSet(false, true)) {
      logger.info("Attempting to shut down the service.")
      serviceRef.getAndSet(UnavailableDocumentIndexService).shutdown()
      logger.info("Service has been shut down.")
    }
  }
}