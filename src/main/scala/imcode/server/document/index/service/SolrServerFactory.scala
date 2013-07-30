package imcode.server.document.index.service

import com.imcode._
import org.apache.solr.client.solrj.impl.{BinaryRequestWriter, HttpSolrServer}
import java.io.File
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.commons.io.FileUtils
import org.apache.solr.core.{CoreContainer, CoreDescriptor}

object SolrServerFactory extends Log4jLoggerSupport {

  def createHttpSolrServer(solrUrl: String) = new HttpSolrServer(solrUrl) |>> { solr =>
    solr.setRequestWriter(new BinaryRequestWriter())
  }


  def createEmbeddedSolrServer(solrHome: String, recreateDataDir: Boolean = false): EmbeddedSolrServer = {
    logger.info(s"Creating embedded SOLr server. Solr home: $solrHome, recreateDataDir: $recreateDataDir.")

    if (recreateDataDir) {
      new File(solrHome, "core/data") |> { dataDir =>
        if (dataDir.exists() && !FileUtils.deleteQuietly(dataDir)) {
          val msg = s"Unable to delete SOLr data dir $dataDir."
          logger.fatal(msg)
          sys.error(msg)
        }
      }
    }

    new CoreContainer(solrHome) |>> { _.load() } |> { coreContainer =>
      new EmbeddedSolrServer(coreContainer, "core")
    }
  }


  def createEmbeddedSolrServer(solrHome: String, dataDirPath: String, recreateDataDir: Boolean): EmbeddedSolrServer = {
    val dataDir: File = if (dataDirPath.startsWith("/")) new File(dataDirPath) else new File(solrHome, "core/" + dataDirPath)

    if (recreateDataDir && dataDir.exists() && !FileUtils.deleteQuietly(dataDir)) {
      val msg =s"Unable to delete SOLr data dir $dataDir."
      logger.fatal(msg)
      sys.error(msg)
    }

    new CoreContainer(solrHome) |>> { _.load() } |> { coreContainer =>
      new CoreDescriptor(coreContainer, "core", "core") |>> { coreDescriptor =>
        coreDescriptor.setDataDir(dataDir.getPath)
      } |> coreContainer.create |> { core =>
        coreContainer.register(core, false)
      }

      new EmbeddedSolrServer(coreContainer, "core")
    }
  }
}