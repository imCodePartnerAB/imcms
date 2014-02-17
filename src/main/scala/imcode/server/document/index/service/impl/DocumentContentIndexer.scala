package imcode.server.document.index.service.impl

import com.imcode._
import scala.collection.JavaConverters._
import scala.collection.breakOut
import org.apache.solr.common.SolrInputDocument
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import _root_.imcode.server.document.{FileDocumentDomainObject, DocumentDomainObject}
import org.apache.tika.metadata.{HttpHeaders, Metadata}
import org.apache.tika.Tika
import _root_.imcode.server.document.index.DocumentIndex

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

    val texts = Seq(doc.getTexts.asScala.toList, doc.getLoopTexts.asScala.map { case (key, text) => (key.getItemNo, text) }.toList).flatten
    val images = Seq(doc.getImages.asScala.toList, doc.getLoopImages.asScala.map { case (key, image) => (key.getItemNo, image) }.toList).flatten
    val menus = doc.getMenus.values.asScala

    for ((no, text) <- doc.getTexts.asScala) {
      val textValue = text.getText

      indexDoc.addField(DocumentIndex.FIELD__NONSTRIPPED_TEXT, textValue)
      indexDoc.addField(DocumentIndex.FIELD__TEXT, textValue)
      indexDoc.addField(DocumentIndex.FIELD__TEXT + no, textValue)
    }

    for ((no, image) <- images) {
      val imageLinkUrl = image.getLinkUrl

      if (null != imageLinkUrl && imageLinkUrl.length > 0) {
        indexDoc.addField(DocumentIndex.FIELD__IMAGE_LINK_URL, imageLinkUrl)
      }
    }

    val childrenIds: Set[Int] = (for (menu <- menus; menuItem <- menu.getMenuItems) yield menuItem.getDocumentId)(breakOut)

    indexDoc.addField(DocumentIndex.FIELD__HAS_CHILDREN, childrenIds.nonEmpty)
    indexDoc.addField(DocumentIndex.FIELD__CHILDREN_COUNT, childrenIds.size)

    childrenIds.foreach(id => indexDoc.addField(DocumentIndex.FIELD__CHILD_ID, id))
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