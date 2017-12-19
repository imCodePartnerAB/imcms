package imcode.server.document.index.service.impl

import _root_.imcode.server.document.index.service._
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.response.QueryResponse

import scala.util.Try

class RemoteDocumentIndexService(solrReadUrl: String, solrWriteUrl: String, serviceOps: DocumentIndexServiceOps)
    extends DocumentIndexService {

  private def newManagedService(): ManagedDocumentIndexService = {
    val solrServerReader = SolrServerFactory.createHttpSolrServer(solrReadUrl)
    val solrServerWriter = SolrServerFactory.createHttpSolrServer(solrWriteUrl)

    new ManagedDocumentIndexService(solrServerReader, solrServerWriter, serviceOps, _ => ())
  }

  def query(solrQuery: SolrQuery): Try[QueryResponse] = ???

  def update(request: IndexUpdateOp) {}

  def rebuild(): IndexRebuildTask = ???

  def currentRebuildTaskOpt(): Option[IndexRebuildTask] = ???

  def shutdown() {}
}