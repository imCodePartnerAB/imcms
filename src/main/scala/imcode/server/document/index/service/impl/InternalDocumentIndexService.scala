package imcode.server.document.index.service.impl

import java.io.IOException
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import _root_.imcode.server.document.index.service.{IndexRebuildTask, IndexUpdateOp, SolrServerFactory, DocumentIndexService}
import com.imcode._
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.{SolrQuery, SolrServerException}
import org.apache.solr.common.SolrException

import scala.util.Try

/**
 * Delegates all invocations to the ManagedSolrDocumentIndexService instance.
 * In case of a fatal indexing error replaces managed instance with a new and re-indexes all documents.
 */
class InternalDocumentIndexService(solrHome: String, serviceOps: DocumentIndexServiceOps) extends DocumentIndexService {

  private val lock = new AnyRef
  private val shutdownRef: AtomicBoolean = new AtomicBoolean(false)
  private val serviceRef: AtomicReference[DocumentIndexService] = new AtomicReference(UnavailableDocumentIndexService)
  private val failureHandler: (ServiceFailure => Unit) = { failure: ServiceFailure =>
    import org.apache.solr.common.SolrException.ErrorCode
    import org.apache.solr.common.SolrException.ErrorCode.{SERVER_ERROR, FORBIDDEN, UNAUTHORIZED, NOT_FOUND, BAD_REQUEST}

    failure.getException match {
      case e: SolrException if Set(BAD_REQUEST).contains(ErrorCode.getErrorCode(e.code)) => logger.warn("Bad search request", e)
      case e: SolrException if Set(SERVER_ERROR, FORBIDDEN, UNAUTHORIZED, NOT_FOUND).contains(ErrorCode.getErrorCode(e.code)) =>
        logger.fatal("Configuration error. Shutting down the service.", e)
        lock.synchronized {
          if (serviceRef.get() == service) {
            shutdown()
          }
        }

      case e: IOException => replaceManagedServerInstance(failure)
      case e: SolrServerException if e.getCause.isInstanceOf[IOException] => replaceManagedServerInstance(failure)
    }

    //logger.fatal("No more index update or rebuild requests will be accepted.", e)
  }

  newManagedService(recreateDataDir = false) |> serviceRef.set


  private def replaceManagedServerInstance(failure: ServiceFailure): Unit = lock.synchronized {
    if (serviceRef.compareAndSet(service, UnavailableDocumentIndexService)) {
      logger.error("Unrecoverable index error. Managed service instance have to be replaced.", failure.getException)
      service.shutdown()

      if (shutdownRef.get) {
        logger.info("New managed service instance can not be created - service has been shout down.")
      } else {
        logger.info("Creating new instance of managed service. Data directory will be recreated.")
        newManagedService(recreateDataDir = true) |> { newService =>
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