package imcode.server.document.index

import com.imcode._
import scala.collection.JavaConversions._
import org.apache.solr.common.SolrInputDocument
import com.imcode.imcms.dao.{ImageDao, TextDao}
import imcode.server.{ImcmsServices}
import imcode.server.document.textdocument.{TextDomainObject, TextDocumentDomainObject}
import imcode.server.document.{FileDocumentDomainObject, DocumentDomainObject}
import org.apache.commons.io.IOUtils
import org.apache.tika.metadata.{HttpHeaders, Metadata}
import org.apache.tika.parser.html.HtmlParser
import org.apache.tika.mime.MediaType
import org.apache.tika.detect.Detector
import java.io.InputStream
import org.apache.tika.Tika

class DocumentContentIndexer(imcmsServices: ImcmsServices) extends Log4jLoggerSupport {

  val tikaAutodetect = new Tika() |>> { tika =>
    tika.setMaxStringLength(-1)
  }

  val tikaHtml: Tika = {
    val parser = new HtmlParser
    val detector = new Detector {
      val mediaType = MediaType.parse("text/html")

      def detect(input: InputStream, metadata: Metadata): MediaType = mediaType
    }

    new Tika(detector, parser) |>> { tika =>
      tika.setMaxStringLength(-1)
    }
  }

  val textDao = imcmsServices.getComponent(classOf[TextDao])
  val imageDao = imcmsServices.getComponent(classOf[ImageDao])


  def index(doc: DocumentDomainObject, indexDox: SolrInputDocument): SolrInputDocument = {
    doc match {
      case textDoc: TextDocumentDomainObject => indexTextDoc(textDoc, indexDox)
      case fileDoc: FileDocumentDomainObject => indexFileDoc(fileDoc, indexDox)
      case _ => indexDox
    }
  }


  /**
   * Texts and images are not taken from textDocument. Instead they are queried from DB.
   */
  def indexTextDoc(textDoc: TextDocumentDomainObject, indexDoc: SolrInputDocument): SolrInputDocument = {
    indexDoc.addField(DocumentIndex.FIELD__TEMPLATE, textDoc.getTemplateName)

    val texts = textDao.getTexts(textDoc.getId, textDoc.getVersion.getNo)
    val images = imageDao.getImages(textDoc.getId, textDoc.getVersion.getNo)
    val menus = textDoc.getMenus.values

    for (text <- texts) {
      val htmlStrippedText = stripHtml(text)

      indexDoc.addField(DocumentIndex.FIELD__NONSTRIPPED_TEXT, text.getText)
      indexDoc.addField(DocumentIndex.FIELD__TEXT, htmlStrippedText)
      indexDoc.addField(DocumentIndex.FIELD__TEXT + text.getNo, htmlStrippedText)
    }

    for (image <- images) {
      val imageLinkUrl = image.getLinkUrl

      if (null != imageLinkUrl && imageLinkUrl.length > 0) {
        indexDoc.addField(DocumentIndex.FIELD__IMAGE_LINK_URL, imageLinkUrl)
      }
    }

    indexDoc.addField(DocumentIndex.FIELD__HAS_CHILDREN, menus.exists(_.getMenuItems.nonEmpty))

    for (menu <- menus; menuItem <- menu.getMenuItems) {
      indexDoc.addField(DocumentIndex.FIELD__CHILD_ID, "" + menuItem.getDocumentId)
    }

    indexDoc
  }


  def indexFileDoc(fileDoc: FileDocumentDomainObject, indexDoc: SolrInputDocument): SolrInputDocument = {
    fileDoc.getDefaultFile |> option foreach { file =>
      indexDoc.addField(DocumentIndex.FIELD__MIME_TYPE, file.getMimeType)
//      val metadata = new Metadata |>> { m =>
//        m.set(HttpHeaders.CONTENT_DISPOSITION, file.getFilename);
//        m.set(HttpHeaders.CONTENT_TYPE, file.getMimeType);
//      }

      try {
        tikaAutodetect.parseToString(file.getInputStreamSource.getInputStream) |> { content =>
          indexDoc.addField(DocumentIndex.FIELD__TEXT, content)
        }
      } catch {
        case e => logger.error("Unable to index content of file-doc-file '%s'".format(file), e);
      }
    }

    indexDoc
  }


  private def stripHtml(tdo: TextDomainObject) = tdo.getText |> {
    case text if tdo.getType != TextDomainObject.TEXT_TYPE_HTML => text
    case htmlText =>
      try {
        tikaHtml.parseToString(IOUtils.toInputStream(htmlText)) |>> { stripped =>
          logger.trace("Stripped html to plain text: '%s' -> '%s'".format(htmlText, stripped))
        }
      } catch {
        case e => logger.error("Unable to strip html '%s'".format(htmlText), e);
      }
  }
}