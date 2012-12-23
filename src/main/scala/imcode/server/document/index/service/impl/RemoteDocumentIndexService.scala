package imcode.server.document.index.service.impl

import com.imcode._
import org.apache.solr.common.params.SolrParams
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.index.service._
import org.apache.solr.client.solrj.response.QueryResponse

class RemoteDocumentIndexService(solrReadUrl: String, solrWriteUrl: String, serviceOps: DocumentIndexServiceOps)
    extends DocumentIndexService {

  private val lock = new AnyRef
  private val shutdownRef: AtomicBoolean = new AtomicBoolean(false)
  private val serviceRef: AtomicReference[DocumentIndexService] = new AtomicReference(NoOpDocumentIndexService)
  private val serviceErrorHandler: ManagedSolrDocumentIndexService.ServiceError => Unit = {
    case indexError if indexError.error.isInstanceOf[SolrInputDocumentCreateException] =>
      // ignore
    case indexError =>
      // replace service
      indexError.service |> { service => lock.synchronized {
        if (serviceRef.compareAndSet(service, NoOpDocumentIndexService)) {
          logger.info("Index error has occuerd. Managed service instance have to be replaced.", indexError.error)
          service.shutdown()

          if (shutdownRef.get()) {
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


  override def query(solrParams: SolrParams): QueryResponse = {
    serviceRef.get().query(solrParams)
  }


  override def search(solrParams: SolrParams, searchingUser: UserDomainObject): Iterator[DocumentDomainObject] = {
    serviceRef.get().search(solrParams, searchingUser)
  }


  override def requestIndexUpdate(request: IndexUpdateRequest) {
    serviceRef.get().requestIndexUpdate(request)
  }


  override def requestIndexRebuild(): Option[IndexRebuildTask] = serviceRef.get().requestIndexRebuild()


  override def indexRebuildTask(): Option[IndexRebuildTask] = serviceRef.get().indexRebuildTask()


  override def shutdown(): Unit = lock.synchronized {
    if (shutdownRef.compareAndSet(false, true)) {
      logger.info("Attempting to shtting down the service.")
      serviceRef.getAndSet(NoOpDocumentIndexService).shutdown()
      logger.info("Service has been shut down.")
    }
  }
}