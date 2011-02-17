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
  }

  val ui = letret(new FileManagerUI(browser.ui)) { ui =>

    /**
     * Recursively applies op to an item.
     * @param opFailMsg - fail message with unbound format parameter substitutable with item - ex. "Unable to copy %s."
     */
    def applyOpToItems(items: Seq[File], op: File => Unit, opFailMsg: String) {
      items match {
        case item :: rest =>
          def applyOpToRestItems() = applyOpToItems(rest, op, opFailMsg)

          try {
            op(item)
            applyOpToRestItems()
          } catch {
            case _ => app.initAndShow(new ConfirmationDialog(opFailMsg format item)) { dlg =>
              dlg.btnOk.setCaption("Skip")
              dlg.setOkHandler { applyOpToRestItems() }
            }
          }

        case _ =>
      }
    }

    ui.miDelete setCommandHandler {
      if (browser.dirContentSelection.nonEmpty) {
        app.initAndShow(new ConfirmationDialog("Delete selected items")) { dlg =>
          dlg setOkHandler {
            applyOpToItems(browser.dirContentSelection.items, FileUtils.forceDelete, "Unable to delete item %s.")
            browser.reload()
          }
        }
      }
    }

    ui.miCopy setCommandHandler {
      if (browser.dirContentSelection.nonEmpty) {
        val dirSelectBrowser = letret(new FileBrowser(isSelectable = false)) { b =>
          b.addPlace("Home", Place(Imcms.getPath))
        }

        app.initAndShow(new DirSelectionDialog("Select distenation directory", dirSelectBrowser)) { dlg =>
          dlg setOkHandler {
            val destDir = dirSelectBrowser.dirTreeSelection.item.get
            def copyOp(item: File) = if (item.isFile) FileUtils.copyFileToDirectory(item, destDir)
                                     else FileUtils.copyDirectoryToDirectory(item, destDir)

            applyOpToItems(browser.dirContentSelection.items, copyOp, "Unable to copy item %s.")
            browser.reload()
          }
        }
      }
    }

    ui.miMove setCommandHandler {
      if (browser.dirContentSelection.nonEmpty) {
        val dirSelectBrowser = letret(new FileBrowser(isSelectable = false)) { b =>
          b.addPlace("Home", Place(Imcms.getPath))
        }

        app.initAndShow(new DirSelectionDialog("Select distenation directory", dirSelectBrowser)) { dlg =>
          dlg setOkHandler {
            val destDir = dirSelectBrowser.dirTreeSelection.item.get
            def copyOp(item: File) = if (item.isFile) FileUtils.moveDirectoryToDirectory(item, destDir, false)
                                     else FileUtils.moveDirectoryToDirectory(item, destDir, false)

            applyOpToItems(browser.dirContentSelection.items, copyOp, "Unable to move item %s.")
            browser.reload()
          }
        }
      }
    }

    ui.miView setCommandHandler {
      for (item <- browser.dirContentSelection.first /*isViewable(file)*/) {
        app.initAndShow(new OKDialog("Content of %s" format item) with CustomSizeDialog) { dlg =>
          dlg.mainUI = new TextArea("", scala.io.Source.fromFile(item).mkString) with ReadOnly with FullSize
          dlg.setSize((500f, 500f))
        }
      }
    }

    ui.miEdit setCommandHandler {
      for (item <- browser.dirContentSelection.first /*isViewable(file)*/) {
        app.initAndShow(new OkCancelDialog("Edit content of %s" format item) with CustomSizeDialog) { dlg =>
          val textArea = new TextArea("", scala.io.Source.fromFile(item).mkString) with FullSize
          dlg.mainUI = textArea
          dlg.setSize((500f, 500f))

          dlg.setOkHandler {
            FileUtils.writeStringToFile(item, textArea.value)
          }
        }
      }
    }

    ui.miUpload setCommandHandler {
      app.initAndShow(new FileUploadDialog("Upload file")) { dlg =>
        dlg.setOkHandler {
          for {
            UploadedData(_, _, content) <- dlg.upload.data
            dir <- browser.dirTreeSelection.item
            file = new File(dir, dlg.upload.saveAsName)
          } {
            if (file.exists && !dlg.upload.isOverwrite) {
              app.show(new MsgDialog("File allready exists", "Please choose different name or check 'overwrite existing'"))
              error("File %s allready exists" format file.getCanonicalPath)
            } else {
              FileUtils.writeByteArrayToFile(file, content)
              browser.reloadDirContent()
            }
          }
        }
      }
    }

    ui.miDownload setCommandHandler {
      browser.dirContentSelection.first foreach { file =>
        app.getMainWindow.open(
          new FileResource(file, app) {
            override def getStream() = letret(super.getStream) { ds =>
              ds.setParameter("Content-Disposition", """attachment; filename="%s"""" format file.getName)
            }
          }
        )
      }
    }

    ui.miReload setCommandHandler {
      browser.reload()
    }
  }
}


class FileManagerUI(browserUI: FileBrowserUI) extends VerticalLayout with Spacing with FullSize {
  val mb = new MenuBar
  val miReload = mb.addItem("Reload")
  val miView = mb.addItem("View", null)
  val miEdit = mb.addItem("Edit", null)
  val miCopy = mb.addItem("Copy", null)
  val miMove = mb.addItem("Move", null)
  val miDelete = mb.addItem("Delete", null)
  val miDownload = mb.addItem("Download", null)
  val miUpload = mb.addItem("Upload", null)

  addComponents(this, mb, browserUI)
  setExpandRatio(browserUI, 1.0f)
}