package com.imcode
package imcms.admin.doc.template

import scala.util.control.{Exception => Ex}
import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.user._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import com.vaadin.ui.Window.Notification
import imcms.admin.system.file._
import org.apache.commons.io.FileUtils
import imcms.security.{PermissionDenied, PermissionGranted}
import java.io.{FileInputStream, ByteArrayInputStream, File}
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._

//todo: common internal ex handler???
//todo: add related docs handling
//-upload fialog and save as handling
class TemplateManager(app: ImcmsApplication) {
  private val templateMapper = Imcms.getServices.getTemplateMapper
  private val fileRE = """(?i)(.+?)(?:\.(\w+))?""".r // filename, (optional extension)

  val ui = new TemplateManagerUI |>> { ui =>
    ui.tblTemplates addValueChangeHandler { handleSelection() }
    ui.miUpload setCommandHandler {
      app.initAndShow(new FileUploaderDialog("Upload template file")) { dlg =>
        // strips filename extension, trims and replaces spaces with underscores
        dlg.uploader.fileNameToSaveAsName = fileRE.unapplySeq(_:String).map(_.head.trim.replaceAll("""\s""", "_")).get
        dlg.wrapOkHandler {
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
                  app.showErrorNotification("Template with such name allready exists")
                  sys.error("File exists")
                case -2 =>
                  app.showErrorNotification("Internal error")
                  sys.error("IO error")
                case n =>
                  app.showErrorNotification("Internal error")
                  sys.error("Unknown error")
              }
            }
          }
        }
      }
    }
    ui.miRename setCommandHandler {
      whenSelected(ui.tblTemplates) { name =>
        app.initAndShow(new OkCancelDialog("Rename template")) { dlg =>
          val fileRenameUI = new TemplateRenameUI |>> { c =>
            c.txtName.value = name
          }

          dlg.mainUI = fileRenameUI
          dlg.wrapOkHandler {
            app.privileged(permission) {
              templateMapper.renameTemplate(name, fileRenameUI.txtName.value)
            }

            reload()
          }
        }
      }
    }
    ui.miEditContent setCommandHandler {
      whenSelected(ui.tblTemplates) { name =>
        app.initAndShow(new Dialog("Template file content") with CustomSizeDialog with NoMarginDialog) { dlg =>
          dlg.mainUI = new TemplateContentEditorUI |>> { c =>
            c.txaContent.value = templateMapper.getTemplateData(name)
          }

          dlg setWidth "600px"
          dlg setHeight "800px"
        }
      }
    }
    ui.miDelete setCommandHandler {
      whenSelected(ui.tblTemplates) { name =>
        app.initAndShow(new ConfirmationDialog("Delete selected template?")) { dlg =>
          dlg wrapOkHandler {
            app.privileged(permission) {
              Ex.allCatch.either(Option(templateMapper getTemplateByName name) foreach templateMapper.deleteTemplate) match {
                case Right(_) =>
                  app.showInfoNotification("Template has been deleted")
                case Left(ex) =>
                  app.showErrorNotification("Internal error")
                  throw ex
              }

              reload()
            }
          }
        }
      }
    }
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = app.user.isSuperAdmin
  def permission = if (canManage) PermissionGranted else PermissionDenied("No permissions to manage templates")

  def reload() {
    ui.tblTemplates.removeAllItems
    for {
      vo <- templateMapper.getAllTemplates
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

    miDocuments.setEnabled(Option(tblTemplates.value) map { name =>
      templateMapper.getCountOfDocumentsUsingTemplate(templateMapper.getTemplateByName(name)) > 0
    } getOrElse false)
  }
}

class TemplateManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icon._

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
    CP[String]("Name"),
    CP[String]("Type"),
    CP[JInteger]("Document count using this template"))

  addComponentsTo(this, mb, rc)
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