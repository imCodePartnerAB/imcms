package com.imcode
package imcms.admin.doc.template

import scala.util.control.{Exception => Ex}
import scala.collection.JavaConverters._
import com.vaadin.ui._
import imcode.server.user._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{_}
import imcms.admin.instance.file._
import org.apache.commons.io.FileUtils
import imcms.security.{PermissionDenied, PermissionGranted}
import java.io.{FileInputStream, ByteArrayInputStream, File}
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.server._
import com.vaadin.server.Page

//todo: common internal ex handler???
//todo: add related docs handling
//-upload fialog and save as handling
class TemplateManager(app: UI) {
  private val templateMapper = Imcms.getServices.getTemplateMapper
  private val fileRE = """(?i)(.+?)(?:\.(\w+))?""".r // filename, (optional extension)

  val ui = new TemplateManagerUI |>> { ui =>
    ui.tblTemplates addValueChangeHandler { handleSelection() }
    ui.miUpload.setCommandHandler {
      new FileUploaderDialog("Upload template file") |>> { dlg =>
        // strips filename extension, trims and replaces spaces with underscores
        dlg.uploader.fileNameToSaveAsName = fileRE.unapplySeq(_:String).map(_.head.trim.replaceAll("""\s""", "_")).get
        dlg.setOkButtonHandler {
          for {
            uploadedFile <- dlg.uploader.uploadedFile
            name = dlg.uploader.ui.txtSaveAsName.value // todo: check not empty
            in = new FileInputStream(uploadedFile.file)
          } {
            app.privileged(permission) {
              templateMapper.saveTemplate(name, uploadedFile.name, in, dlg.uploader.ui.chkOverwrite.booleanValue) match {
                case 0 =>
                  FileUtils.deleteQuietly(uploadedFile.file)
                  reload() // ok
                case -1 =>
                  Page.getCurrent.showErrorNotification("Template with such name allready exists")
                  sys.error("File exists")
                case -2 =>
                  Page.getCurrent.showErrorNotification("Internal error")
                  sys.error("IO error")
                case n =>
                  Page.getCurrent.showErrorNotification("Internal error")
                  sys.error("Unknown error")
              }
            }
          }
        }
      } |> UI.getCurrent.addWindow
    }
    ui.miRename.setCommandHandler {
      whenSelected(ui.tblTemplates) { name =>
        new OkCancelDialog("Rename template") |>> { dlg =>
          val fileRenameUI = new TemplateRenameUI |>> { c =>
            c.txtName.value = name
          }

          dlg.mainUI = fileRenameUI
          dlg.setOkButtonHandler {
            app.privileged(permission) {
              templateMapper.renameTemplate(name, fileRenameUI.txtName.value)
            }

            reload()
          }
        } |> UI.getCurrent.addWindow
      }
    }
    ui.miEditContent.setCommandHandler {
      whenSelected(ui.tblTemplates) { name =>
        new Dialog("Template file content") with CustomSizeDialog with NoContentMarginDialog |>> { dlg =>
          dlg.mainUI = new TemplateContentEditorUI |>> { c =>
            c.txaContent.value = templateMapper.getTemplateData(name)
          }

          dlg.setWidth("600px")
          dlg.setHeight("800px")
        } |> UI.getCurrent.addWindow
      }
    }
    ui.miDelete.setCommandHandler {
      whenSelected(ui.tblTemplates) { name =>
        new ConfirmationDialog("Delete selected template?") |>> { dlg =>
          dlg.setOkButtonHandler {
            app.privileged(permission) {
              Ex.allCatch.either(templateMapper.getTemplateByName(name).asOption.foreach(templateMapper.deleteTemplate)) match {
                case Right(_) =>
                  Page.getCurrent.showInfoNotification("Template has been deleted")
                case Left(ex) =>
                  Page.getCurrent.showErrorNotification("Internal error")
                  throw ex
              }

              reload()
            }
          }
        } |> UI.getCurrent.addWindow
      }
    }
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = UI.getCurrent.imcmsUser.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage templates")

  def reload() {
    ui.tblTemplates.removeAllItems
    for {
      vo <- templateMapper.getAllTemplates.asScala
      name = vo.getName
      fileRE(_, ext) = vo.getFileName
    } ui.tblTemplates.addItem(Array[AnyRef](name, ext, Int box templateMapper.getCountOfDocumentsUsingTemplate(vo)), name)

    canManage |> { value =>
      import ui._
      tblTemplates.setSelectable(value)
      doto[{def setEnabled(e: Boolean)}](miUpload, miDownload, miRename, miDelete, miEditContent) { _ setEnabled value }   //ui.mb,
    }

    handleSelection()
  }

  private def handleSelection() {
    import ui._
    (canManage && tblTemplates.isSelected) |> { enabled =>
      doto(miDownload, miRename, miEditContent, miDelete) { _ setEnabled enabled }
    }

    miDocuments.setEnabled(tblTemplates.value.asOption.map { name =>
      templateMapper.getCountOfDocumentsUsingTemplate(templateMapper.getTemplateByName(name)) > 0
    } getOrElse false)
  }
}

class TemplateManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miUpload = mb.addItem("Upload", New16, null)
  val miDownload = mb.addItem("Download", New16, null)
  val miRename = mb.addItem("Rename", Edit16, null)
  val miDelete = mb.addItem("Delete", Delete16, null)
  val miEditContent = mb.addItem("Edit content", EditContent16, null)
  val miDocuments = mb.addItem("Related documents", Documents16, null)
  val miHelp = mb.addItem("Help", Help16, null)
  val tblTemplates = new Table with SingleSelect[TemplateName] with Selectable with Immediate
  val rc = new ReloadableContentUI(tblTemplates)

  addContainerProperties(tblTemplates,
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[String]("Type"),
    PropertyDescriptor[JInteger]("Document count using this template"))

  this.addComponents(mb, rc)
}


class TemplateRenameUI extends FormLayout with UndefinedSize {
  val txtName = new TextField("Name")

  addComponent(txtName)
}

class TemplateContentEditorUI extends VerticalLayout with FullSize {
  val txaContent = new TextArea with FullSize |>> {
    _.setRows(20)
  }

  addComponent(txaContent)
}