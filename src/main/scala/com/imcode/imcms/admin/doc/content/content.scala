package com.imcode
package imcms
package admin.doc.content

import scala.collection.mutable.{Map => MMap}
import scala.collection.breakOut
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
import com.vaadin.data.Property.{ValueChangeEvent, ValueChangeListener}
import java.io.{FileInputStream, ByteArrayInputStream}
import com.imcode.imcms.admin.system.file.{UploadedFile, FileUploaderDialog}
import scala.collection.immutable.ListMap
import imcode.util.io.{FileInputStreamSource, InputStreamSource}
import com.vaadin.ui.Table.ColumnGenerator
import com.vaadin.terminal.{ThemeResource, ExternalResource}
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


//case class MimeType(name: String, displayName: String)


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

/**
 * doc is used to create initial state.
 */
class FileDocContentEditor(val doc: FileDocumentDomainObject) extends DocContentEditor with ImcmsServicesSupport {
  type MimeType = String
  type DisplayName = String

  case class State(fdfs: Map[FileId, FileDocumentFile], defaultFdfId: Option[FileId])


  private var state = State(
    doc.getFiles.map { case (id, fdf) => id -> fdf.clone } (breakOut),
    ?(doc.getDefaultFileId)
  )

  private def mimeTypes: ListMap[MimeType, DisplayName] =
    imcmsServices.getDocumentMapper.getAllMimeTypesWithDescriptions(ui.getApplication.user)
    .map { case Array(mimeType, displayName) => mimeType -> displayName } (breakOut)

  private def findFDFByName(name: String, ignoreCase: Boolean = true): Option[FileDocumentFile] = {
    val namePredicate: String => Boolean = if (ignoreCase) name.equalsIgnoreCase else (name ==)

    state.fdfs.collectFirst {
      case (_, fdf) if namePredicate(fdf.getFilename) => fdf
    }
  }

  val ui = letret(new FileDocContentEditorUI) { ui =>
    ui.tblFiles.addGeneratedColumn("Type", new ColumnGenerator {
      def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef): String = {
        val mimeType = state.fdfs(itemId.asInstanceOf[FileId]).getMimeType
        mimeTypes.get(mimeType) match {
          case Some(displayName) => displayName
          case _ => mimeType
        }
      }
    })

