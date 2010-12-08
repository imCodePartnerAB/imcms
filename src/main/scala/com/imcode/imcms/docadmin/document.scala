package com.imcode.imcms.docadmin

import com.imcode._
import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import com.imcode.imcms.api._
import com.imcode.imcms.sysadmin.permissions.{UserUI, UsersView}
import imcode.server.user._
import imcode.server.{Imcms}
import java.util.{Date, Collection => JCollection}
import scala.collection.mutable.{Map => MMap}
import imcode.server.document._
import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.AbstractFieldWrapper._
import java.util.concurrent.atomic.AtomicReference
import java.net.{MalformedURLException, URL}
import com.vaadin.ui.Window.Notification
import com.imcode.imcms.vaadin.flow.{Flow, FlowPage, FlowUI}
import com.sun.org.apache.xml.internal.security.utils.I18n
import imcode.server.document.FileDocumentDomainObject.FileDocumentFile
import imcode.util.io.InputStreamSource
import java.io.ByteArrayInputStream

//todo: type Component = UI ??


/**
 * Document editors factory - creates and initializes document editors.
 */
class EditorsFactory(app: VaadinApplication) {
  
  import scala.util.control.{Exception => E}
  
  def newURLDocFlow(parentDoc: DocumentDomainObject): FlowUI = {
    val docUI = new URLDocEditorUI
    val docValidator = () => E.allCatch.either(new URL(docUI.txtURL.value)) fold (ex => Some(ex.getMessage), url => None)
    val page0 = new FlowPage(() => docUI, docValidator)

    val metaModel = MetaModel(DocumentTypeDomainObject.URL_ID, parentDoc)
    val metaMVC = new MetaEditor(app, metaModel)
    val metaValidator = () => Some("meta is invalid, please fix the following errors..")
    val page1 = new FlowPage(() => metaMVC.view, metaValidator)

    val commit = () => Left("Not implemented")

    new FlowUI(new Flow(commit, page0, page1))
  }

  
  def newFileDocFlow(parentDoc: DocumentDomainObject): FlowUI = {
    val docEditor = new FileDocEditor(app, new FileDocumentDomainObject)
    val docValidator = () => None
    val page0 = new FlowPage(() => docEditor.ui, docValidator)

    val metaModel = MetaModel(DocumentTypeDomainObject.URL_ID, parentDoc)
    val metaMVC = new MetaEditor(app, metaModel)
    val metaValidator = () => Some("meta is invalid, please fix the following errors..")
    val page1 = new FlowPage(() => metaMVC.view, metaValidator)

    val commit = () => Left("Not implemented")

    new FlowUI(new Flow(commit, page0, page1))
  }

  def newTextDocFlow(parentDoc: DocumentDomainObject): FlowUI = {
    val metaModel = MetaModel(DocumentTypeDomainObject.URL_ID, parentDoc)
    val metaMVC = new MetaEditor(app, metaModel)
    val metaValidator = () => Some("meta is invalid, please fix the following errors..")
    val page1 = new FlowPage(() => metaMVC.view, metaValidator)

    val commit = () => Left("Not implemented")

    new FlowUI(new Flow(commit, page1))
  }

  def editURLDocument = new URLDocEditorUI
  def editFileDocument = new FileDocEditorUI
  def editTextDocument {}
}


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

/**
 * File document editor UI
 * File document is `container` which may contain one or more related or unrelated files.
 * If there is more than one file then one of them must be set as default.
 * Default file content is returned when an user clicks on a doc link in a browser. 
 */
class FileDocEditorUI extends VerticalLayout {
  val menuBar = new MenuBar
  val miAdd = menuBar.addItem("Add", null)
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


class FileDocDialogContent extends FormLayout {
  // model
  val uploadReceiver = new MemoryUploadReceiver

  // ui
  val sltMimeType = new ListSelect("Mime type")
  val lblUploadStatus = new Label
  val txtFileId = new TextField("File id") //with ValueType[String]
  val upload = new Upload(null, uploadReceiver) with UploadEventHandler {
    setImmediate(true)
    setButtonCaption("Select")

    def handleEvent(e: com.vaadin.ui.Component.Event) = e match {
      case e: Upload#SucceededEvent =>
        alterNameTextField()
      case e: Upload#FailedEvent =>
        uploadReceiver.uploadRef.set(None)
        alterNameTextField()
      case _ =>
    }
  }

  // test
  txtFileId.value = "-0-"
  addComponents(this, lblUploadStatus, upload, sltMimeType, txtFileId)
  alterNameTextField()

  def alterNameTextField() = let(uploadReceiver.uploadRef.get) { uploadOpt =>
    lblUploadStatus setValue (uploadOpt match {
      case Some(upload) => upload.filename
      case _ => "No file selected"
    })
  }
}

class FileDocEditor(app: VaadinApplication, doc: FileDocumentDomainObject) {
  val ui = letret(new FileDocEditorUI) { ui =>
    ui.tblFiles.itemsProvider = () =>
      doc.getFiles.toSeq collect {
        case (fileId, fdf) =>
          fileId -> List(fileId, fdf.getId, fdf.getMimeType, fdf.getInputStreamSource.getSize.toString, (fileId == doc.getDefaultFileId).toString)
      }

    ui.miAdd setCommand block {
      app.initAndShow(new OkCancelDialog("Add file")) { w =>
        let(w setMainContent new FileDocDialogContent) { c =>
          w addOkButtonClickListener {
            c.uploadReceiver.uploadRef.get match {
              case Some(upload) =>
                val file = new FileDocumentFile
                val source = new InputStreamSource {
                  def getInputStream = new ByteArrayInputStream(upload.content)
                  def getSize = upload.content.length
                }

                file.setInputStreamSource(source)
                file.setFilename(c.txtFileId.value)

                doc.addFile(c.txtFileId.value, file)
                ui.tblFiles.reload
              case _ =>
            }
          }
        }
      }
    }

    ui.miEdit setCommand block {

    }

    ui.miDelete setCommand block {

    }

    ui.miSetDefault setCommand block {

    }
  }
}



