package imcode.server.document.index.solr

import java.io.File
import imcode.server.user.UserDomainObject
import com.imcode._
import imcode.server.document.DocumentDomainObject
import org.apache.solr.client.solrj.SolrQuery
import java.util.concurrent.atomic.AtomicReference
import java.util.Collections
import scala.swing.{Reactor}
import scala.swing.event.Event


/**
 * Delegates all invocations to the ManagedSolrDocumentIndexService instance.
 * In case of an indexing error replaces managed instance with new one and re-indexes documents.
 */
class EmbeddedSolrDocumentIndexService(solrHome: File, ops: SolrDocumentIndexServiceOps)
    extends SolrDocumentIndexService with Reactor {

  private val serviceRef: AtomicReference[SolrDocumentIndexService] = new AtomicReference(newManagedService())

  reactions += {
    // swap target service
    case EmbeddedSolrDocumentIndexService.IndexError(publisher, error) =>
      serviceRef.synchronized {
        serviceRef.get() match {
          case service if service eq publisher =>
            deafTo(service)
            serviceRef.set(NoOpSolrDocumentIndexService)
            service.shutdown()
            // wait till shutdown
            serviceRef.set(newManagedService(requestIndexRebuild = true))
          case _ =>
        }
      }
  }

  // todo: replace requestIndexRebuild flag with enum values
  private def newManagedService(requestIndexRebuild: Boolean = false): ManagedSolrDocumentIndexService = {
    val solrServerReader = SolrServerFactory.createEmbeddedSolrServer(solrHome)
    val solrServerWriter = SolrServerFactory.createEmbeddedSolrServer(solrHome)

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


object EmbeddedSolrDocumentIndexService {
  case class IndexError(publisher: SolrDocumentIndexService, error: Throwable) extends Event
}


/**
 * rebuild monitor - Service UNAVAILABLE | IDLE | Monitor -vs- Option[Monitor]
 */
object NoOpSolrDocumentIndexService extends SolrDocumentIndexService {

  def search(query: SolrQuery, searchingUser: UserDomainObject): JList[DocumentDomainObject] = Collections.emptyList()

  def requestIndexUpdate(op: SolrDocumentIndexService.IndexUpdateOp) {}

  def requestIndexRebuild() {}

  def shutdown() {}
}