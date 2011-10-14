package com.imcode
package imcms
package admin.doc.content

import scala.collection.JavaConversions._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import com.imcode.imcms.api._
import imcode.server.user._
import imcode.server.{Imcms}
import imcode.server.document._
import com.imcode.imcms.vaadin._

import java.net.{MalformedURLException, URL}
import imcode.server.document.FileDocumentDomainObject.FileDocumentFile
import textdocument.TextDocumentDomainObject
import java.util.{EnumSet}
import imcms.mapping.DocumentMapper.SaveDirectives
import imcms.mapping.{DocumentMapper, DocumentSaver}
import com.vaadin.terminal.ExternalResource
import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}
import admin.system.file.FileUploaderDialog
import java.io.{FileInputStream, ByteArrayInputStream}
import imcode.util.io.{FileInputStreamSource, InputStreamSource}
import com.vaadin.ui._

trait DocContentEditor {
  def ui: Component
  def doc: DocumentDomainObject

  // def validate: Left error/Right ok ???
}



class TextDocContentEditor(val doc: TextDocumentDomainObject) extends DocContentEditor {
  val ui = letret(new TextDocContentEditorUI) { ui =>

  }
}


class URLDocContentEditor(val doc: UrlDocumentDomainObject) extends DocContentEditor {
  val ui = letret(new URLDocContentEditorUI) { ui =>
    ui.txtURL.value = "http://"
  }
}


class HtmlDocContentEditor(val doc: HtmlDocumentDomainObject) extends DocContentEditor {
  val ui = letret(new HTMLDocContentEditorUI) { ui =>
    ui.txaHTML.value = <html/>.toString
  }
}

/**
 * Used with deprecated docs such as Browser.
 */
class UnsupportedDocContentEditor(val doc: DocumentDomainObject) extends DocContentEditor {
  val ui = new Label("N/A".i)
}


case class MimeType(name: String, displayName: String)



/**
 * URL document editor UI
 */
class URLDocContentEditorUI extends FormLayout {
  val txtURL = new TextField("URL/Link".i) with ValueType[String] with FullWidth

  addComponents(this, txtURL)
}


/**
 * HTML document editor UI
 */
class HTMLDocContentEditorUI extends FormLayout {
  val txaHTML = new TextArea("HTML".i) with FullSize

  addComponents(this, txaHTML)
}


// todo: mimetypes???, id???, filename???
class FileDocContentEditor(app: ImcmsApplication, val doc: FileDocumentDomainObject, mimeTypes: Seq[MimeType]) extends DocContentEditor {
  val ui = letret(new FileDocContentEditorUI) { ui =>
    ui.miUpload setCommandHandler {
      //todo: handle replace if exists
      app.initAndShow(new FileUploaderDialog("Add file")) { dlg =>
        dlg.wrapOkHandler {
          dlg.uploader.uploadedFile match {
            case Some(uploadedFile) =>
              val fileDocFile = new FileDocumentFile

              fileDocFile.setInputStreamSource(new FileInputStreamSource(uploadedFile.file))
              fileDocFile.setFilename(uploadedFile.filename) // id???
              fileDocFile.setMimeType(uploadedFile.mimeType)

              doc.addFile(uploadedFile.filename, fileDocFile)  // todo: filename -> id
              reload()
            case _ =>
          }
        }
      }
    }

    ui.miEditProperties.setCommandHandler {
      whenSingle(ui.tblFiles.selection) { fileId =>
        app.initAndShow(new OkCancelDialog("Edit file properties")) { dlg =>
          val fdf = doc.getFile(fileId)
          val editorUI = letret(new FileDocFilePropertiesEditorUI) { eui =>
            eui.txtId.value = fdf.getId
            eui.txtName.value = fdf.getFilename
            //eui.cbType.value = ???
          }

          dlg.mainUI = editorUI
          dlg.wrapOkHandler {
            // validate
            fdf.setId(editorUI.txtId.value)
            fdf.setFilename(editorUI.txtName.value)
            //fdf.setMimeType()

          }
        }
      }
    }

    ui.miDelete setCommandHandler {
      whenSelected(ui.tblFiles) { fileIds =>
        fileIds foreach doc.removeFile

        reload()
      }
    }

    ui.miSetDefault setCommandHandler {
      whenSingle(ui.tblFiles.selection) { fileId =>
        doc.setDefaultFileId(fileId)

        reload()
      }
    }
  }

  def reload() {
    val defaultFileId = doc.getDefaultFileId

    doc.getFiles.toSeq collect {
      case (fileId, fdf) =>
        fileId -> List(fileId, fdf.getId, fdf.getMimeType, fdf.getInputStreamSource.getSize.toString, (fileId == defaultFileId).toString)
    }
  }

  // init
  reload()
}


/**
 * File document editor UI
 * File document is `container` which may contain one or more related or unrelated files.
 * If there is more than one file then one of them must be set as default.
 * Default file content is returned when an user clicks on a doc link in a browser.
 */
class FileDocContentEditorUI extends VerticalLayout with UndefinedSize {
  val mb = new MenuBar
  val miUpload = mb.addItem("Upload", null)
  val miEditProperties = mb.addItem("Edit properties", null)
  val miDelete = mb.addItem("Delete", null)
  val miSetDefault = mb.addItem("Set default", null)

  val tblFiles = new Table with MultiSelect[FileId] with Selectable with Immediate {
    addContainerProperties(this,
      ContainerProperty[FileId]("Id"),
      ContainerProperty[String]("Name"),
      ContainerProperty[String]("Size"),
      ContainerProperty[String]("Type"),
      ContainerProperty[String]("Default"))
  }

  addComponents(this, mb, tblFiles)
}


class FileDocFilePropertiesEditorUI extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id")
  val txtName = new TextField("Name")
  val cbType = new ComboBox("Type") with SingleSelect[String]
}


class TextDocContentEditorUI extends VerticalLayout with FullSize with Spacing with Margin {
  // todo: show outline/redirect external doc editor
}


/**
 * This page is shown as a second page in the flow - next after meta.
 * User may choose whether copy link texts (filled in meta page) into the text fields no 1 and 2.
 * Every language's texts is shown in its tab.
 */
class NewTextDocContentEditorUI extends VerticalLayout with FullSize with Spacing with Margin {
  class TextsUI extends FormLayout with FullSize {
    val txtText1 = new TextField("No 1")
    val txtText2 = new TextField("No 2")

    addComponents(this, txtText1, txtText2)
  }

  val chkCopyI18nMetaTextsToTextFields = new CheckBox("Copy link heading & subheading to text 1 & text 2 in page")
                                           with Immediate
  val tsTexts = new TabSheet with UndefinedSize with FullSize

  addComponents(this, chkCopyI18nMetaTextsToTextFields, tsTexts)
  setExpandRatio(tsTexts, 1.0f)
}