package imcode.server.document.index

import com.imcode._
import _root_.imcode.server.ImcmsServices
import _root_.imcode.server.document.index.service.impl._
import _root_.imcode.server.document.FileDocumentDomainObject
import org.apache.commons.lang.StringUtils
import org.apache.commons.io.FilenameUtils

/**
 *
 */
object DocumentIndexFactory extends Log4jLoggerSupport {

  def create(services: ImcmsServices): DocumentIndex = services.getConfig |> { config =>
    (config.getSolrUrl.trimToOption, config.getSolrHome.trimToOption) |> {
      case (Some(solrUrl), _) =>
        new RemoteDocumentIndexService(solrUrl, solrUrl, createDocumentIndexServiceOps(services)) with IndexRebuildScheduler

      case (_, Some(solrHome)) =>
        new InternalDocumentIndexService(solrHome, createDocumentIndexServiceOps(services)) with IndexRebuildScheduler

      case _ =>
        val errMsg = """|Configuration error. Unable to create DocumentIndex.\n
                        |Neither Config.solrUrl nor Config.solrHome is set.\n
                        |Set Config.solrUrl for remote SOLr server or Config.solrHome for internal SOLr server.\n
                        |If both are set then Config.solrUrl takes precedence and Config.solrHome is ignored.
                     """.stripMargin
        logger.fatal(errMsg)
        throw new IllegalArgumentException(errMsg)
    } |> { service =>
      service.setRebuildIntervalInMinutes(config.getIndexingSchedulePeriodInMinutes.toInt |> opt)
      new DocumentIndexImpl(service, services.getDocumentI18nSupport.getDefaultLanguage)
    }
  }

  private def createDocumentIndexServiceOps(services: ImcmsServices): DocumentIndexServiceOps = {
    import FileDocumentDomainObject.FileDocumentFile

    val fileDocFileFilter: (FileDocumentFile => Boolean) = services.getConfig |> { config =>
      val disabledFileExtensions = config.getIndexDisabledFileExtensionsAsSet
      val disabledFileMimes = config.getIndexDisabledFileMimesAsSet

      if (disabledFileExtensions.isEmpty && disabledFileMimes.isEmpty) {
        fdf: FileDocumentFile => true
      } else {
        fdf: FileDocumentFile => {
          val ext = fdf.getFilename |> StringUtils.trimToEmpty |> FilenameUtils.getExtension |> { _.toLowerCase }
          val mime = fdf.getMimeType |> StringUtils.trimToEmpty |> { _.toLowerCase }

          !(disabledFileExtensions.contains(ext) || disabledFileMimes.contains(mime))
        }
      }
    }

    new DocumentIndexServiceOps(
      services.getDocumentMapper,
      new DocumentIndexer(
        services.getDocumentMapper,
        services.getCategoryMapper,
        new DocumentContentIndexer(fileDocFileFilter)
      )
    )
  }
}