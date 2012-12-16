package imcode.server.document.index

import com.imcode._
import com.imcode.Log4jLoggerSupport
import _root_.imcode.server.ImcmsServices
import _root_.imcode.server.document.index.solr._

/**
 *
 */
object DocumentIndexFactory extends Log4jLoggerSupport {

  def create(services: ImcmsServices): DocumentIndex = services.getConfig |> { config =>
    (Option(config.getSolrUrl), Option(config.getSolrHome)) |> {
      case (Some(solrUrl), _) =>
        new ExternalSolrDocumentIndexService(solrUrl, solrUrl, createSolrDocumentIndexServiceOps(services))

      case (_, Some(solrHome)) =>
        new EmbeddedSolrDocumentIndexService(solrHome, createSolrDocumentIndexServiceOps(services))

      case _ =>
        val errMsg = "Configuration error. Neither Config.solrUrl nor Config.solrHome is set."
        logger.fatal(errMsg)
        throw new IllegalArgumentException(errMsg)
    } |> { service =>
      new DocumentIndexImpl(service, services.getI18nSupport.getDefaultLanguage)
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