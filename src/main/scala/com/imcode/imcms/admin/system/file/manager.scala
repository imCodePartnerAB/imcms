package com.imcode
package imcms.admin.system.file

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin._
import java.io.File
import org.apache.commons.io.FileUtils
import com.vaadin.terminal.FileResource
import actors.Actor
import scala.concurrent.ops.{spawn}


class FileManager(app: ImcmsApplication) {
  val browser = ImcmsFileBrowser.addAllLocations(new FileBrowser(isMultiSelect = true))

  val preview = letret(new FilePreview(browser)) { preview =>
    preview.ui.previewUI.setSize((256, 256))
  }

  val ui = letret(new FileManagerUI(browser.ui, preview.ui)) { ui =>

    ui.miEditRename setCommandHandler {
      for ((dir, Seq(item)) <- browser.selection; if item.isFile) {
        app.initAndShow(new OkCancelDialog("file.mgr.dlg.rename.item.title".i)) { dlg =>
          val dlgUI = new ItemRenameDialogUI
          dlg.mainUI = dlgUI
          dlgUI.txtName.value = item.getName

          dlg.wrapOkHandler {
            val forbiddenChars = """?"\/:;%*|<>""".toSet
            let(dlgUI.txtName.value.trim) {
              case name if name.isEmpty || name.head == '.' || name.exists(forbiddenChars(_)) =>
                val msg = "file.mgr.dlg.transfer.illegal.item.name.msg".i
                dlgUI.txtName.value = msg
                error(msg)

              case name => new File(dir, name) match {
                case file if file.exists =>
                  val msg = "file.mgr.dlg.transfer.item.exist.msg".f(name, dir)
                  dlgUI.txtName.value = msg
                  error(msg)

                case file => item.renameTo(file)
              }
            }
          }
        }
      }
    }

    ui.miEditDelete setCommandHandler {
      new ItemsDeleteHelper(app, browser) delete()
    }

    ui.miEditCopy setCommandHandler {
      new ItemsTransferHelper(app, browser) copy()
    }

    ui.miEditMove setCommandHandler {
      new ItemsTransferHelper(app, browser) move()
    }

    ui.miFileShow setCommandHandler {
      for ((_, Seq(item)) <- browser.selection; if item.isFile)
        FileOps.show(app, item)
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
      for ((_, Seq(item)) <- browser.selection; if item.isFile)
        FileOps.download(app, item)
    }

    ui.miViewReload setCommandHandler { browser.reloadLocation(preserveTreeSelection = true) }

    ui.miViewPreview setCommandHandler { preview.enabled = !preview.enabled }

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
}


class FileManagerUI(browserUI: FileBrowserUI, previewUI: FilePreviewUI) extends GridLayout(2, 2) with Spacing with FullSize {
  val mb = new MenuBar
  val miFile = mb.addItem("file.mgr.menu.file".i)
  val miFileShow = miFile.addItem("file.mgr.menu.file.show".i)
  val miFileEdit = miFile.addItem("file.mgr.menu.file.edit".i)
  val miFileUpload = miFile.addItem("file.mgr.menu.file.upload".i)
  val miFileDownload = miFile.addItem("file.mgr.menu.file.download".i)
  val miNew = mb.addItem("file.mgr.menu.new".i)
  val miNewDir = miNew.addItem("file.mgr.menu.new.dir".i)
  val miEdit = mb.addItem("file.mgr.menu.edit")
  val miEditCopy = miEdit.addItem("file.mgr.menu.edit.copy")
  val miEditMove = miEdit.addItem("file.mgr.menu.edit.move")
  val miEditRename = miEdit.addItem("file.mgr.menu.edit.rename")
  val miEditDelete = miEdit.addItem("file.mgr.menu.edit.delete")
  val miView = mb.addItem("file.mgr.menu.view")
  val miViewReload = miView.addItem("file.mgr.menu.view.reload")
  val miViewPreview = miView.addItem("file.mgr.menu.view.toggle_preview")
  val miHelp = mb.addItem("file.mgr.menu.help")

  addComponent(mb, 0, 0, 1, 0)
  addComponents(this, browserUI, previewUI)
  setComponentAlignment(previewUI, Alignment.MIDDLE_CENTER)
  setColumnExpandRatio(0, 1f)
  setRowExpandRatio(1, 1f)
}


// helpers - AsyncItemsHandlers

case class ItemsState(remaining: Seq[File], processed: Seq[File])


class ItemsDeleteHelper(app: ImcmsApplication, browser: FileBrowser) {

  def delete() = for (selection <- browser.selection if selection.hasItems) {
    app.initAndShow(new ConfirmationDialog("Delete selected items?")) { dlg =>
      dlg wrapOkHandler { asyncDeleteItems(selection.items) }
    }
  }

