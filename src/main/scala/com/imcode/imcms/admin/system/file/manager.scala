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
      browser.dirContentSelection.first foreach { file =>
      }
    }

    ui.miEdit setCommandHandler {
      browser.dirContentSelection.first foreach { file =>
      }
    }

    ui.miUpload setCommandHandler {
    }

    ui.miDownload setCommandHandler {
      browser.dirContentSelection.first foreach { file =>
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
  val miUpload = mb.addItem("Uploa, null)d", null)

  addComponents(this, mb, browserUI)
  setExpandRatio(browserUI, 1.0f)
}