package com.imcode
package imcms.admin.doc.template

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.user._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import com.vaadin.ui.Window.Notification
import imcms.admin.system.file._
import org.apache.commons.io.FileUtils
import java.io.{ByteArrayInputStream, File}
import imcms.security.{PermissionDenied, PermissionGranted}

//todo: common internal ex handler???
//todo: add related docs handling
//-upload fialog and save as handling
class TemplateManager(app: ImcmsApplication) {
  private val templateMapper = Imcms.getServices.getTemplateMapper
  private val fileRE = """(?i)(.+?)(?:\.(\w+))?""".r // filename, (optional extension)

  val ui = letret(new TemplateManagerUI) { ui =>
    ui.tblTemplates addValueChangeHandler { handleSelection() }
    ui.miUpload setCommandHandler {
      app.initAndShow(new FileUploadDialog("Upload template file")) { dlg =>
        // strips filename extension, trims and replaces spaces with underscores
        dlg.upload.fileNameToSaveAsName = fileRE.unapplySeq(_:String).map(_.head.trim.replaceAll("""\s""", "_")).get
        dlg.wrapOkHandler {
          for {
            data <- dlg.upload.data
            name = dlg.upload.ui.txtSaveAsName.value // todo: check not empty
            in = new ByteArrayInputStream(data.content)
          } {
            app.privileged(permission) {
              templateMapper.saveTemplate(name, data.filename, in, dlg.upload.ui.chkOverwrite.booleanValue) match {
                case 0 => reload() // ok
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
          val fileRenameUI = letret(new TemplateRenameUI) { c =>
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
          dlg.mainContent = letret(new TemplateContentEditorUI) { c =>
            c.txtContent.value = templateMapper.getTemplateData(name)
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
              EX.allCatch.either(?(templateMapper getTemplateByName name) foreach templateMapper.deleteTemplate) match {
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

    let(canManage) { canManage =>
      import ui._
      tblTemplates.setSelectable(canManage)
      forlet[{def setEnabled(e: Boolean)}](miUpload, miDownload, miRename, miDelete, miEditContent) { _ setEnabled canManage }   //ui.mb,
    }

    handleSelection()
  }

  private def handleSelection() {
    import ui._
    let(canManage && tblTemplates.isSelected) { enabled =>
      forlet(miDownload, miRename, miEditContent, miDelete) { _ setEnabled enabled }
    }

    miDocuments.setEnabled(?(tblTemplates.value) map { name =>
      templateMapper.getCountOfDocumentsUsingTemplate(templateMapper.getTemplateByName(name)) > 0
    } getOrElse false)
  }
}

class TemplateManagerUI extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icons._

  val mb = new MenuBar
  val miUpload = mb.addItem("Upload", New16)
  val miDownload = mb.addItem("Download", New16)
  val miRename = mb.addItem("Rename", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miEditContent = mb.addItem("Edit content", EditContent16)
  val miDocuments = mb.addItem("Related documents", Documents16)
  val miHelp = mb.addItem("Help", Help16)
  val tblTemplates = new Table with SingleSelect2[TemplateName] with Selectable with Immediate
  val rc = new ReloadableContentUI(tblTemplates)

  addContainerProperties(tblTemplates,
    CP[String]("Name"),
    CP[String]("Type"),
    CP[JInteger]("Document count using this template"))

  addComponents(this, mb, rc)
}


class TemplateRenameUI extends FormLayout with UndefinedSize {
  val txtName = new TextField("Name")

  addComponent(txtName)
}

class TemplateContentEditorUI extends VerticalLayout with FullSize {
  val txtContent = new TextField with FullSize {setRows(20)}

  addComponent(txtContent)
}