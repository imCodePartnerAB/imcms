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
import annotation.tailrec
import actors.Actor

class FileManager(app: ImcmsApplication) {
  val browser = letret(new FileBrowser(isMultiSelect = true)) { browser =>
    browser.addLocation("Home", LocationConf(Imcms.getPath))
    browser.addLocation("Templates", LocationConf(new File(Imcms.getPath, "WEB-INF/templates/text")))
    browser.addLocation("Images", LocationConf(new File(Imcms.getPath, "images")))
    browser.addLocation("Conf", LocationConf(new File(Imcms.getPath, "WEB-INF/conf")))
    browser.addLocation("Logs", LocationConf(new File(Imcms.getPath, "WEB-INF/logs")))
  }

  val ui = letret(new FileManagerUI(browser.ui)) { ui =>

    /**
     * Recursively applies op to an item.
     * @param opFailMsg - fail message with unbound format parameter substitutable with fsNode - ex. "Unable to copy %s."
     */
    def applyOpToItems(items: Seq[File], op: File => Unit, opFailMsg: String, afterFn: () => Any = () => ()) {
      items match {
        case item :: rest =>
          def applyOpToRestItems() = applyOpToItems(rest, op, opFailMsg, afterFn)
          def applyOpToEmptyItems() = applyOpToItems(Nil, op, opFailMsg, afterFn)

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

        case _ =>
          browser.reloadLocationTree(preserveTreeSelection = true)
          afterFn()
      }
    }

    def promptCd(root: File, dir: File) = () => {
      app.initAndShow(new OKDialog("Done")) { dlg =>
        dlg.btnOk.addClickHandler {
          browser.select(root, dir)
        }
      }
    }

    ui.miEditDelete setCommandHandler {
      for (selection <- browser.selection if selection.hasItems) {
        app.initAndShow(new ConfirmationDialog("Delete selected items")) { dlg =>
          dlg setOkHandler {
            applyOpToItems(selection.items, FileUtils.forceDelete, "Unable to delete item %s.")
          }
        }
      }
    }

    ui.miEditCopy setCommandHandler {
//      for (selection <- browser.selection if selection.nonEmpty) {
//        val dirSelectBrowser = letret(new FileBrowser(isSelectable = false)) { b =>
//          b.addLocation("Home", LocationConf(Imcms.getPath))
//        }
//
//        app.initAndShow(new DirSelectionDialog("Select distination directory", dirSelectBrowser)) { dlg =>
//          dlg setOkHandler {
//            for (destSelection <- dirSelectBrowser.selection; destDir = destSelection.dir) {
//              //copy dialog/progress ???
//              def op(item: File) = if (item.isFile) FileUtils.copyFileToDirectory(item, destDir)
//                                   else FileUtils.copyDirectoryToDirectory(item, destDir)
//
//              val afterFn = promptCd(dirSelectBrowser.location.get._1.root, destDir)
//              applyOpToItems(selection.items, op, "Unable to copy item %s.", afterFn)
//            }
//          }
//        }
//      }
      new ItemsTransfer(app, browser) copy
    }

    ui.miEditMove setCommandHandler {
//      for (selection <- browser.selection if selection.hasItems) {
//        val dirSelectBrowser = letret(new FileBrowser(isSelectable = false)) { b =>
//          b.addLocation("Home", LocationConf(Imcms.getPath))
//        }
//
//        app.initAndShow(new DirSelectionDialog("Select distenation directory", dirSelectBrowser)) { dlg =>
//          dlg setOkHandler {
//            for (destSelection <- dirSelectBrowser.selection; destDir = destSelection.dir) {
//              def op(item: File) = if (item.isFile) FileUtils.moveFileToDirectory(item, destDir, false)
//                                   else FileUtils.moveDirectoryToDirectory(item, destDir, false)
//
//              applyOpToItems(selection.items, op, "Unable to move item %s.")
//            }
//          }
//        }
//      }
    }

    ui.miFilePreview setCommandHandler {
      for (selection <- browser.selection; item <- selection.firstItem /*isViewable(item)*/) {
        app.initAndShow(new OKDialog("Content of %s" format item) with CustomSizeDialog, resizable = true) { dlg =>
          dlg.mainUI = new TextArea("", scala.io.Source.fromFile(item).mkString) with ReadOnly with FullSize
          dlg.setSize((500, 500))
        }
      }
    }

    ui.miFileEdit setCommandHandler {
      for (selection <- browser.selection; item <- selection.firstItem /*isEditable(item)*/) {
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
      for (selection <- browser.selection; item <- selection.firstItem if item.isFile) {
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
      browser.reloadLocationTree(preserveTreeSelection = true)
    }

    ui.miNewDir setCommandHandler {
      for (selection <- browser.selection) {
        app.initAndShow(new OkCancelDialog("New directory")) { dlg =>
          val txtName = new TextField("Name")
          dlg.mainUI = txtName
          dlg.setOkHandler {
            FileUtils.forceMkdir(new File(selection.dir, txtName.value))
            browser.reloadLocationTree()
          }
        }
      }
    }
  }

  browser.listen { e =>
    ui.lblDirTreePath.value = e match {
      case Some(LocationSelection(dir, _)) => dir.getCanonicalPath
      case _ => ""
    }
  }

  browser.notifyListeners()
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

  val lblDirTreePath = new Label

  addComponents(this, mb, browserUI, lblDirTreePath)
  setExpandRatio(browserUI, 1.0f)
}



class ItemsTransfer(app: ImcmsApplication, browser: FileBrowser) {

  case class TransferState(remaining: Seq[File], processed: Seq[File])

  def copy() {
    def copy(actor: Actor, destDir: File, transferState: TransferState) = transferState match {
      case TransferState(item :: remaining, processed) =>
        def copyItem(destItemName: String): Unit = let(new File(destDir, destItemName)) { destItem =>
          if (!destItem.exists) {
            // try/catch - handle error
            if (item.isFile) FileUtils.copyFile(item, destItem)
            else FileUtils.copyDirectory(item, destItem)

            actor ! (('process, TransferState(remaining, destItem +: processed)))
            // try/catch - handle error
          } else {
            app.initAndShow(new YesNoCancelDialog("Item with name %s allready exists")) { dlg =>
              val dlgMainUI = letret(new ItemRenameDialogUI) { ui =>
                ui.lblMsg.value = "Please provide different name"
              }

              dlg.mainUI = dlgMainUI
              dlg.setYesHandler { copyItem(dlgMainUI.txtName.value) }
              dlg.setNoHandler { actor ! (('process, TransferState(remaining, processed))) }
              dlg.setCancelHandler { actor ! (('process, TransferState(Nil, processed))) }
            }
          }
        }

        copyItem(item.getName)

      case _ =>
        actor ! ('process, transferState)
    }


    def copyItems(destLocationRoot: File, destDir: File, items: Seq[File]) {

      def finish(transferDialog: Dialog, transferState: TransferState) = app.synchronized {
        transferDialog.close()

        if (transferState.processed.isEmpty) {
          app.showWarningNotification("No items where copied")
        } else {
          app.initAndShow(new ConfirmationDialog("Finished", "%d items where copied. Would you like to preview")) { dlg =>
            dlg.setOkHandler { browser.select(destLocationRoot, destDir, transferState.processed) }
          }
        }
      }

      def handleUndefinedEvent(transferDialog: Dialog, event: Any) = app.synchronized {
        transferDialog.close()
        app.showErrorNotification("An error occured while copying items", event.toString)
      }


      app.initAndShow(new OKDialog("Copying files into %s" format destDir)) { dlg =>
        val dialogUI = new ItemsTransferDialogUI

        dlg.mainUI = dialogUI
        dlg.btnOk.setCaption("Cancel")
        dialogUI.lblMsg.value = "Preparing to copy"

        object CopyActor extends Actor {

          def act() {
            react {
              case ('process, transferState @ TransferState(Nil, _)) =>
                finish(dlg, transferState)

              case ('process, transferState @ TransferState(item :: _, _)) =>
                app.synchronized {
                  dialogUI.lblMsg.value = "Copying " + item.getName
                }

                Actor.actor {
                  Thread.sleep(5000)
                  copy(CopyActor, destDir, transferState)
                }

                act()

              case 'cancel =>
                react {
                  case (_, transferState: TransferState) => finish(dlg, transferState)
                  case undefined => handleUndefinedEvent(dlg, undefined)
                }

              case undefined => handleUndefinedEvent(dlg, undefined)
            }
          }
        }

        dlg.btnOk.addClickHandler {
          app.synchronized {
            dlg.btnOk.setEnabled(false)
            dialogUI.lblMsg.value = "Cancelling"
          }

          CopyActor ! 'cancel
        }

        CopyActor.start()
        CopyActor ! (('process, TransferState(items, Nil)))
      }
    }

    // refactor into dest dir selection method
    for (selection <- browser.selection if selection.hasItems) {
      val dirSelectBrowser = letret(new FileBrowser(isSelectable = false)) { dsb =>
        dsb.addLocation("Home", LocationConf(Imcms.getPath))
      }

      app.initAndShow(new DirSelectionDialog("Select distination directory", dirSelectBrowser, Seq(selection.dir))) { dlg =>
        dlg setOkHandler {
          for {
            destLocation <- dirSelectBrowser.location
            destSelection <- dirSelectBrowser.selection
          } copyItems(destLocation._1.root, destSelection.dir, selection.items)
        }
      }
    }

  }
}


/** File/Dir Copy/Move UI */
class ItemsTransferDialogUI extends FormLayout with Spacing with UndefinedSize {
  val lblMsg = new Label
  val pi = new ProgressIndicator

  addComponents(this, lblMsg, pi)
}


class ItemRenameDialogUI extends FormLayout with Spacing with UndefinedSize {
  val lblMsg = new Label
  val txtName = new TextField("Name")

  addComponents(this, lblMsg, txtName)
}