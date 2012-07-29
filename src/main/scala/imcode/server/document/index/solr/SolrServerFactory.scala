package imcode.server.document.index.solr

import com.imcode._
import com.imcode.Log4jLoggerSupport
import org.apache.solr.client.solrj.impl.{BinaryRequestWriter, HttpSolrServer}
import java.io.File
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.core.CoreContainer

object SolrServerFactory extends Log4jLoggerSupport {

  def createHttpSolrServer(solrUrl: String) = new HttpSolrServer(solrUrl) with SolrServerShutdown |>> { solr =>
    solr.setRequestWriter(new BinaryRequestWriter())
  }


  def createEmbeddedSolrServer(solrHome: String, recreateDataDir: Boolean = false): EmbeddedSolrServer with SolrServerShutdown = {
    if (recreateDataDir) {
      new File(solrHome, "core/data") |> { dataDir =>
        if (dataDir.exists() && !dataDir.delete()) sys.error("Unable to delete SOLr data dir %s.".format(dataDir))
      }
    }

    new CoreContainer(solrHome, new File(solrHome, "solr.xml")) |> { coreContainer =>
      new EmbeddedSolrServer(coreContainer, "core") with SolrServerShutdown
    }
  }
}