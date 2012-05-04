package imcode.server.document.index

import com.imcode._
import imcode.server.Config
import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.core.CoreContainer
import java.io.File
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.impl.{BinaryRequestWriter, HttpSolrServer}

class SolrServerFactory(basedir: File, config: Config) {

  val server: SolrServer = if (config.getSolrUrl == null) createEmbeddedServer() else createRemoteServer()

  // run simple query, get hit count ???


  private def createEmbeddedServer(): SolrServer = {
    val coreContainer = new CoreContainer(
      "/Users/ajosua/projects/wstar/solr/src/test/resources/multicore",
      new File("/Users/ajosua/projects/wstar/solr/src/test/resources/multicore/solr.xml"))

    new EmbeddedSolrServer(coreContainer, "core0")
  }


  private def createRemoteServer(): SolrServer = new HttpSolrServer(config.getSolrUrl) |< { solr =>
    solr.setRequestWriter(new BinaryRequestWriter())
  }
}