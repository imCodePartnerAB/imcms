package com.imcode
package imcms.admin.document.template.file

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.user._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin._
import com.vaadin.ui.Window.Notification
import java.io.File
import imcms.admin.filesystem._
import org.apache.commons.io.FileUtils

//Int box templateMapper.getCountOfDocumentsUsingTemplate(t)

//                    val in = new ByteArrayInputStream(upload.content)
//                    val result = templateMapper.saveTemplate(c.txtName.value,
//                        upload.filename, in, c.chkOverwriteExisting.booleanValue)
//
//                    result match {
//                      case 0 => reloadTableItems // ok
//                      case -1 => error("File exists") // file exists
//                      case -2 => error("IO error")  // io error
//                      case n => error("Unknown error: " + n)
//                    }


class TemplateFileManager(app: ImcmsApplication) {
  val fileRE = """(?i)(.+)\.(\S+)""".r // name,ext
  val templateMapper = Imcms.getServices.getTemplateMapper
  val browser = letret(new FileBrowser) { browser =>
    browser.addLocation("Templates", Location(templateMapper.getTemplateDirectory, Location.templateFileFilter))
  }

  val ui = letret(new TemplateFileManagerUI(browser.ui)) { ui =>
    //ui.tblTemplates addListener block { handleSelection() }
    ui.miUpload setCommand block {
      app.initAndShow(new FileUploadDialog("Upload template file")) { dlg =>
        dlg.addOkHandler {
          for {
            data <- dlg.upload.data
            dir <- browser.dirTreeSelection
            filename = dlg.upload.ui.txtFilename.value // check not empty
            file = new File(dir, filename)
          } {
            if (file.exists && !dlg.upload.ui.ckhOverwrite.booleanValue) error("File exists")
            else {
              FileUtils.writeByteArrayToFile(file, data.content)
              browser.reloadDirContent
            }
          }
        }
      }
    }
    ui.miRename setCommand block {
      browser.dirContentSelection foreach { file =>
        app.initAndShow(new OkCancelDialog("Rename template file")) { dlg =>
          val fileRenameUI = letret(new FileRenameUI) { c =>
            c.txtName.value = file.getName
          }

          dlg.mainUI = fileRenameUI
          dlg.addOkHandler {
            if (canManage) { // move up!!
              for {
                Seq(name, ext) <- fileRE.unapplySeq(file.getName)
                template <- ?(templateMapper.getTemplateByName(name))
                Seq(newName, _) <- fileRE.unapplySeq(fileRenameUI.txtName.value)
              } templateMapper.renameTemplate(name, newName)
            } else error("NO PERMISSIONS")

            browser.reloadDirContent   // <- reload!!
          }
        }
      }
    }
    ui.miEditContent setCommand block {
      browser.dirContentSelection foreach { file =>
        app.initAndShow(new Dialog("Template file content") with CustomSizeDialog) { dlg =>
          dlg.mainContent = letret(new TemplateContentEditorUI) { c =>
            c.txtContent.value = scala.io.Source.fromFile(file).mkString
          }

          dlg setWidth "600px"
          dlg setHeight "800px"
        }
      }
    }
    ui.miDelete setCommand block {
      browser.dirContentSelection foreach { file =>
        app.initAndShow(new ConfirmationDialog("Delete selected template file?")) { dlg =>
          dlg addOkHandler {
            if (canManage) {
              for {
                Seq(name, ext) <- fileRE.unapplySeq(file.getName)
                template <- ?(templateMapper.getTemplateByName(name))
              } templateMapper deleteTemplate template
            } else error("NO PERMISSIONS")

            browser.reloadDirContent   // <- reload!!
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
//    ui.tblTemplates.removeAllItems
//    for {
//      vo <- templateMapper.getAllTemplates
//      name = vo.getName
//      kind = fileExtRE.unapplySeq(vo.getFileName) map (_.head) get
//    } ui.tblTemplates.addItem(Array[AnyRef](name, kind, Int box templateMapper.getCountOfDocumentsUsingTemplate(vo)), name)
//
//    handleSelection()
  }

  private def handleSelection() {
//    let(canManage && ui.tblTemplates.isSelected) { isSelected =>
//      ui.miEdit.setEnabled(isSelected)
//      ui.miEditContent.setEnabled(isSelected)
//      ui.miDelete.setEnabled(isSelected)
//    }
  }
}

class TemplateFileManagerUI(browserUI: FileBrowserUI) extends VerticalLayout with Spacing with UndefinedSize {
  import com.imcode.imcms.vaadin.Theme.Icons._

  val mb = new MenuBar with UndefinedSize
  val miUpload = mb.addItem("Upload", New16)
  val miRename = mb.addItem("Rename", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miEditContent = mb.addItem("Edit content", EditContent16)
  val miHelp = mb.addItem("Help", Help16)

  addComponents(this, mb, browserUI)
  browserUI.setWidth("400px"); setHeight("500px")
}


class FileRenameUI extends FormLayout with UndefinedSize {
  val txtName = new TextField("Name")

  addComponent(txtName)
}

class TemplateContentEditorUI extends VerticalLayout with FullSize {
  val txtContent = new TextField with FullSize {setRows(20)}

  addComponent(txtContent)
}

