package imcode.server.document.index

import com.imcode._
import scala.collection.JavaConverters._
import org.apache.solr.common.SolrInputDocument
import imcode.server.document.textdocument.{TextDomainObject, TextDocumentDomainObject}
import imcode.server.document.{FileDocumentDomainObject, DocumentDomainObject}
import org.apache.commons.io.IOUtils
import org.apache.tika.metadata.{HttpHeaders, Metadata}
import org.apache.tika.parser.html.HtmlParser
import org.apache.tika.mime.MediaType
import org.apache.tika.detect.Detector
import java.io.InputStream
import org.apache.tika.Tika

class DocumentContentIndexer extends Log4jLoggerSupport {

  private val tikaAutodetect = new Tika() |>> { tika =>
    tika.setMaxStringLength(-1)
  }

  private val tikaHtml: Tika = {
    val parser = new HtmlParser
    val detector = new Detector {
      val mediaType = MediaType.parse("text/html")

      def detect(input: InputStream, metadata: Metadata): MediaType = mediaType
    }

    new Tika(detector, parser) |>> { tika =>
      tika.setMaxStringLength(-1)
    }
  }

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
  def indexTextDoc(doc: TextDocumentDomainObject, indexDoc: SolrInputDocument): SolrInputDocument = indexDoc |>> { _ =>
    indexDoc.addField(DocumentIndex.FIELD__TEMPLATE, doc.getTemplateName)

    val texts = Seq(doc.getTexts.values, doc.getLoopTexts.values).map(_.asScala).flatten
    val images = Seq(doc.getImages.values, doc.getLoopImages.values).map(_.asScala).flatten
    val menus = doc.getMenus.values.asScala

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
      indexDoc.addField(DocumentIndex.FIELD__CHILD_ID, menuItem.getDocumentId.toString)
    }
  }


  def indexFileDoc(doc: FileDocumentDomainObject, indexDoc: SolrInputDocument): SolrInputDocument = indexDoc |>> { _ =>
    doc.getDefaultFile |> opt foreach { file =>
      indexDoc.addField(DocumentIndex.FIELD__MIME_TYPE, file.getMimeType)
//      val metadata = new Metadata |>> { m =>
//        m.set(HttpHeaders.CONTENT_DISPOSITION, file.getFilename)
//        m.set(HttpHeaders.CONTENT_TYPE, file.getMimeType)
//      }

      try {
        tikaAutodetect.parseToString(file.getInputStreamSource.getInputStream) |> { content =>
          indexDoc.addField(DocumentIndex.FIELD__TEXT, content)
        }
      } catch {
        case e => logger.error("Unable to index content of file-doc-file '%s'".format(file), e);
      }
    }
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