  private def asyncDeleteItems(items: Seq[File]) {
    def handleFinished(progressDialog: Dialog, itemsState: ItemsState) = app.synchronized {
      progressDialog.close()

      if (itemsState.processed.isEmpty) {
        app.showWarningNotification("No items where deleted")
      } else {
        browser.reloadLocation()

        app.show(new MsgDialog("Finished", "Deleted %d item(s)." format itemsState.processed.size))
      }
    }

    def handleUndefined(progressDialog: Dialog, msg: Any) = app.synchronized {
      progressDialog.close()
      app.showErrorNotification("An error occured while deleting items", msg.toString)
    }

    app.initAndShow(new CancelDialog("Deleting items")) { dlg =>
      val dlgUI = new ItemsDeletePrgressDialogUI
      dlg.mainUI = dlgUI
      dlgUI.lblMsg.value = "Preparing to delete"
      dlgUI.pi.setPollingInterval(500)

      object DeleteActor extends Actor {
        def act() {
          react {
            case itemsState @ ItemsState(Nil, _) =>
              app.synchronized {
                dlgUI.pi.setValue(1)
              }

              handleFinished(dlg, itemsState)

            case itemsState @ ItemsState(remaining @ (item :: _), _) =>
              app.synchronized {
                let (items.size.asInstanceOf[Float]) { max =>
                  dlgUI.pi.setValue((max - remaining.size) / max)
                }

                dlgUI.lblMsg.value = "Deleting " + item.getName
              }

              spawn {
                Thread.sleep(2000)
                asyncDeleteItem(DeleteActor, itemsState)
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

      dlg.setCancelHandler {
        dlg.btnCancel.setEnabled(false)
        dlgUI.lblMsg.value = "Cancelling"

        DeleteActor ! 'cancel
      }

      DeleteActor ! ItemsState(items, Nil)
      DeleteActor.start()
    }
  }

  /**
   * Attempts to delete fist of the remaining items into the destination dir.
   * Updates transfer state and send it to the actor.
   *
   * Client-side is updated using progress indicator pooling feature - this means that
   * all UI updates must be synchronized against Application.
   */
  private def asyncDeleteItem(stateHandler: Actor, itemsState: ItemsState) = itemsState match {
    case ItemsState(item :: remaining, processed) =>
      try {
        FileUtils.forceDelete(item)
        stateHandler ! ItemsState(remaining, item +: processed)
      } catch {
        case e => app.synchronized {
          app.initAndShow(new OkCancelDialog("Unable to delete")) { dlg =>
            dlg.btnOk.setCaption("Skip")
            dlg.mainUI = new Label("An error occured while deleting item %s." format item.getName) with UndefinedSize

            dlg.wrapOkHandler { stateHandler ! ItemsState(remaining, processed) }
            dlg.wrapCancelHandler { stateHandler ! ItemsState(Nil, processed) }
          }
        }
      }

    case _ =>
      stateHandler ! itemsState
  }
}


// todo: refactor - merge duplicated code
class ItemsTransferHelper(app: ImcmsApplication, browser: FileBrowser) {

  def copy() {
    // refactor into dest dir selection method??
    for (selection <- browser.selection if selection.hasItems) {
      val dirSelectBrowser = ImcmsFileBrowser.addAllLocations(new FileBrowser(isSelectable = false))

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
      val dlgUI = new ItemsTransferProgressDialogUI

      dlg.mainUI = dlgUI
      dlgUI.lblMsg.value = "Preparing to copy"
      dlgUI.pi.setPollingInterval(500)

      object CopyActor extends Actor {
        def act() {
          react {
            case itemsState @ ItemsState(Nil, _) =>
              app.synchronized {
                dlgUI.pi.setValue(1)
              }

              handleFinished(dlg, itemsState)

            case itemsState @ ItemsState(remaining @ (item :: _), _) =>
              app.synchronized {
                dlgUI.lblMsg.value = "Copying " + item.getName

                let (items.size.asInstanceOf[Float]) { max =>
                  dlgUI.pi.setValue((max - remaining.size) / max)
                }
              }

              spawn {
                Thread.sleep(2000)
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

      dlg.setCancelHandler {
        dlg.btnCancel.setEnabled(false)
        dlgUI.lblMsg.value = "Cancelling"

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
          dlg.wrapCancelHandler { browser.reloadLocation() }
        }
      }
    }

    def handleUndefined(transferDialog: Dialog, msg: Any) = app.synchronized {
      transferDialog.close()
      app.showErrorNotification("An error occured while moving items", msg.toString)
    }

    app.initAndShow(new CancelDialog("Moving items into .../%s" format destDir)) { dlg =>
      val dlgUI = new ItemsTransferProgressDialogUI

      dlg.mainUI = dlgUI
      dlgUI.lblMsg.value = "Preparing to move"
      dlgUI.pi.setPollingInterval(500)

      object MoveActor extends Actor {
        def act() {
          react {
            case itemsState @ ItemsState(Nil, _) =>
              app.synchronized {
                dlgUI.pi.setValue(1)
              }

              handleFinished(dlg, itemsState)

            case itemsState @ ItemsState(remaining @ (item :: _), _) =>
              app.synchronized {
                dlgUI.lblMsg.value = "Moving " + item.getName

                let (items.size.asInstanceOf[Float]) { max =>
                  dlgUI.pi.setValue((max - remaining.size) / max)
                }
              }

              spawn {
                Thread.sleep(2000)
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

      dlg.setCancelHandler {
        dlg.btnCancel.setEnabled(false)
        dlgUI.lblMsg.value = "Cancelling"

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


class ItemsDeletePrgressDialogUI extends FormLayout with Spacing with UndefinedSize {
  val lblMsg = new Label with UndefinedSize
  val pi = new ProgressIndicator

  addComponents(this, lblMsg, pi)
}


class ItemsTransferProgressDialogUI extends FormLayout with Spacing with UndefinedSize {
  val lblMsg = new Label with UndefinedSize
  val pi = new ProgressIndicator

  addComponents(this, lblMsg, pi)
}


class ItemRenameDialogUI extends FormLayout with Spacing with UndefinedSize {
  val lblMsg = new Label with UndefinedSize
  val txtName = new TextField("file.mgr.dlg.transfer.item.frm.fld.name".i)

  addComponents(this, lblMsg, txtName)
}