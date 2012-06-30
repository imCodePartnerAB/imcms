package imcode.server.document.index

import com.imcode._
import com.imcode.Log4jLoggerSupport
import imcode.server.ImcmsServices
import imcode.server.document.index.solr._

/**
 *
 */
object DocumentIndexServiceFactory extends Log4jLoggerSupport {

  def createService(services: ImcmsServices): DocumentIndexService = services.getConfig |> { config =>
    (Option(config.getSolrUrl), Option(config.getSolrHome)) |> {
      case (Some(solrUrl), _) =>
        new RemoteSolrDocumentIndexService(solrUrl, createSolrDocumentIndexServiceOps(services))

      case (_, Some(solrHome)) =>
        new EmbeddedSolrDocumentIndexServiceProxy(solrHome, createSolrDocumentIndexServiceOps(services))

      case _ =>
        val errMsg = "Configuration error. Neither Config.solrUrl nor Config.solrHome is set."
        logger.fatal(errMsg)
        throw new IllegalArgumentException(errMsg)
    } |> { service =>
      new SolrDocumentIndexServiceWrapper(service)
    }
  }

  private def createSolrDocumentIndexServiceOps(services: ImcmsServices): SolrDocumentIndexServiceOps =
    new SolrDocumentIndexServiceOps(
      services.getDocumentMapper,
      new DocumentIndexer(
        services.getDocumentMapper,
        services.getCategoryMapper,
        new DocumentContentIndexer
      )
    )
}