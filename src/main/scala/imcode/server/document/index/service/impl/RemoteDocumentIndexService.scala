package imcode.server.document.index.service.impl

import java.util.function.Consumer

import _root_.imcode.server.document.index.service._
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.response.QueryResponse

class RemoteDocumentIndexService(solrReadUrl: String, solrWriteUrl: String, serviceOps: DocumentIndexServiceOps)
    extends DocumentIndexService {

  def query(solrQuery: SolrQuery): QueryResponse = ???

  private def newManagedService(): ManagedDocumentIndexService = {
    val solrServerReader = SolrServerFactory.createHttpSolrServer(solrReadUrl)
    val solrServerWriter = SolrServerFactory.createHttpSolrServer(solrWriteUrl)

    new ManagedDocumentIndexService(solrServerReader, solrServerWriter, serviceOps, new Consumer[ServiceFailure] {
      override def accept(t: ServiceFailure): Unit = ()
    })
  }

  def update(request: IndexUpdateOp) {}

  def rebuild(): IndexRebuildTask = ???

  def currentRebuildTaskOpt(): Option[IndexRebuildTask] = ???

  def shutdown() {}
}