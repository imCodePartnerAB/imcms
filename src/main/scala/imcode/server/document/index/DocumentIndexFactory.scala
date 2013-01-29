package imcode.server.document.index

import com.imcode._
import com.imcode.Log4jLoggerSupport
import _root_.imcode.server.ImcmsServices
import imcode.server.document.index.service.impl._
import scala.Some

/**
 *
 */
object DocumentIndexFactory extends Log4jLoggerSupport {

  def create(services: ImcmsServices): DocumentIndex = services.getConfig |> { config =>
    (Option(config.getSolrUrl), Option(config.getSolrHome)) |> {
      case (Some(solrUrl), _) =>
        new RemoteDocumentIndexService(solrUrl, solrUrl, createDocumentIndexServiceOps(services))

      case (_, Some(solrHome)) =>
        new EmbeddedDocumentIndexService(solrHome, createDocumentIndexServiceOps(services))

      case _ =>
        val errMsg = "Configuration error. Config.solrUrl or Config.solrHome is not set."
        logger.fatal(errMsg)
        throw new IllegalArgumentException(errMsg)
    } |> { service =>
      new DocumentIndexImpl(service, services.getI18nContentSupport.getDefaultLanguage)
    }
  }

  private def createDocumentIndexServiceOps(services: ImcmsServices): DocumentIndexServiceOps =
    new DocumentIndexServiceOps(
      services.getDocumentMapper,
      new DocumentIndexer(
        services.getDocumentMapper,
        services.getCategoryMapper,
        new DocumentContentIndexer
      )
    )
}