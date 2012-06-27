package imcode.server.document.index

import com.imcode._
import com.imcode.Log4jLoggerSupport
import imcode.server.{ImcmsServices}

object DocumentIndexServiceFactory extends Log4jLoggerSupport {

  def createIndexService(services: ImcmsServices): SolrDocumentIndexService = services.getConfig |> { config =>
    (Option(config.getSolrUrl), Option(config.getSolrHome)) |> {
      case (Some(solrUrl), _) => new RemoteSolrDocumentIndexService(solrUrl)

      case (_, Some(solrHome)) => new EmbeddedSolrDocumentIndexService(solrHome)

      case _ =>
        val errMsg = "Configuration error. Neither Config.solrUrl nor Config.solrHome is set."
        logger.fatal(errMsg)
        throw new IllegalArgumentException(errMsg)
    } |>> { service =>
      service.documentMapper = services.getDocumentMapper
      service.documentIndexer = new DocumentIndexer(
        services.getDocumentMapper,
        services.getCategoryMapper,
        new DocumentContentIndexer
      )
    }
  }
}