package com.imcode
package imcms
package admin.doc.content

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


class FileDocContentEditor(val doc: FileDocumentDomainObject) extends DocContentEditor with ImcmsServicesSupport {
  type MimeType = String
  type DisplayName = String

  def mimeTypes: ListMap[MimeType, DisplayName] =
    imcmsServices.getDocumentMapper.getAllMimeTypesWithDescriptions(ui.getApplication.user)
    .map { case Array(mimeType, displayName) => mimeType -> displayName } (breakOut)

  val ui = letret(new FileDocContentEditorUI) { ui =>
    ui.tblFiles.addGeneratedColumn("Type", new ColumnGenerator {
      def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef): String = {
        val mimeType = doc.getFiles.get(itemId.asInstanceOf[FileId]).getMimeType
        mimeTypes.get(mimeType) match {
          case Some(displayName) => displayName
          case _ => mimeType
        }
      }
    })

    ui.tblFiles.addGeneratedColumn("Size", new ColumnGenerator {
      // todo: calculate size, add unit (kb, mb, etc)
      def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef): String =
        doc.getFiles.get(itemId.asInstanceOf[FileId]).getInputStreamSource.getSize.toString
    })

    ui.tblFiles.addGeneratedColumn("Default", new ColumnGenerator {
      // todo: change icon
      def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) = letret(new CheckBox) { chk =>
        if (itemId.asInstanceOf[FileId] == doc.getDefaultFileId) {
          chk.check
        }

        chk.setReadOnly(true)
      }
    })

    ui.tblFiles.setColumnAlignment("Default", Table.ALIGN_CENTER)

    ui.miUpload.setCommandHandler {
      ui.getApplication.initAndShow(new FileUploaderDialog("Add file")) { dlg =>
        dlg.setOkHandler {
          dlg.uploader.uploadedFile match {
            case Some(UploadedFile(name, mimeType, file)) =>
              doc.getFiles.collectFirst { case (_, fdf) if fdf.getFilename == name => fdf } |> {
                case Some(fdf) if !dlg.uploader.isOverwrite => Left("File with such name allready exists")

                case Some(fdf) => Right(fdf)

                case None => letret(Right(new FileDocumentFile)) {
                  case Right(fdf) =>
                    val id = (
                      for ((IntNumber(id), _) <- doc.getFiles) yield id
                    ) |> { ids =>
                      if (ids.isEmpty) 1 else ids.max + 1
                    } |> {
                      _.toString
                    }

                    fdf.setId(id)
                    fdf.setFilename(name)
                }
              } |> {
                case Left(errMsg) =>
                  ui.getApplication.showErrorNotification(errMsg)

                case Right(fdf) =>
                  fdf.setMimeType(mimeType)
                  fdf.setInputStreamSource(new FileInputStreamSource(file))

                  doc.addFile(fdf.getId, fdf)
                  reload()
                  dlg.close()
              }

            case _ =>
          }
        }
      }
    } // ui.miUpload.setCommandHandler

    ui.miEditProperties.setCommandHandler {
      whenSingle(ui.tblFiles.selection) { fileId =>
        ui.getApplication.initAndShow(new OkCancelDialog("Edit file properties")) { dlg =>
          val fdf = doc.getFile(fileId)
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
          dlg.wrapOkHandler {
            // todo: validate
            fdf.setId(editorUI.txtId.value)
            fdf.setFilename(editorUI.txtName.value)
            fdf.setMimeType(editorUI.cbType.value)

            reload()
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
    ui.tblFiles.removeAllItems()

    val defaultFileId = doc.getDefaultFileId

    for ((fileId, fdf) <- doc.getFiles) {
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
  val txtId = new TextField("Id")
  val txtName = new TextField("Name")
  val cbType = new ComboBox("Type") with SingleSelect[String] with NoTextInput with NoNullSelection

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