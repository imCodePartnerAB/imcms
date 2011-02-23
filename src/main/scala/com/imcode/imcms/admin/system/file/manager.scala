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
              dlg.wrapOkHandler { applyOpToRestItems() }
              dlg.wrapCancelHandler { applyOpToEmptyItems() }
            }
          }

        case _ =>
          browser.reloadLocation(preserveTreeSelection = true)
          afterFn()
      }
    }

    ui.miEditRename setCommandHandler {
      for (selection <- browser.selection if selection.hasSingleItem) {
        // rename
      }
    }

    ui.miEditDelete setCommandHandler {
      for (selection <- browser.selection if selection.hasItems) {
        app.initAndShow(new ConfirmationDialog("Delete selected items")) { dlg =>
          dlg wrapOkHandler {
            applyOpToItems(selection.items, FileUtils.forceDelete, "Unable to delete item %s.")
          }
        }
      }
    }

    ui.miEditCopy setCommandHandler {
      new ItemsTransfer(app, browser) copy()
    }

    ui.miEditMove setCommandHandler {
      new ItemsTransfer(app, browser) move()
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

          dlg.wrapOkHandler {
            FileUtils.writeStringToFile(item, textArea.value)
          }
        }
      }
    }

    ui.miFileUpload setCommandHandler {
      for (selection <- browser.selection; dir = selection.dir) {
        app.initAndShow(new FileUploadDialog("Upload file")) { dlg =>
          dlg.wrapOkHandler {
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
      browser.reloadLocation(preserveTreeSelection = true)
    }

    ui.miNewDir setCommandHandler {
      for (selection <- browser.selection) {
        app.initAndShow(new OkCancelDialog("New directory")) { dlg =>
          val txtName = new TextField("Name")
          dlg.mainUI = txtName
          dlg.wrapOkHandler {
            FileUtils.forceMkdir(new File(selection.dir, txtName.value))
            browser.reloadLocation()
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
  val miEditRename = miEdit.addItem("Rename")
  val miEditDelete = miEdit.addItem("Delete")
  val miView = mb.addItem("View")
  val miViewReload = miView.addItem("Reload")
  val miHelp = mb.addItem("Help")

  val lblDirTreePath = new Label

  addComponents(this, mb, browserUI, lblDirTreePath)
  setExpandRatio(browserUI, 1.0f)
}


// ItemsTransferHelper
// todo: refactor duplicated code code
class ItemsTransfer(app: ImcmsApplication, browser: FileBrowser) {

  // transfer state
  case class ItemsState(remaining: Seq[File], processed: Seq[File])

  def copy() {
    // refactor into dest dir selection method??
    for (selection <- browser.selection if selection.hasItems) {
      val dirSelectBrowser = letret(new FileBrowser(isSelectable = false)) { dsb =>
        dsb.addLocation("Home", LocationConf(Imcms.getPath))
      }

      app.initAndShow(new DirSelectionDialog("Copy to", dirSelectBrowser, Seq(selection.dir))) { dlg =>
        dlg wrapOkHandler {
          for {
            destLocation <- dirSelectBrowser.location
            destSelection <- dirSelectBrowser.selection
          } asyncCopyItems(destLocation._1.root, destSelection.dir, selection.items)
        }
      }
    }
  }


  def move() {
    // refactor into dest dir selection method??
    for (selection <- browser.selection if selection.hasItems) {
      val dirSelectBrowser = letret(new FileBrowser(isSelectable = false)) { dsb =>
        dsb.addLocation("Home", LocationConf(Imcms.getPath))
      }

      app.initAndShow(new DirSelectionDialog("Move to", dirSelectBrowser, Seq(selection.dir))) { dlg =>
        dlg wrapOkHandler {
          for {
            destLocation <- dirSelectBrowser.location
            destSelection <- dirSelectBrowser.selection
          } asyncMoveItems(destLocation._1.root, destSelection.dir, selection.items)
        }
      }
    }
  }


  /**
   * Asynchronously copies selected browser items into destination directory.
   * If at least one item was copied prompts user to select copied items in destination directory.
   *
   * Client-side is updated using progress indicator pooling feature - this means that
   * all UI updates must be synchronized against Application.
   */
  private def asyncCopyItems(destLocationRoot: File, destDir: File, items: Seq[File]) {

    def handleFinished(transferDialog: Dialog, itemsState: ItemsState) = app.synchronized {
      transferDialog.close()

      if (itemsState.processed.isEmpty) {
        app.showWarningNotification("No items where copied")
      } else {
        app.initAndShow(new ConfirmationDialog("Finished", "%d items where copied. Would you like to preview" format itemsState.processed.size)) { dlg =>
          dlg.wrapOkHandler { browser.select(destLocationRoot, destDir, itemsState.processed) }
        }
      }
    }

    def handleUndefined(transferDialog: Dialog, msg: Any) = app.synchronized {
      transferDialog.close()
      app.showErrorNotification("An error occured while copying items", msg.toString)
    }

    app.initAndShow(new CancelDialog("Copying items into .../%s" format destDir)) { dlg =>
      val dlgUI = new ItemsTransferDialogUI

      dlg.mainUI = dlgUI
      dlgUI.lblMsg.value = "Preparing to copy"

      object CopyActor extends Actor {
        def act() {
          react {
            case itemsState @ ItemsState(Nil, _) => handleFinished(dlg, itemsState)

            case itemsState @ ItemsState(item :: _, _) =>
              app.synchronized {
                dlgUI.lblMsg.value = "Copying " + item.getName
              }

              Actor.actor {
                Thread.sleep(5000)
                asyncCopyItem(CopyActor, destDir, itemsState)
              }

              act()

            case 'cancel =>
              react {
                case itemsState: ItemsState => handleFinished(dlg, itemsState)
                case undefined => handleUndefined(dlg, undefined)
              }

            case undefined => handleUndefined(dlg, undefined)
          }
        }
      }

      dlg.btnCancel.addClickHandler {
        app.synchronized {
          dlg.btnCancel.setEnabled(false)
          dlgUI.lblMsg.value = "Cancelling"
        }

        CopyActor ! 'cancel
      }

      CopyActor ! ItemsState(items, Nil)
      CopyActor.start()
    }
  }

  /**
   * Attempts to copy fist of the remaining items into the destination dir.
   * Updates transfer state and send it to the actor.
   *
   * Client-side is updated using progress indicator pooling feature - this means that
   * all UI updates must be synchronized against Application.
   */
  private def asyncCopyItem(stateHandler: Actor, destDir: File, itemsState: ItemsState) = itemsState match {
    case ItemsState(item :: remaining, processed) =>
      // allows item renaming
      def copyItem(destItemName: String): Unit = let(new File(destDir, destItemName)) { destItem =>
        if (!destItem.exists) {
          try {
            if (item.isFile) FileUtils.copyFile(item, destItem)
            else FileUtils.copyDirectory(item, destItem)

            stateHandler ! ItemsState(remaining, destItem +: processed)
          } catch {
            case e => app.synchronized {
              app.initAndShow(new OkCancelDialog("Unable to copy")) { dlg =>
                dlg.btnOk.setCaption("Skip")
                dlg.mainUI = new Label("An error occured while coping item %s." format item.getName) with UndefinedSize

                dlg.wrapOkHandler { stateHandler ! ItemsState(remaining, processed) }
                dlg.wrapCancelHandler { stateHandler ! ItemsState(Nil, processed) }
              }
            }
          }
        } else {
          app.synchronized {
            app.initAndShow(new YesNoCancelDialog("Unable to copy")) { dlg =>
              val dlgUI = letret(new ItemRenameDialogUI) { dlgUI =>
                dlgUI.lblMsg.value = "Item %s allready exists in .../%s".format(destItemName, destDir.getName)
                dlgUI.txtName.value = destItemName
              }

              dlg.btnYes.setCaption("Rename")
              dlg.btnNo.setCaption("Skip")

              dlg.mainUI = dlgUI
              dlg.wrapYesHandler { copyItem(dlgUI.txtName.value) }
              dlg.wrapNoHandler { stateHandler ! ItemsState(remaining, processed) }
              dlg.wrapCancelHandler { stateHandler ! ItemsState(Nil, processed) }
            }
          }
        }
      }

      copyItem(item.getName)

    case _ =>
      stateHandler ! itemsState
  }

  /**
   * Asynchronously moves selected browser items into destination directory.
   * If at least one item was copied prompts user to select copied items in destination directory.
   *
   * Client-side is updated using progress indicator pooling feature - this means that
   * all UI updates must be synchronized against Application.
   */
  private def asyncMoveItems(destLocationRoot: File, destDir: File, items: Seq[File]) {

    def handleFinished(transferDialog: Dialog, itemsState: ItemsState) = app.synchronized {
      transferDialog.close()

      if (itemsState.processed.isEmpty) {
        app.showWarningNotification("No items where moved")
      } else {
        app.initAndShow(new ConfirmationDialog("Finished", "%d items where moved. Would you like to preview" format itemsState.processed.size)) { dlg =>
          dlg.wrapOkHandler { browser.select(destLocationRoot, destDir, itemsState.processed) }
        }
      }
    }

    def handleUndefined(transferDialog: Dialog, msg: Any) = app.synchronized {
      transferDialog.close()
      app.showErrorNotification("An error occured while moving items", msg.toString)
    }

    app.initAndShow(new CancelDialog("Moving items into .../%s" format destDir)) { dlg =>
      val dlgUI = new ItemsTransferDialogUI

      dlg.mainUI = dlgUI
      dlgUI.lblMsg.value = "Preparing to move"

      object MoveActor extends Actor {
        def act() {
          react {
            case itemsState @ ItemsState(Nil, _) => handleFinished(dlg, itemsState)

            case itemsState @ ItemsState(item :: _, _) =>
              app.synchronized {
                dlgUI.lblMsg.value = "Moving " + item.getName
              }

              Actor.actor {
                Thread.sleep(5000)
                asyncMoveItem(MoveActor, destDir, itemsState)
              }

              act()

            case 'cancel =>
              react {
                case itemsState: ItemsState => handleFinished(dlg, itemsState)
                case undefined => handleUndefined(dlg, undefined)
              }

            case undefined => handleUndefined(dlg, undefined)
          }
        }
      }

      dlg.btnCancel.addClickHandler {
        app.synchronized {
          dlg.btnCancel.setEnabled(false)
          dlgUI.lblMsg.value = "Cancelling"
        }

        MoveActor ! 'cancel
      }

      MoveActor ! ItemsState(items, Nil)
      MoveActor.start()
    }
  }

  /**
   * Attempts to move fist of the remaining items into the destination dir.
   * Updates transfer state and send it to the actor.
   *
   * Client-side is updated using progress indicator pooling feature - this means that
   * all UI updates must be synchronized against Application.
   */
  private def asyncMoveItem(stateHandler: Actor, destDir: File, itemsState: ItemsState) = itemsState match {
    case ItemsState(item :: remaining, processed) =>
      // allows item renaming
      def moveItem(destItemName: String): Unit = let(new File(destDir, destItemName)) { destItem =>
        if (!destItem.exists) {
          try {
            if (item.isFile) FileUtils.moveFile(item, destItem)
            else FileUtils.moveDirectory(item, destItem)

            stateHandler ! ItemsState(remaining, destItem +: processed)
          } catch {
            case e => app.synchronized {
              app.initAndShow(new OkCancelDialog("Unable to move")) { dlg =>
                dlg.btnOk.setCaption("Skip")
                dlg.mainUI = new Label("An error occured while moving item %s." format item.getName) with UndefinedSize

                dlg.wrapOkHandler { stateHandler ! ItemsState(remaining, processed) }
                dlg.wrapCancelHandler { stateHandler ! ItemsState(Nil, processed) }
              }
            }
          }
        } else {
          app.synchronized {
            app.initAndShow(new YesNoCancelDialog("Unable to move")) { dlg =>
              val dlgUI = letret(new ItemRenameDialogUI) { dlgUI =>
                dlgUI.lblMsg.value = "Item %s allready exists in .../%s".format(destItemName, destDir.getName)
                dlgUI.txtName.value = destItemName
              }

              dlg.btnYes.setCaption("Rename")
              dlg.btnNo.setCaption("Skip")

              dlg.mainUI = dlgUI
              dlg.wrapYesHandler { moveItem(dlgUI.txtName.value) }
              dlg.wrapNoHandler { stateHandler ! ItemsState(remaining, processed) }
              dlg.wrapCancelHandler { stateHandler ! ItemsState(Nil, processed) }
            }
          }
        }
      }

      moveItem(item.getName)

    case _ =>
      stateHandler ! itemsState
  }

}


/** File/Dir Copy/Move UI */
class ItemsTransferDialogUI extends FormLayout with Spacing with UndefinedSize {
  val lblMsg = new Label with UndefinedSize
  val pi = new ProgressIndicator

  addComponents(this, lblMsg, pi)
}


class ItemRenameDialogUI extends FormLayout with Spacing with UndefinedSize {
  val lblMsg = new Label with UndefinedSize
  val txtName = new TextField("Name")

  addComponents(this, lblMsg, txtName)
}