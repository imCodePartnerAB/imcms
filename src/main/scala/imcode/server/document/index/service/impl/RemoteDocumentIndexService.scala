package imcode.server.document.index.service.impl

import com.imcode._
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.index.service._
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.client.solrj.SolrQuery
import scala.util.Try

class RemoteDocumentIndexService(solrReadUrl: String, solrWriteUrl: String, serviceOps: DocumentIndexServiceOps)
    extends DocumentIndexService {

  private def newManagedService(): ManagedSolrDocumentIndexService = {
    val solrServerReader = SolrServerFactory.createHttpSolrServer(solrReadUrl)
    val solrServerWriter = SolrServerFactory.createHttpSolrServer(solrWriteUrl)

    new ManagedSolrDocumentIndexService(solrServerReader, solrServerWriter, serviceOps, _ => ())
  }

  def query(solrQuery: SolrQuery): Try[QueryResponse] = ???

  def search(solrQuery: SolrQuery): Try[JList[DocumentDomainObject]] = ???

  def update(request: IndexUpdateOp) {}

  def rebuild(): Try[IndexRebuildTask] = ???

  def currentRebuildTaskOpt(): Option[IndexRebuildTask] = ???

  def shutdown() {}
}