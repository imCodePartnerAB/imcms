package com.imcode
package imcms.admin.system.file

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import imcode.server.document.{CategoryDomainObject}
import java.io.File
import com.vaadin.ui.Window.Notification
import org.apache.commons.io.FileUtils
import com.vaadin.terminal.FileResource

class FileManager(app: ImcmsApplication) {
  val browser = letret(new FileBrowser(isMultiSelect = true)) { browser =>
    browser.addPlace("Home", Place(Imcms.getPath))
    browser.addPlace("Templates", Place(new File(Imcms.getPath, "WEB-INF/templates/text")))
    browser.addPlace("Images", Place(new File(Imcms.getPath, "images")))
    browser.addPlace("Conf", Place(new File(Imcms.getPath, "WEB-INF/conf")))
    browser.addPlace("Logs", Place(new File(Imcms.getPath, "WEB-INF/logs")))
    browser.addPlace("Logs", Place(new File(Imcms.getPath, "/abcdef")))
  }

  val ui = letret(new FileManagerUI(browser.ui)) { ui =>

    /**
     * Recursively applies op to an item.
     * @param opFailMsg - fail message with unbound format parameter substitutable with fsNode - ex. "Unable to copy %s."
     */
    def applyOpToItems(items: Seq[File], op: File => Unit, opFailMsg: String) {
      items match {
        case item :: rest =>
          def applyOpToRestItems() = applyOpToItems(rest, op, opFailMsg)
          def applyOpToEmptyItems() = applyOpToItems(Nil, op, opFailMsg)

          try {
            op(item)
            applyOpToRestItems()
          } catch {
            case _ => app.initAndShow(new ConfirmationDialog(opFailMsg format item)) { dlg =>
              dlg.btnOk.setCaption("Skip")
              dlg.setOkHandler { applyOpToRestItems() }
              dlg.setCancelHandler { applyOpToEmptyItems() }
            }
          }

        case _ => browser.reloadLocationDir(preserveDirTreeSelection = true)
      }
    }

    ui.miEditDelete setCommandHandler {
      for (selection <- browser.selection if selection.nonEmpty) {
        app.initAndShow(new ConfirmationDialog("Delete selected items")) { dlg =>
          dlg setOkHandler {
            applyOpToItems(selection.items, FileUtils.forceDelete, "Unable to delete item %s.")
          }
        }
      }
    }

    ui.miEditCopy setCommandHandler {
      for (selection <- browser.selection if selection.nonEmpty) {
        val dirSelectBrowser = letret(new FileBrowser(isSelectable = false)) { b =>
          b.addPlace("Home", Place(Imcms.getPath))
        }

        app.initAndShow(new DirSelectionDialog("Select distenation directory", dirSelectBrowser)) { dlg =>
          dlg setOkHandler {
            for (destSelection <- dirSelectBrowser.selection; destDir = destSelection.dir) {
              def op(item: File) = if (item.isFile) FileUtils.copyFileToDirectory(item, destDir)
                                   else FileUtils.copyDirectoryToDirectory(item, destDir)

              applyOpToItems(selection.items, op, "Unable to copy item %s.")
            }
          }
        }
      }
    }

    ui.miEditMove setCommandHandler {
      for (selection <- browser.selection if selection.nonEmpty) {
        val dirSelectBrowser = letret(new FileBrowser(isSelectable = false)) { b =>
          b.addPlace("Home", Place(Imcms.getPath))
        }

        app.initAndShow(new DirSelectionDialog("Select distenation directory", dirSelectBrowser)) { dlg =>
          dlg setOkHandler {
            for (destSelection <- dirSelectBrowser.selection; destDir = destSelection.dir) {
              def op(item: File) = if (item.isFile) FileUtils.moveDirectoryToDirectory(item, destDir, false)
                                   else FileUtils.moveDirectoryToDirectory(item, destDir, false)

              applyOpToItems(selection.items, op, "Unable to move item %s.")
            }
          }
        }
      }
    }

    ui.miFilePreview setCommandHandler {
      for (selection <- browser.selection; item <- selection.first /*isViewable(item)*/) {
        app.initAndShow(new OKDialog("Content of %s" format item) with CustomSizeDialog, resizable = true) { dlg =>
          dlg.mainUI = new TextArea("", scala.io.Source.fromFile(item).mkString) with ReadOnly with FullSize
          dlg.setSize((500, 500))
        }
      }
    }

    ui.miFileEdit setCommandHandler {
      for (selection <- browser.selection; item <- selection.first /*isEditable(item)*/) {
        app.initAndShow(new OkCancelDialog("Edit content of %s" format item) with CustomSizeDialog, resizable = true) { dlg =>
          val textArea = new TextArea("", scala.io.Source.fromFile(item).mkString) with FullSize
          dlg.mainUI = textArea
          dlg.setSize((500, 500))

          dlg.setOkHandler {
            FileUtils.writeStringToFile(item, textArea.value)
          }
        }
      }
    }

    ui.miFileUpload setCommandHandler {
      for (selection <- browser.selection; dir = selection.dir) {
        app.initAndShow(new FileUploadDialog("Upload file")) { dlg =>
          dlg.setOkHandler {
            for {
              UploadedData(_, _, content) <- dlg.upload.data
              file = new File(dir, dlg.upload.saveAsName)
            } {
              if (file.exists && !dlg.upload.isOverwrite) {
                app.show(new MsgDialog("File allready exists", "Please choose different name or check 'overwrite existing'"))
                error("File %s allready exists" format file.getCanonicalPath)
              } else {
                FileUtils.writeByteArrayToFile(file, content)
                browser.reloadLocationItems()
              }
            }
          }
        }
      }
    }

    ui.miFileDownload setCommandHandler {
      for (selection <- browser.selection; item <- selection.first if item.isFile) {
        app.getMainWindow.open(
          new FileResource(item, app) {
            override def getStream() = letret(super.getStream) { ds =>
              ds.setParameter("Content-Disposition", """attachment; filename="%s"""" format item.getName)
            }
          }
        )
      }
    }

    ui.miViewReload setCommandHandler {
      browser.reloadLocationDir()
    }
  }
}


class FileManagerUI(browserUI: FileBrowserUI) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miFile = mb.addItem("File")
  val miFilePreview = miFile.addItem("Preview")
  val miFileEdit = miFile.addItem("Edit content")
  val miFileUpload = miFile.addItem("Upload")
  val miFileDownload = miFile.addItem("Download")
  val miNew = mb.addItem("New")
  val miNewDir = miNew.addItem("Directory")
  val miEdit = mb.addItem("Edit")
  val miEditCopy = miEdit.addItem("Copy to...")
  val miEditMove = miEdit.addItem("Move to...")
  val miEditDelete = miEdit.addItem("Delete")
  val miView = mb.addItem("View")
  val miViewReload = miView.addItem("Reload")
  val miHelp = mb.addItem("Help")

  addComponents(this, mb, browserUI)
  setExpandRatio(browserUI, 1.0f)
}