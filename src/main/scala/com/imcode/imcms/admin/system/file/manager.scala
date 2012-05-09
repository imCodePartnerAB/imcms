package com.imcode
package imcms
package admin.system.file

import scala.collection.JavaConversions._
import com.vaadin.ui._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin._
import java.io.File
import org.apache.commons.io.FileUtils
import actors.Actor
import scala.concurrent.ops.{spawn}
import com.vaadin.terminal.{UserError, FileResource}

class FileManager(app: ImcmsApplication) {
  val browser = ImcmsFileBrowser.addAllLocations(new FileBrowser(isMultiSelect = true))

  val preview = new FilePreview(browser) |>> { preview =>
    preview.ui.previewUI.setSize(256, 256)
  }

  val ui = new FileManagerUI(browser.ui, preview.ui) |>> { ui =>

    ui.miEditRename setCommandHandler {
      for (LocationSelection(dir, Seq(item)) <- browser.selection; if item.isFile) {
        app.initAndShow(new OkCancelDialog("file.mgr.dlg.rename.item.title".f(item.getName))) { dlg =>
          dlg.btnOk.setCaption("file.mgr.dlg.transfer.item.btn.rename".i)

          val dlgUI = new ItemRenameDialogUI
          dlg.mainUI = dlgUI
          dlgUI.txtName.value = item.getName

          dlg.wrapOkHandler {
            val forbiddenChars = """?"\/:;%*|>>>"""
            val forbiddenCharsSet = forbiddenChars.toSet
            dlgUI.txtName.value.trim |> {
              case name if name.isEmpty || name.head == '.' || name.exists(forbiddenCharsSet(_)) =>
                val msg = "file.mgr.dlg.illegal.item.name.msg".f(forbiddenChars)
                dlgUI.lblMsg.value = msg
                dlgUI.lblMsg.setComponentError(new UserError(msg))
                sys.error(msg)

              case name => new File(dir, name) match {
                case file if file.exists =>
                  val msg = "file.mgr.dlg.transfer.item.exist.msg".f(name, dir.getName)
                  dlgUI.lblMsg.value = msg
                  dlgUI.lblMsg.setComponentError(new UserError(msg))
                  sys.error(msg)

                case file => item.renameTo(file)
              }
            }

            app.showInfoNotification("file.mgr.rename.item.info.msg".i)
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
      for (LocationSelection(_, Seq(item)) <- browser.selection; if item.isFile && FileOps.isShowable(item))
        FileOps.default(app, item)
    }

    ui.miFileEdit setCommandHandler {
      for (LocationSelection(_, Seq(item)) <- browser.selection; if item.isFile) {
        app.initAndShow(new OkCancelDialog("file.edit.dlg.title".f(item)) with CustomSizeDialog, resizable = true) { dlg =>
          dlg.btnOk.setCaption("btn_save".i)

          val textArea = new TextArea("", scala.io.Source.fromFile(item).mkString) with FullSize
          dlg.mainUI = textArea
          dlg.setSize(500, 500)

          dlg.wrapOkHandler {
            FileUtils.writeStringToFile(item, textArea.value)
          }
        }
      }
    }

    ui.miFileUpload setCommandHandler {
      for (LocationSelection(dir, _) <- browser.selection) {
        app.initAndShow(new FileUploaderDialog("file.upload.dlg.title".i)) { dlg =>
          dlg.wrapOkHandler {
            for {
              UploadedFile(_, _, file) <- dlg.uploader.uploadedFile
              destFile = new File(dir, dlg.uploader.saveAsName)
            } {
              if (destFile.exists && !dlg.uploader.isOverwrite) {
                app.show(new MsgDialog("file.mgr.dlg.upload.item.exist.title".i, "file.mgr.dlg.upload.item.exist.msg".i))
                sys.error("File %s allready exists" format destFile.getCanonicalPath)
              } else {
                FileUtils.moveFile(file, destFile)
                browser.reloadLocationItems()
              }
            }
          }
        }
      }
    }

    ui.miFileDownload setCommandHandler {
      for (LocationSelection(_, Seq(item)) <- browser.selection; if item.isFile)
        FileOps.download(app, item)
    }

    ui.miViewReload setCommandHandler { browser.reloadLocation(preserveTreeSelection = true) }

    ui.miViewPreview setCommandHandler { preview.enabled = !preview.enabled }

    ui.miNewDir setCommandHandler {
      for (selection <- browser.selection) {
        app.initAndShow(new OkCancelDialog("file.mgr.dlg.new_dir.title".i)) { dlg =>
          val txtName = new TextField("file.mgr.dlg.new_dir.frm.fld.name".i)
          val lblMsg = new Label with UndefinedSize
          dlg.mainUI = new FormLayout with UndefinedSize {addComponents(this, lblMsg, txtName) }

          // refactor
          val forbiddenChars = """?"\/:;%*|>>>"""
          val forbiddenCharsSet = forbiddenChars.toSet

          dlg.wrapOkHandler {
            txtName.value.trim match {
              case name if name.isEmpty || name.head == '.' || name.exists(forbiddenCharsSet(_))  =>
                val msg = "file.mgr.dlg.illegal.item.name.msg".f(forbiddenChars)
                lblMsg.value = msg
                lblMsg.setComponentError(new UserError(msg))
                sys.error(msg)

              case name =>
                FileUtils.forceMkdir(new File(selection.dir, txtName.value))
                browser.reloadLocation()
            }
          }
        }
      }
    }
  }
}


class FileManagerUI(browserUI: FileBrowserUI, previewUI: FilePreviewUI) extends GridLayout(2, 2) with Spacing with FullSize {
  import Theme.Icons._

  val mb = new MenuBar
  val miFile = mb.addItem("file.mgr.menu.file".i, File16)
  val miFileShow = miFile.addItem("file.mgr.menu.file.show".i)
  val miFileEdit = miFile.addItem("file.mgr.menu.file.edit".i)
  val miFileUpload = miFile.addItem("file.mgr.menu.file.upload".i)
  val miFileDownload = miFile.addItem("file.mgr.menu.file.download".i)
  val miNew = mb.addItem("file.mgr.menu.new".i, New16)
  val miNewDir = miNew.addItem("file.mgr.menu.new.dir".i)
  val miEdit = mb.addItem("file.mgr.menu.edit".i, Edit16)
  val miEditCopy = miEdit.addItem("file.mgr.menu.edit.copy".i)
  val miEditMove = miEdit.addItem("file.mgr.menu.edit.move".i)
  val miEditRename = miEdit.addItem("file.mgr.menu.edit.rename".i)
  val miEditDelete = miEdit.addItem("file.mgr.menu.edit.delete".i)
  val miView = mb.addItem("file.mgr.menu.view".i)
  val miViewReload = miView.addItem("file.mgr.menu.view.reload".i, Reload16)
  val miViewPreview = miView.addItem("file.mgr.menu.view.toggle_preview".i)
  val miHelp = mb.addItem("file.mgr.menu.help".i, Help16)

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
    app.initAndShow(new ConfirmationDialog("file.mgr.dlg.delete.confirm.msg".i)) { dlg =>
      dlg wrapOkHandler { asyncDeleteItems(selection.items) }
    }
  }

  private def asyncDeleteItems(items: Seq[File]) {
    def handleFinished(progressDialog: Dialog, itemsState: ItemsState) = app.synchronized {
      progressDialog.close()

      if (itemsState.processed.isEmpty) {
        app.showWarningNotification("file.mgr.delete.nop.warn.msg".i)
      } else {
        browser.reloadLocation()

        app.show(new InformationDialog("file.mgr.dlg.delete.summary.msg".f(itemsState.processed.size)))
      }
    }
    // no i18n
    def handleUndefined(progressDialog: Dialog, msg: Any) = app.synchronized {
      progressDialog.close()
      app.showErrorNotification("An error occured while deleting items", msg.toString)
    }

    app.initAndShow(new CancelDialog("dlg.progress.title".i)) { dlg =>
      val dlgUI = new ItemsDeletePrgressDialogUI
      dlg.mainUI = dlgUI
      dlgUI.lblMsg.value = "file.mgr.dlg.delete.progress.prepare.msg".i
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
                items.size.asInstanceOf[Float] |> { max =>
                  dlgUI.pi.setValue((max - remaining.size) / max)
                }

                val parentName = Option(item.getParentFile) map (_.getName) getOrElse "."
                dlgUI.lblMsg.value = "file.mgr.dlg.delete.progress.msg".f(item.getName, parentName)
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
        dlgUI.lblMsg.value = "dlg.progress.cancelling.msg".i

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
          app.initAndShow(new OkCancelErrorDialog("file.mgr.dlg.delete.item.err.msg".f(item.getName))) { dlg =>
            dlg.btnOk.setCaption("btn_skip".i)

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

      app.initAndShow(new DirSelectionDialog("file.mgr.dlg.select.copy.dest.title".i, dirSelectBrowser, Seq(selection.dir)), resizable = true) { dlg =>
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
      val dirSelectBrowser = ImcmsFileBrowser.addAllLocations(new FileBrowser(isSelectable = false))

      app.initAndShow(new DirSelectionDialog("file.mgr.dlg.select.move.dest.title", dirSelectBrowser, Seq(selection.dir)), resizable = true) { dlg =>
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
        app.showWarningNotification("file.mgr.copy.nop.warn.msg".i)
      } else {
        app.initAndShow(new ConfirmationDialog("dlg.info.title".i, "file.mgr.dlg.copy.summary.msg".f(itemsState.processed.size, destDir.getName))) { dlg =>
          dlg.wrapOkHandler { browser.select(destLocationRoot, destDir, itemsState.processed) }
        }
      }
    }
    // no i18n
    def handleUndefined(transferDialog: Dialog, msg: Any) = app.synchronized {
      transferDialog.close()
      app.showErrorNotification("An error occured while copying items", msg.toString)
    }

    app.initAndShow(new CancelDialog("dlg.progress.title".i)) { dlg =>
      val dlgUI = new ItemsTransferProgressDialogUI

      dlg.mainUI = dlgUI
      dlgUI.lblMsg.value = "file.mgr.dlg.copy.progress.prepare.msg".i
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
                dlgUI.lblMsg.value = "file.mgr.dlg.copy.progress.msg".f(item.getName, destDir.getName)

                items.size.asInstanceOf[Float] |> { max =>
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
        dlgUI.lblMsg.value = "dlg.progress.cancelling.msg".i

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
      def copyItem(destItemName: String): Unit = new File(destDir, destItemName) |> { destItem =>
        if (!destItem.exists) {
          try {
            if (item.isFile) FileUtils.copyFile(item, destItem)
            else FileUtils.copyDirectory(item, destItem)

            stateHandler ! ItemsState(remaining, destItem +: processed)
          } catch {
            case e => app.synchronized {
              app.initAndShow(new OkCancelErrorDialog("Unable to copy")) { dlg =>
                dlg.btnOk.setCaption("btn_skip".i)
                dlg.mainUI = new Label("file.mgr.dlg.copy.item.err.msg".f(item.getName)) with UndefinedSize

                dlg.wrapOkHandler { stateHandler ! ItemsState(remaining, processed) }
                dlg.wrapCancelHandler { stateHandler ! ItemsState(Nil, processed) }
              }
            }
          }
        } else {
          app.synchronized {
            app.initAndShow(new YesNoCancelDialog("file.mgr.dlg.copy.item.issue.title".i)) { dlg =>
              val dlgUI = new ItemRenameDialogUI |>> { dlgUI =>
                dlgUI.lblMsg.value = "file.mgr.dlg.transfer.item.exist.msg".f(destItemName, destDir.getName)
                dlgUI.txtName.value = destItemName
              }

              dlg.btnYes.setCaption("btn_rename".i)
              dlg.btnNo.setCaption("btn_skip".i)

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
        app.showWarningNotification("file.mgr.move.nop.warn.msg".i)
      } else {
        app.initAndShow(new ConfirmationDialog("dlg.info.title".i, "file.mgr.dlg.move.summary.msg".f(itemsState.processed.size, destDir.getName))) { dlg =>
          dlg.wrapOkHandler { browser.select(destLocationRoot, destDir, itemsState.processed) }
          dlg.wrapCancelHandler { browser.reloadLocation() }
        }
      }
    }
    // no i18n
    def handleUndefined(transferDialog: Dialog, msg: Any) = app.synchronized {
      transferDialog.close()
      app.showErrorNotification("An error occured while moving items", msg.toString)
    }

    app.initAndShow(new CancelDialog("dlg.progress.title".i)) { dlg =>
      val dlgUI = new ItemsTransferProgressDialogUI

      dlg.mainUI = dlgUI
      dlgUI.lblMsg.value = "file.mgr.dlg.move.progress.prepare.msg".i
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
                dlgUI.lblMsg.value = "file.mgr.dlg.move.progress.msg".f(item.getName, destDir.getName)

                items.size.asInstanceOf[Float] |> { max =>
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
        dlgUI.lblMsg.value = "dlg.progress.cancelling.msg".i

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
      def moveItem(destItemName: String): Unit = new File(destDir, destItemName) |> { destItem =>
        if (!destItem.exists) {
          try {
            if (item.isFile) FileUtils.moveFile(item, destItem)
            else FileUtils.moveDirectory(item, destItem)

            stateHandler ! ItemsState(remaining, destItem +: processed)
          } catch {
            case e => app.synchronized {
              app.initAndShow(new OkCancelErrorDialog("file.mgr.dlg.move.item.err.msg".f(item.getName))) { dlg =>
                dlg.wrapOkHandler { stateHandler ! ItemsState(remaining, processed) }
                dlg.wrapCancelHandler { stateHandler ! ItemsState(Nil, processed) }
              }
            }
          }
        } else {
          app.synchronized {
            app.initAndShow(new YesNoCancelDialog("file.mgr.dlg.move.item.issue.title".i)) { dlg =>
              val dlgUI = new ItemRenameDialogUI |>> { dlgUI =>
                dlgUI.lblMsg.value = "file.mgr.dlg.transfer.item.exist.msg".f(destItemName, destDir.getName)
                dlgUI.txtName.value = destItemName
              }

              dlg.btnYes.setCaption("btn_rename".i)
              dlg.btnNo.setCaption("btn_skip".i)

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