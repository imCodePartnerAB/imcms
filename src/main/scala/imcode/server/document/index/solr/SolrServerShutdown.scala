package imcode.server.document.index.solr

import org.apache.solr.client.solrj.SolrServer

trait SolrServerShutdown { this: SolrServer =>
  def shutdown()
}