    ui.tblFiles.addGeneratedColumn("Size", new ColumnGenerator {
      // todo: calculate size, add unit (kb, mb, etc)
      def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef): String =
        state.fdfs(itemId.asInstanceOf[FileId]).getInputStreamSource.getSize.toString
    })

    ui.tblFiles.addGeneratedColumn("Default", new ColumnGenerator {
      def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) = letret(new CheckBox) { chk =>
        for (id <- state.defaultFdfId if id == itemId.asInstanceOf[FileId]) {
          chk.check
        }

        chk.setReadOnly(true)
      }
    })

    ui.tblFiles.setColumnAlignment("Default", Table.ALIGN_CENTER)

    ui.miUpload.setCommandHandler {
      ui.getApplication.initAndShow(new FileUploaderDialog("Add file")) { dlg =>
        dlg.setOkHandler {
          for (UploadedFile(_, mimeType, file) <- dlg.uploader.uploadedFile) {
            val saveAsName = dlg.uploader.saveAsName

            findFDFByName(saveAsName) |> {
              case _ if saveAsName.isEmpty => Left("Name is required")

              case Some(fdf) if !dlg.uploader.isOverwrite => Left("File with such name allready exists")

              case Some(fdf) => Right(fdf) // return new instance?

              case None => letret(Right(new FileDocumentFile)) {
                case Right(fdf) =>
                  val id = (
                    for ((IntNumber(id), _) <- state.fdfs) yield id
                  ) |> { ids =>
                    if (ids.isEmpty) 1 else ids.max + 1
                  } |> {
                    _.toString
                  }

                  fdf.setId(id)
              }
            } |> {
              case Left(errMsg) =>
                dlg.uploader.ui.txtSaveAsName.setComponentError(errMsg)
                ui.getApplication.showErrorNotification(errMsg)

              case Right(fdf) =>
                fdf.setFilename(saveAsName)
                fdf.setMimeType(mimeType)
                fdf.setInputStreamSource(new FileInputStreamSource(file))

                state = State(state.fdfs.updated(fdf.getId, fdf), state.defaultFdfId orElse Some(fdf.getId))

                reload()
                dlg.close()
            }
          }
        }
      }
    } // ui.miUpload.setCommandHandler

    ui.miEditProperties.setCommandHandler {
      whenSingle(ui.tblFiles.selection) { fileId =>
        ui.getApplication.initAndShow(new OkCancelDialog("Edit file properties")) { dlg =>
          val fdf = state.fdfs(fileId)
          val editorUI = letret(new FileDocFilePropertiesEditorUI) { eui =>
            eui.txtId.value = fdf.getId
            eui.txtName.value = fdf.getFilename

            for ((mimeType, displayName) <- mimeTypes) {
              eui.cbType.addItem(mimeType, "%s (%s)".format(displayName, mimeType))
            }

            val mimeType = fdf.getMimeType

            if (mimeTypes.get(mimeType).isEmpty) {
              eui.cbType.addItem(mimeType, mimeType)
            }

            eui.cbType.value = mimeType
          }

          dlg.mainUI = editorUI
          dlg.setOkHandler {
            val errors = MMap.empty[AbstractComponent, ErrorMsg]

            editorUI.txtId.trimOpt match {
              case None =>
                errors += editorUI.txtId -> "id is required"

              case Some(newId) if newId != fdf.getId =>
                for (fdf2 <- state.fdfs.get(newId)) {
                  errors += editorUI.txtId -> "id is allready assigned to other file in this document"
                }

              case _ =>
            }

            editorUI.txtName.trimOpt match {
              case None =>
                errors += editorUI.txtName -> "name is required"

              case Some(newName) if !newName.equalsIgnoreCase(fdf.getFilename) =>
                for (fdf2 <- findFDFByName(newName)) {
                  errors += editorUI.txtName -> "name is allready assigned to other file in this document"
                }

              case _ =>
            }

            editorUI.txtId.setComponentError(null)
            editorUI.txtName.setComponentError(null)

            if (errors.nonEmpty) {
              for ((component, errMsg) <- errors) {
                component.setComponentError(errMsg)
              }
            } else {
              // todo: mixin trait: so we can delete temp file??
              // if id has changed, update doc filemap
              val newId = editorUI.txtId.trim
              val fdfs = state.fdfs - fileId + (newId -> fdf)
              val defaultFdfId = state.defaultFdfId.map {
                case id if (fileId == id) && (fileId != newId) => newId
                case id => id
              }

              fdf.setId(newId)
              fdf.setFilename(editorUI.txtName.trim)
              fdf.setMimeType(editorUI.cbType.value)

              state = State(fdfs, defaultFdfId)

              reload()
              dlg.close()
            }
          }
        }
      }
    }

    ui.miDelete setCommandHandler {
      whenSelected(ui.tblFiles) { fileIds =>
        val fdfs = state.fdfs filterKeys fileIds.toSet.andThen(!=)
        val ids = fdfs.keySet
        val defaultFdfId = ids.find(state.defaultFdfId.get ==) orElse ids.headOption

        state = State(fdfs, defaultFdfId)

        reload()
      }
    }

    ui.miSetDefault setCommandHandler {
      whenSingle(ui.tblFiles.selection) { fileId =>
        state = state.copy(defaultFdfId = Some(fileId))

        reload()
      }
    }
  }

  def reload() {
    ui.tblFiles.removeAllItems()

    for ((fileId, fdf) <- state.fdfs) {
      ui.tblFiles.addItem(Array[AnyRef](fileId, fdf.getFilename), fileId)
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
class FileDocContentEditorUI extends VerticalLayout with Spacing with Margin with FullSize {
  val mb = new MenuBar
  val miUpload = mb.addItem("Upload", null)
  val miEditProperties = mb.addItem("Edit properties", null)
  val miDelete = mb.addItem("Delete", null)
  val miSetDefault = mb.addItem("Set default", null)

  val tblFiles = new Table with MultiSelect[FileId] with Selectable with Immediate with FullSize {
    addContainerProperties(this,
      ContainerProperty[FileId]("Id"),
      ContainerProperty[String]("Name"))
  }

  addComponents(this, mb, tblFiles)
  setExpandRatio(tblFiles, 1.0f)
}


class FileDocFilePropertiesEditorUI extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Required
  val txtName = new TextField("Name") with Required
  val cbType = new ComboBox("Type") with Required with SingleSelect[String] with NoTextInput with NoNullSelection

  addComponents(this, txtId, txtName, cbType)
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