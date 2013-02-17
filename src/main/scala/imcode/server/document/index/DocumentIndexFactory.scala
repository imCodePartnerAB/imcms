package imcode.server.document.index

import com.imcode._
import com.imcode.Log4jLoggerSupport
import _root_.imcode.server.ImcmsServices
import imcode.server.document.index.service.impl._

/**
 *
 */
object DocumentIndexFactory extends Log4jLoggerSupport {

  def create(services: ImcmsServices): DocumentIndex = services.getConfig |> { config =>
    (config.getSolrUrl.trimToOption, config.getSolrHome.trimToOption) |> {
      case (Some(solrUrl), _) =>
        new RemoteDocumentIndexService(solrUrl, solrUrl, createDocumentIndexServiceOps(services))

      case (_, Some(solrHome)) =>
        new InternalDocumentIndexService(solrHome, createDocumentIndexServiceOps(services))

      case _ =>
        val errMsg = """|Configuration error. Unable to create DocumentIndex.\n
                        |Neither Config.solrUrl nor Config.solrHome is set.\n
                        |Set Config.solrUrl for remote SOLr server or Config.solrHome for internal SOLr server.\n
                        |If both are set then Config.solrUrl takes precedence and Config.solrHome is ignored.
                     """.stripMargin
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