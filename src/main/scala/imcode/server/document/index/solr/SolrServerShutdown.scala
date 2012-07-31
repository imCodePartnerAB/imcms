package imcode.server.document.index.solr

import org.apache.solr.client.solrj.SolrServer

/**
 * Since SOLr v.4 SolrServer class has shutdown method
 */
trait SolrServerShutdown { this: SolrServer =>
  def shutdown()
}