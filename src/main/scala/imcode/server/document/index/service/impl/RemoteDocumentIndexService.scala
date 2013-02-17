package imcode.server.document.index.service.impl

import com.imcode._
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.index.service._
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.SolrQuery

class RemoteDocumentIndexService(solrReadUrl: String, solrWriteUrl: String, serviceOps: DocumentIndexServiceOps)
    extends DocumentIndexService {

  private val lock = new AnyRef
  private val shutdownRef: AtomicBoolean = new AtomicBoolean(false)
  private val serviceRef: AtomicReference[DocumentIndexService] = new AtomicReference(UnavailableDocumentIndexService)
  private val serviceErrorHandler: ManagedSolrDocumentIndexService.ServiceFailure => Unit = {
    case indexError if indexError.exception.isInstanceOf[SolrInputDocumentCreateException] =>
      // ignore
    case indexError =>
      // replace service
      indexError.service |> { service => lock.synchronized {
        if (serviceRef.compareAndSet(service, UnavailableDocumentIndexService)) {
          logger.info("Index error has occuerd. Managed service instance have to be replaced.", indexError.exception)
          service.shutdown()

          if (shutdownRef.get) {
            logger.info("New managed service instance will not be created - service has been shout down.")
          } else {
            logger.info("Creating new instance of managed service. Data directory will be recreated.")
            newManagedService() |> { newService =>
              serviceRef.set(newService)
              newService.requestIndexRebuild()
            }

            logger.info("New managed service instance has been created.")
          }
        }
      }
    }
  }

  newManagedService() |> serviceRef.set


  private def newManagedService(): ManagedSolrDocumentIndexService = {
    val solrServerReader = SolrServerFactory.createHttpSolrServer(solrReadUrl)
    val solrServerWriter = SolrServerFactory.createHttpSolrServer(solrWriteUrl)

    new ManagedSolrDocumentIndexService(solrServerReader, solrServerWriter, serviceOps, serviceErrorHandler)
  }


  override def query(solrQuery: SolrQuery): QueryResponse = {
    serviceRef.get.query(solrQuery)
  }


  override def search(solrQuery: SolrQuery, searchingUser: UserDomainObject): Iterator[DocumentDomainObject] = {
    serviceRef.get.search(solrQuery, searchingUser)
  }


  override def requestIndexUpdate(request: IndexUpdateRequest) {
    serviceRef.get.requestIndexUpdate(request)
  }


  override def requestIndexRebuild(): Option[IndexRebuildTask] = serviceRef.get.requestIndexRebuild()


  override def currentIndexRebuildTaskOpt(): Option[IndexRebuildTask] = serviceRef.get.currentIndexRebuildTaskOpt()


  override def shutdown(): Unit = lock.synchronized {
    if (shutdownRef.compareAndSet(false, true)) {
      logger.info("Attempting to shtting down the service.")
      serviceRef.getAndSet(UnavailableDocumentIndexService).shutdown()
      logger.info("Service has been shut down.")
    }
  }
}