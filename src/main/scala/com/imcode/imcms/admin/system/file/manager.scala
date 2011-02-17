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
    def applyOpToFSNodes(op: File => Unit, opFailMsg: String, fsNodes: Seq[File] = browser.dirContentSelection.fsNodes) {
      fsNodes match {
        case fsNode :: rest =>
          def applyOpToRestFSNodes() = applyOpToFSNodes(op, opFailMsg, rest)
          def applyOpToEmptyFSNodes() = applyOpToFSNodes(op, opFailMsg, Nil)

          try {
            op(fsNode)
            applyOpToRestFSNodes()
          } catch {
            case _ => app.initAndShow(new ConfirmationDialog(opFailMsg format fsNode)) { dlg =>
              dlg.btnOk.setCaption("Skip")
              dlg.setOkHandler { applyOpToRestFSNodes() }
              dlg.setCancelHandler { applyOpToEmptyFSNodes() }
            }
          }

        case _ => browser.reloadDirTree(preserveDirTreeSelection = true)
      }
    }

    ui.miEditDelete setCommandHandler {
      if (browser.dirContentSelection.nonEmpty) {
        app.initAndShow(new ConfirmationDialog("Delete selected items")) { dlg =>
          dlg setOkHandler {
            applyOpToFSNodes(FileUtils.forceDelete, "Unable to delete item %s.")
          }
        }
      }
    }

    ui.miEditCopy setCommandHandler {
      if (browser.dirContentSelection.nonEmpty) {
        val dirSelectBrowser = letret(new FileBrowser(isSelectable = false)) { b =>
          b.addPlace("Home", Place(Imcms.getPath))
        }

        app.initAndShow(new DirSelectionDialog("Select distenation directory", dirSelectBrowser)) { dlg =>
          dlg setOkHandler {
            val destDir = dirSelectBrowser.dirTreeSelection.dir.get
            def copyOp(fsNode: File) = if (fsNode.isFile) FileUtils.copyFileToDirectory(fsNode, destDir)
                                       else FileUtils.copyDirectoryToDirectory(fsNode, destDir)

            applyOpToFSNodes(copyOp, "Unable to copy item %s.")
          }
        }
      }
    }

    ui.miEditMove setCommandHandler {
      if (browser.dirContentSelection.nonEmpty) {
        val dirSelectBrowser = letret(new FileBrowser(isSelectable = false)) { b =>
          b.addPlace("Home", Place(Imcms.getPath))
        }

        app.initAndShow(new DirSelectionDialog("Select distenation directory", dirSelectBrowser), resizable = true) { dlg =>
          dlg setOkHandler {
            val destDir = dirSelectBrowser.dirTreeSelection.dir.get
            def copyOp(fsNode: File) = if (fsNode.isFile) FileUtils.moveDirectoryToDirectory(fsNode, destDir, false)
                                       else FileUtils.moveDirectoryToDirectory(fsNode, destDir, false)

            applyOpToFSNodes(copyOp, "Unable to move item %s.")
          }
        }
      }
    }

    ui.miFilePreview setCommandHandler {
      for (item <- browser.dirContentSelection.first /*isViewable(file)*/) {
        app.initAndShow(new OKDialog("Content of %s" format item) with CustomSizeDialog, resizable = true) { dlg =>
          dlg.mainUI = new TextArea("", scala.io.Source.fromFile(item).mkString) with ReadOnly with FullSize
          dlg.setSize((500, 500))
        }
      }
    }

    ui.miFileEdit setCommandHandler {
      for (item <- browser.dirContentSelection.first /*isViewable(file)*/) {
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
      app.initAndShow(new FileUploadDialog("Upload file")) { dlg =>
        dlg.setOkHandler {
          for {
            UploadedData(_, _, content) <- dlg.upload.data
            dir <- browser.dirTreeSelection.dir
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

    ui.miFileDownload setCommandHandler {
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

    ui.miViewReload setCommandHandler {
      browser.reloadDirTree()
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