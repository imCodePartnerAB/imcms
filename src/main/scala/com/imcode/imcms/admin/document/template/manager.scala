package com.imcode
package imcms.admin.document.template

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.user._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import com.vaadin.ui.Window.Notification
import imcms.admin.filesystem._
import org.apache.commons.io.FileUtils
import java.io.{ByteArrayInputStream, File}

class TemplateManager(app: ImcmsApplication) {
  val fileRE = """(?i)(.+?)(?:\.(\w+))?""".r // filename, (optional extension)
  val templateMapper = Imcms.getServices.getTemplateMapper

  val ui = letret(new TemplateManagerUI) { ui =>
    ui.tblTemplates addListener block { handleSelection() }
    ui.miUpload setCommand block {
      app.initAndShow(new FileUploadDialog("Upload template file")) { dlg =>
        // strips filename extension, trims and replaces spaces with underscores
        dlg.upload.fileNameToSaveAsName = fileRE.unapplySeq(_:String).map(_.head.trim.replaceAll("""\s""", "_")).get
        dlg.addOkHandler {
          for {
            data <- dlg.upload.data
            name = dlg.upload.ui.txtSaveAsName.value // check not empty
            in = new ByteArrayInputStream(data.content)
          } {
            // check can manage
            templateMapper.saveTemplate(name, data.filename, in, dlg.upload.ui.chkOverwrite.booleanValue) match {
              case 0 => reload() // ok
              case -1 => error("File exists") // file exists
              case -2 => error("IO error")  // io error
              case n => error("Unknown error: " + n)
            }
          }
        }
      }
    }
    ui.miRename setCommand block {
      whenSelected(ui.tblTemplates) { name =>
        app.initAndShow(new OkCancelDialog("Rename template")) { dlg =>
          val fileRenameUI = letret(new TemplateRenameUI) { c =>
            c.txtName.value = name
          }

          dlg.mainUI = fileRenameUI
          dlg.addOkHandler {
            if (canManage) { // move up!!
              templateMapper.renameTemplate(name, fileRenameUI.txtName.value)
            } else error("NO PERMISSIONS")

            reload()
          }
        }
      }
    }
    ui.miEditContent setCommand block {
      whenSelected(ui.tblTemplates) { name =>
        app.initAndShow(new Dialog("Template file content") with CustomSizeDialog) { dlg =>
          dlg.mainContent = letret(new TemplateContentEditorUI) { c =>
            c.txtContent.value = templateMapper.getTemplateData(name)
          }

          dlg setWidth "600px"
          dlg setHeight "800px"
        }
      }
    }
    ui.miDelete setCommand block {
      whenSelected(ui.tblTemplates) { name =>
        app.initAndShow(new ConfirmationDialog("Delete selected template?")) { dlg =>
          dlg addOkHandler {
            if (canManage) {
              ?(templateMapper.getTemplateByName(name)) foreach { template =>
                templateMapper deleteTemplate template
              }
            } else error("NO PERMISSIONS")

            reload()
          }
        }
      }
    }
  }

  reload()
  // END OF PRIMARY CONSTRUCTOR

  def canManage = app.user.isSuperAdmin

  //todo add sec check
  def reload() {
    ui.tblTemplates.removeAllItems
    for {
      vo <- templateMapper.getAllTemplates
      name = vo.getName
      fileRE(_, ext) = vo.getFileName
    } ui.tblTemplates.addItem(Array[AnyRef](name, ext, Int box templateMapper.getCountOfDocumentsUsingTemplate(vo)), name)

    handleSelection()
  }

  private def handleSelection() {
    let(canManage && ui.tblTemplates.isSelected) { isSelected =>
      ui.miDownload.setEnabled(isSelected)
      ui.miRename.setEnabled(isSelected)
      ui.miEditContent.setEnabled(isSelected)
      ui.miDelete.setEnabled(isSelected)
    }
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
  val miHelp = mb.addItem("Help", Help16)
  val tblTemplates = new Table with SingleSelect2[String] with Selectable with Immediate
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