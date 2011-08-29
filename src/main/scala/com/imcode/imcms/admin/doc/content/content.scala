package com.imcode
package imcms.admin.doc.content

import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import com.imcode.imcms.api._
import imcode.server.user._
import imcode.server.{Imcms}
import imcode.server.document._
import com.imcode.imcms.vaadin._

import java.net.{MalformedURLException, URL}
import imcode.server.document.FileDocumentDomainObject.FileDocumentFile
import imcode.util.io.InputStreamSource
import java.io.ByteArrayInputStream
import textdocument.TextDocumentDomainObject
import java.util.{EnumSet}
import imcms.mapping.DocumentMapper.SaveDirectives
import imcms.mapping.{DocumentMapper, DocumentSaver}



// todo: rename to doc content editor ??? | extend editor with DocEditor ???
trait DocEditor {
  def ui: Component
  def doc: DocumentDomainObject

  // def validate: Left error/Right ok ???
}


class URLDocEditor(val doc: UrlDocumentDomainObject) extends DocEditor {
  val ui = new URLDocEditorUI
}

class TextDocEditor(val doc: TextDocumentDomainObject) extends DocEditor {
  val ui = new TextDocEditorUI
}

class HtmlDocEditor(val doc: HtmlDocumentDomainObject) extends DocEditor {
  val ui = new HTMLDocEditorUI
}


case class MimeType(name: String, displayName: String)



/**
 * URL document editor UI
 */
// todo: escape URL text, validate???
class URLDocEditorUI extends FormLayout {
  val lblTodo = new Label("#TODO: SUPPORTED PROTOCOLS: HTTPS, FTP?; VALIDATE?")
  val txtURL = new TextField("Content URL") with ValueType[String] {
    setInternalValue("http://")
  }

  addComponents(this, lblTodo, txtURL)
}

class HTMLDocEditorUI extends FormLayout {
  val lblHTML = new Label("HTML")

  addComponents(this, lblHTML)
}

/**
 * File document editor UI
 * File document is `container` which may contain one or more related or unrelated files.
 * If there is more than one file then one of them must be set as default.
 * Default file content is returned when an user clicks on a doc link in a browser. 
 */
class FileDocEditorUI extends VerticalLayout with UndefinedSize {
  val menuBar = new MenuBar
  val miNew = menuBar.addItem("Add", null)
  val miEdit = menuBar.addItem("Edit", null)
  val miDelete = menuBar.addItem("Delete", null)
  val miSetDefault = menuBar.addItem("Set default", null)

  type FileId = String
  val tblFiles = new Table with ValueType[FileId] with Selectable with Immediate with Reloadable {
    addContainerProperties(this,
      ContainerProperty[FileId]("File Id"),
      ContainerProperty[String]("File name"),
      ContainerProperty[String]("Size"),
      ContainerProperty[String]("Mime type"),
      ContainerProperty[String]("Default"))
  }

  addComponents(this, menuBar, tblFiles)
}

/** Add/Edit file doc's file */
class FileDocFileDialogContent extends FormLayout with UndefinedSize {
  // model
  val uploadReceiver = new MemoryUploadReceiver

  // ui
  val sltMimeType = new Select("Mime type") with ValueType[String]
  val lblUploadStatus = new Label with UndefinedSize
  val txtFileId = new TextField("File id")
  val upload = new Upload(null, uploadReceiver) with UploadEventHandler {
    setImmediate(true)
    setButtonCaption("Select")

    def handleEvent(e: com.vaadin.ui.Component.Event) = e match {
      case e: Upload.SucceededEvent =>
        alterNameTextField()
      case e: Upload.FailedEvent =>
        uploadReceiver.uploadRef.set(None)
        alterNameTextField()
      case _ =>
    }
  }

  addComponents(this, lblUploadStatus, upload, sltMimeType, txtFileId)
  alterNameTextField()

  def alterNameTextField() = let(uploadReceiver.uploadRef.get) { uploadOpt =>
    lblUploadStatus setValue (uploadOpt match {
      case Some(upload) => upload.filename
      case _ => "No file selected"
    })
  }
}

class FileDocEditor(app: ImcmsApplication, val doc: FileDocumentDomainObject, mimeTypes: Seq[MimeType]) extends DocEditor {
  val ui = letret(new FileDocEditorUI) { ui =>
    ui.tblFiles.itemsProvider = () =>
      doc.getFiles.toSeq collect {
        case (fileId, fdf) =>
          fileId -> List(fileId, fdf.getId, fdf.getMimeType, fdf.getInputStreamSource.getSize.toString, (fileId == doc.getDefaultFileId).toString)
      }

    ui.tblFiles.reload()

    ui.miNew setCommandHandler {
      app.initAndShow(new OkCancelDialog("Add file")) { w =>
        let(w setMainContent new FileDocFileDialogContent) { c =>
          for (MimeType(name, displayName) <- mimeTypes) {
            c.sltMimeType.addItem(name)  
          }

          w wrapOkHandler {
            c.uploadReceiver.uploadRef.get match {
              case Some(upload) =>
                val file = new FileDocumentFile
                val source = new InputStreamSource {
                  def getInputStream = new ByteArrayInputStream(upload.content)
                  def getSize = upload.content.length
                }

                file.setInputStreamSource(source)
                file.setFilename(c.txtFileId.value)
                file.setMimeType(c.sltMimeType.value)

                doc.addFile(c.txtFileId.value, file)
                ui.tblFiles.reload()
              case _ =>
            }
          }
        }
      }
    }

    // todo: replace old file - delete from storage
    ui.miEdit setCommandHandler {
      whenSelected(ui.tblFiles) { fileId =>
        app.initAndShow(new OkCancelDialog("Edit file")) { dlg =>
          let(dlg.setMainContent(new FileDocFileDialogContent)) { c =>
            val fdf = doc.getFile(fileId)
            
            c.txtFileId.value = fileId
            //c.sltMimeType.value = "" // todo: set
            c.lblUploadStatus.value = fdf.getFilename

            dlg wrapOkHandler {
              c.uploadReceiver.uploadRef.get match {
                case Some(upload) => // relace fdf
                  val newFdf = new FileDocumentFile
                  val source = new InputStreamSource {
                    def getInputStream = new ByteArrayInputStream(upload.content)
                    def getSize = upload.content.length
                  }

                  newFdf.setInputStreamSource(source)
                  newFdf.setFilename(c.txtFileId.value)

                  doc.addFile(c.txtFileId.value, newFdf)
                  doc.removeFile(fileId)

                case _ => // update fdf
                  fdf.setId(c.txtFileId.value)
                  // todo: fdf.setMimeType()
              }

              ui.tblFiles.reload()
            }
          }
        }
      }
    }

    ui.miDelete setCommandHandler {
      whenSelected(ui.tblFiles) { fileId =>
        doc.removeFile(fileId)

        ui.tblFiles.reload()
      }
    }

    ui.miSetDefault setCommandHandler {
      whenSelected(ui.tblFiles) { fileId =>
        doc.setDefaultFileId(fileId)

        ui.tblFiles.reload()
      }
    }
  }
}


class TextDocEditorUI extends VerticalLayout with FullSize with Spacing with Margin {
  // todo: show outline/redirect external doc editor
}


/**
 * This page is shown as a second page in the flow - next after meta.
 * User may choose whether copy link texts (filled in meta page) into the text fields no 1 and 2.
 * Every language's texts is shown in its tab.
 */
class NewTextDocEditorUI extends VerticalLayout with FullSize with Spacing with Margin {
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