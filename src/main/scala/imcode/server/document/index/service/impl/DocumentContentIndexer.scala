package imcode.server.document.index.service.impl

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
import imcode.server.document.index.DocumentIndex

class DocumentContentIndexer(fileDocFileFilter: FileDocumentDomainObject.FileDocumentFile => Boolean) extends Log4jLoggerSupport {

  private val tika = new Tika() |>> { tika =>
    tika.setMaxStringLength(-1)
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
      val textValue = text.getText

      indexDoc.addField(DocumentIndex.FIELD__NONSTRIPPED_TEXT, textValue)
      indexDoc.addField(DocumentIndex.FIELD__TEXT, textValue)
      indexDoc.addField(DocumentIndex.FIELD__TEXT + text.getNo, textValue)
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
    for (file <- doc.getDefaultFile.asOption if fileDocFileFilter(file)) {
      indexDoc.addField(DocumentIndex.FIELD__MIME_TYPE, file.getMimeType)
      val metadata = new Metadata |>> { m =>
        m.set(HttpHeaders.CONTENT_DISPOSITION, file.getFilename)
        m.set(HttpHeaders.CONTENT_TYPE, file.getMimeType)
      }

      try {
        tika.parseToString(file.getInputStreamSource.getInputStream, metadata) |> { content =>
          indexDoc.addField(DocumentIndex.FIELD__TEXT, content)
        }
      } catch {
        case e: Exception => logger.error(s"Unable to index doc ${doc.getId} file '$file'.", e)
      }
    }
  }
}