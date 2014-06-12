package imcode.server.document.index.service.impl

import com.imcode._
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import imcode.server.document.index.service._
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.SolrQuery
import imcode.server.document.index.service.impl.ManagedDocumentIndexService.{SearchFailure, WriteFailure}
import scala.util.Try
import org.apache.solr.common.SolrException

/**
 * Delegates all invocations to the ManagedSolrDocumentIndexService instance.
 * In case of an indexing error replaces managed instance with new one and re-indexes documents.
 */
class InternalDocumentIndexService(solrHome: String, serviceOps: DocumentIndexServiceOps) extends DocumentIndexService {

  private val lock = new AnyRef
  private val shutdownRef: AtomicBoolean = new AtomicBoolean(false)
  private val serviceRef: AtomicReference[DocumentIndexService] = new AtomicReference(UnavailableDocumentIndexService)
  private val failureHandler: (ManagedDocumentIndexService.ServiceFailure => Unit) = {
    case failure@SearchFailure(service, exception) =>
      import SolrException.ErrorCode
      import SolrException.ErrorCode._
      exception match {
        case e: SolrException if Set(BAD_REQUEST).contains(ErrorCode.getErrorCode(e.code)) => logger.warn("Bad search request", e)
        case e: SolrException if Set(SERVER_ERROR, FORBIDDEN, UNAUTHORIZED, NOT_FOUND).contains(ErrorCode.getErrorCode(e.code)) =>
          logger.fatal("Configuration error. Shutting down the service.", e)
          lock.synchronized {
            if (serviceRef.get() == service) {
              shutdown()
            }
          }
        case _ => replaceManagedServerInstance(failure)
      }

    case failure: WriteFailure =>
      failure.exception match {
        case e: SolrInputDocumentCreateException =>
          logger.fatal("Unexpected system error. Unable to create SolrInputDocument. No more index update or rebuild requests will be accepted.", e)
        //fixme: review
        //case _ => replaceManagedServerInstance(failure)
      }
  }

  newManagedService(recreateDataDir = false) |> serviceRef.set


  private def replaceManagedServerInstance(failure: ManagedDocumentIndexService.ServiceFailure): Unit = lock.synchronized {
    import failure.service

    if (serviceRef.compareAndSet(service, UnavailableDocumentIndexService)) {
      logger.error("Unrecoverable index error. Managed service instance have to be replaced.", failure.exception)
      service.shutdown()

      if (shutdownRef.get) {
        logger.info("New managed service instance can not be created - service has been shout down.")
      } else {
        logger.info("Creating new instance of managed service. Data directory will be recreated.")
        newManagedService(recreateDataDir = true) |> {
          newService =>
            serviceRef.set(newService)
            newService.rebuild()
        }

        logger.info("New managed service instance has been created.")
      }
    }
  }

  private def newManagedService(recreateDataDir: Boolean): ManagedDocumentIndexService = {
    val solrServer = SolrServerFactory.createEmbeddedSolrServer(solrHome, recreateDataDir)

    new ManagedDocumentIndexService(solrServer, solrServer, serviceOps, failureHandler)
  }

  override def query(solrQuery: SolrQuery): Try[QueryResponse] = serviceRef.get.query(solrQuery)

  override def update(request: IndexUpdateOp): Unit = serviceRef.get.update(request)

  override def rebuild(): Try[IndexRebuildTask] = serviceRef.get.rebuild()

  override def currentRebuildTaskOpt(): Option[IndexRebuildTask] = serviceRef.get.currentRebuildTaskOpt()

  override def shutdown(): Unit = lock.synchronized {
    if (shutdownRef.compareAndSet(false, true)) {
      logger.info("Attempting to shut down the service.")
      Try(serviceRef.getAndSet(UnavailableDocumentIndexService).shutdown())
      logger.info("Service has been shut down.")
    }
  }
}