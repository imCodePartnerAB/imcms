package imcode.server.document.index.solr

import com.imcode._
import com.imcode.Log4jLoggerSupport
import org.apache.solr.client.solrj.impl.{BinaryRequestWriter, HttpSolrServer}
import java.io.File
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.commons.io.FileUtils
import org.apache.solr.core.{SolrConfig, CoreContainer, SolrCore, CoreDescriptor}

object SolrServerFactory extends Log4jLoggerSupport {

  def createHttpSolrServer(solrUrl: String) = new HttpSolrServer(solrUrl) with SolrServerShutdown |>> { solr =>
    solr.setRequestWriter(new BinaryRequestWriter())
  }


  def createEmbeddedSolrServer(solrHome: String, recreateDataDir: Boolean = false): EmbeddedSolrServer with SolrServerShutdown = {
    logger.info("Creating embedded SOLr server. Solr home: %s, recreateDataDir: %s.".format(solrHome, recreateDataDir))

    if (recreateDataDir) {
      new File(solrHome, "core/data") |> { dataDir =>
        if (dataDir.exists() && !FileUtils.deleteQuietly(dataDir)) {
          val msg = "Unable to delete SOLr data dir %s.".format(dataDir)
          logger.fatal(msg)
          sys.error(msg)
        }
      }
    }

    new CoreContainer(solrHome, new File(solrHome, "solr.xml")) |> { coreContainer =>
      new EmbeddedSolrServer(coreContainer, "core") with SolrServerShutdown
    }
  }


  def createEmbeddedSolrServer(solrHome: String, dataDirPath: String, recreateDataDir: Boolean): EmbeddedSolrServer with SolrServerShutdown = {
    val dataDir: File = if (dataDirPath.startsWith("/")) new File(dataDirPath) else new File(solrHome, "core/" + dataDirPath)

    if (recreateDataDir && dataDir.exists() && !FileUtils.deleteQuietly(dataDir)) {
      val msg = "Unable to delete SOLr data dir %s.".format(dataDir)
      logger.fatal(msg)
      sys.error(msg)
    }

    new CoreContainer(solrHome) |> { coreContainer =>
      new CoreDescriptor(coreContainer, "core", "core") |>> { coreDescriptor =>
        coreDescriptor.setDataDir(dataDir.getPath)
      } |> coreContainer.create |> { core =>
        coreContainer.register(core, false)
      }

      new EmbeddedSolrServer(coreContainer, "core") with SolrServerShutdown
    }
  }
}