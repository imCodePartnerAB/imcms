package imcode.server.document.index

import com.imcode._
import imcode.server.Config
import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.core.CoreContainer
import java.io.File
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.impl.{BinaryRequestWriter, HttpSolrServer}
import java.lang.IllegalStateException

class IndexService(config: Config) extends Log4jLoggerSupport {

  if (config.getSolrUrl == null && config.getSolrHome == null) {
    val msg = "Configuration error. Neither Config.solrUrl nor Config.solrHome is set."
    logger.fatal(msg)
    throw new IllegalStateException(msg)
  }


  val solrServer: SolrServer with SolrServerShutdown =
    if (config.getSolrUrl == null) createEmbeddedSolrServer() else createRemoteSolrServer()


  def shutdown() {
    solrServer.shutdown()
  }


  private def createEmbeddedSolrServer() = {
    val coreContainer = new CoreContainer(config.getSolrHome.getAbsolutePath, new File(config.getSolrHome, "solr.xml"))

    new EmbeddedSolrServer(coreContainer, "imcms") with SolrServerShutdown
  }


  private def createRemoteSolrServer() = new HttpSolrServer(config.getSolrUrl) with SolrServerShutdown |< { solr =>
    solr.setRequestWriter(new BinaryRequestWriter())
  }
}


trait SolrServerShutdown {
  def shutdown()
}