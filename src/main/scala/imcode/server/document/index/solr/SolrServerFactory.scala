package imcode.server.document.index.solr

import com.imcode._
import com.imcode.Log4jLoggerSupport
import org.apache.solr.client.solrj.impl.{BinaryRequestWriter, HttpSolrServer}
import java.io.File
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.core.CoreContainer
import org.apache.solr.client.solrj.SolrServer

object SolrServerFactory extends Log4jLoggerSupport {

  def createHttpSolrServer(solrUrl: String) = new HttpSolrServer(solrUrl) with SolrServerShutdown |>> { solr =>
    solr.setRequestWriter(new BinaryRequestWriter())
  }


  def createEmbeddedSolrServer(solrHome: File, recreateDataDir: Boolean = false): EmbeddedSolrServer with SolrServerShutdown = {
    if (recreateDataDir) {
      new File(solrHome, "core/data") |> { dataDir =>
        if (dataDir.exists() && !dataDir.delete()) sys.error("Unable to delete SOLr data dir %s.".format(dataDir))
      }
    }

    new CoreContainer(solrHome.getAbsolutePath, new File(solrHome, "solr.xml")) |> { coreContainer =>
      new EmbeddedSolrServer(coreContainer, "core") with SolrServerShutdown
    }
  }
}


trait SolrServerShutdown {
    this: SolrServer { def shutdown() } =>

  def shutdown()
}