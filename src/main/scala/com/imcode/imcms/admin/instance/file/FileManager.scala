package com.imcode
package imcms
package admin.instance.file

import com.imcode.imcms.vaadin.Current
import com.vaadin.ui._

import java.io.File
import org.apache.commons.io.FileUtils
import scala.actors.Actor
import com.imcode.util.Threads.spawn
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.server._
import com.imcode.imcms.vaadin.component.dialog._
import com.vaadin.server.UserError

class FileManager {
  val browser = ImcmsFileBrowser.addAllLocations(new FileBrowser(isMultiSelect = true))

  val preview = new FilePreview(browser) |>> { preview =>
    preview.view.previewComponent.setSize(256, 256)
  }

  val view = new FileManagerView(browser.view, preview.view) |>> { w =>

    w.miEditRename.setCommandHandler { _ =>
      for (LocationSelection(dir, Seq(item)) <- browser.selection; if item.isFile) {
        new OkCancelDialog("file_mgr_dlg.rename.item.title".f(item.getName)) |>> { dlg =>
          dlg.btnOk.setCaption("file_mgr_dlg.transfer.item.btn.rename".i)

          val dlgMainComponent = new ItemRenameDialogView
          dlg.mainComponent = dlgMainComponent
          dlgMainComponent.txtName.value = item.getName

          dlg.setOkButtonHandler {
            val forbiddenChars = """?"\/:;%*|>>>"""
            val forbiddenCharsSet = forbiddenChars.toSet
            dlgMainComponent.txtName.value.trim |> {
              case name if name.isEmpty || name.head == '.' || name.exists(forbiddenCharsSet(_)) =>
                val msg = "file_mgr_dlg.illegal.item.name.msg".f(forbiddenChars)
                dlgMainComponent.lblMsg.value = msg
                dlgMainComponent.lblMsg.setComponentError(new UserError(msg))
                sys.error(msg)

              case name => new File(dir, name) match {
                case file if file.exists =>
                  val msg = "file_mgr_dlg.transfer.item.exist.msg".f(name, dir.getName)
                  dlgMainComponent.lblMsg.value = msg
                  dlgMainComponent.lblMsg.setComponentError(new UserError(msg))
                  sys.error(msg)

                case file => item.renameTo(file)
              }
            }

            Current.page.showInfoNotification("file_mgr.rename.item.info.msg".i)
          }
        } |> Current.ui.addWindow
      }
    }

    w.miEditDelete.setCommandHandler { _ =>
      new ItemsDeleteHelper(browser) delete()
    }

    w.miEditCopy.setCommandHandler { _ =>
      new ItemsTransferHelper(browser) copy()
    }

    w.miEditMove.setCommandHandler { _ =>
      new ItemsTransferHelper(browser) move()
    }

    w.miFileShow.setCommandHandler { _ =>
      for (LocationSelection(_, Seq(item)) <- browser.selection; if item.isFile && FileOps.isShowable(item))
        FileOps.default(item)
    }

    w.miFileEdit.setCommandHandler { _ =>
      for (LocationSelection(_, Seq(item)) <- browser.selection; if item.isFile) {
        new OkCancelDialog("file.edit.dlg.title".f(item)) with CustomSizeDialog with Resizable with OKCaptionIsSave |>> { dlg =>
          val textArea = new TextArea("", scala.io.Source.fromFile(item).mkString) with FullSize
          dlg.mainComponent = textArea
          dlg.setSize(500, 500)

          dlg.setOkButtonHandler {
            FileUtils.writeStringToFile(item, textArea.value)
          }
        } |> Current.ui.addWindow
      }
    }

    w.miFileUpload.setCommandHandler { _ =>
      for (LocationSelection(dir, _) <- browser.selection) {
        new FileUploaderDialog("file_upload_dlg.title".i) |>> { dlg =>
          dlg.setOkButtonHandler {
            for {
              UploadedFile(_, _, file) <- dlg.uploader.uploadedFile
              destFile = new File(dir, dlg.uploader.saveAsName)
            } {
              if (destFile.exists && !dlg.uploader.mayOverwrite) {
                new MsgDialog("file_mgr_dlg.upload.item.exist.title".i, "file_mgr_dlg.upload.item.exist.msg".i).show()
                sys.error("File %s allready exists" format destFile.getCanonicalPath)
              } else {
                FileUtils.moveFile(file, destFile)
                browser.reloadLocationItems()
              }
            }
          }
        } |> Current.ui.addWindow
      }
    }

    w.miFileDownload.setCommandHandler { _ =>
      for (LocationSelection(_, Seq(item)) <- browser.selection if item.isFile)
        FileOps.download(item)
    }

    w.miViewReload.setCommandHandler { _ => browser.reloadLocation(preserveTreeSelection = true) }

    w.miViewPreview.setCommandHandler { _ => preview.enabled = !preview.enabled }

    w.miNewDir.setCommandHandler { _ =>
      for (selection <- browser.selection) {
        new OkCancelDialog("file_mgr_dlg.new_dir.title".i) |>> { dlg =>
          val txtName = new TextField("file_mgr_dlg.new_dir.frm.fld.name".i)
          val lblMsg = new Label with UndefinedSize
          dlg.mainComponent = new FormLayout with UndefinedSize { addComponents(lblMsg, txtName) }

          // refactor
          val forbiddenChars = """?"\/:;%*|>>>"""
          val forbiddenCharsSet = forbiddenChars.toSet

          dlg.setOkButtonHandler {
            txtName.value.trim match {
              case name if name.isEmpty || name.head == '.' || name.exists(forbiddenCharsSet(_))  =>
                val msg = "file_mgr_dlg.illegal.item.name.msg".f(forbiddenChars)
                lblMsg.value = msg
                lblMsg.setComponentError(new UserError(msg))
                sys.error(msg)

              case name =>
                FileUtils.forceMkdir(new File(selection.dir, txtName.value))
                browser.reloadLocation()
            }
          }
        } |> Current.ui.addWindow
      }
    }
  }
}


// helpers - AsyncItemsHandlers

case class ItemsState(remaining: Seq[File], processed: Seq[File])


class ItemsDeleteHelper(browser: FileBrowser) {

  def delete() = for (selection <- browser.selection if selection.hasItems) {
    new ConfirmationDialog("file_mgr_dlg.delete.confirm.msg".i) |>> { dlg =>
      dlg.setOkButtonHandler { asyncDeleteItems(selection.items) }
    } |> Current.ui.addWindow
  }

  private def asyncDeleteItems(items: Seq[File]) {
    def handleFinished(progressDialog: Dialog, itemsState: ItemsState) = Current.ui.withSessionLock {
      progressDialog.close()

      if (itemsState.processed.isEmpty) {
        Current.page.showWarningNotification("file_mgr.delete.nop.warn.msg".i)
      } else {
        browser.reloadLocation()

        new InformationDialog("file_mgr_dlg.delete.summary.msg".f(itemsState.processed.size)) |> Current.ui.addWindow
      }
    }
    // no i18n
    def handleUndefined(progressDialog: Dialog, msg: Any) = Current.ui.withSessionLock {
      progressDialog.close()
      Current.page.showErrorNotification("An error occured while deleting items", msg.toString)
    }

    new CancelDialog("dlg_title.progress".i) |>> { dlg =>
      val dialogMainWidget = new ItemsDeleteProgressDialogView
      dlg.mainComponent = dialogMainWidget
      dialogMainWidget.lblMsg.value = "file_mgr_dlg.delete.progress.prepare.msg".i
      dialogMainWidget.pi.setPollingInterval(500)

      object DeleteActor extends Actor {
        def act() {
          react {
            case itemsState @ ItemsState(Nil, _) =>
              Current.ui.withSessionLock {
                dialogMainWidget.pi.value = 1
              }

              handleFinished(dlg, itemsState)

            case itemsState @ ItemsState(remaining @ (item :: _), _) =>
              Current.ui.withSessionLock {
                items.size.asInstanceOf[Float] |> { max =>
                  dialogMainWidget.pi.value = (max - remaining.size) / max
                }

                val parentName = item.getParentFile.asOption.map(_.getName).getOrElse(".")
                dialogMainWidget.lblMsg.value = "file_mgr_dlg.delete.progress.msg".f(item.getName, parentName)
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

      dlg.setCancelButtonHandler {
        dlg.btnCancel.setEnabled(false)
        dialogMainWidget.lblMsg.value = "dlg.progress.cancelling.msg".i

        DeleteActor ! 'cancel
      }

      DeleteActor ! ItemsState(items, Nil)
      DeleteActor.start()
    } |> Current.ui.addWindow
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
        case e: Exception => Current.ui.withSessionLock {
          new OkCancelErrorDialog("file_mgr_dlg.delete.item.err.msg".f(item.getName)) |>> { dlg =>
            dlg.btnOk.setCaption("btn_caption.skip".i)

            dlg.setOkButtonHandler { stateHandler ! ItemsState(remaining, processed) }
            dlg.setCancelButtonHandler { stateHandler ! ItemsState(Nil, processed) }
          } |> Current.ui.addWindow
        }
      }

    case _ =>
      stateHandler ! itemsState
  }
}


// todo: refactor - merge duplicated code
class ItemsTransferHelper(browser: FileBrowser) {

  def copy() {
    // refactor into dest dir selection method??
    for (selection <- browser.selection if selection.hasItems) {
      val dirSelectBrowser = ImcmsFileBrowser.addAllLocations(new FileBrowser(isSelectable = false))

      new DirSelectionDialog("file_mgr_dlg.select.copy.dest.title".i, dirSelectBrowser, Seq(selection.dir)) with Resizable |>>  { dlg =>
        dlg.setOkButtonHandler {
          for {
            destLocation <- dirSelectBrowser.location
            destSelection <- dirSelectBrowser.selection
          } asyncCopyItems(destLocation._1.root, destSelection.dir, selection.items)
        }
      } |> Current.ui.addWindow
    }
  }


  def move() {
    // refactor into dest dir selection method??
    for (selection <- browser.selection if selection.hasItems) {
      val dirSelectBrowser = ImcmsFileBrowser.addAllLocations(new FileBrowser(isSelectable = false))

      new DirSelectionDialog("file_mgr_dlg.select.move.dest.title", dirSelectBrowser, Seq(selection.dir)) with Resizable |>> { dlg =>
        dlg.setOkButtonHandler {
          for {
            destLocation <- dirSelectBrowser.location
            destSelection <- dirSelectBrowser.selection
          } asyncMoveItems(destLocation._1.root, destSelection.dir, selection.items)
        }
      } |> Current.ui.addWindow
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

    def handleFinished(transferDialog: Dialog, itemsState: ItemsState) = Current.ui.withSessionLock {
      transferDialog.close()

      if (itemsState.processed.isEmpty) {
        Current.page.showWarningNotification("file_mgr.copy.nop.warn.msg".i)
      } else {
        new ConfirmationDialog("dlg_title.info".i, "file_mgr_dlg.copy.summary.msg".f(itemsState.processed.size, destDir.getName)) |>> { dlg =>
          dlg.setOkButtonHandler { browser.select(destLocationRoot, destDir, itemsState.processed) }
        } |> Current.ui.addWindow
      }
    }
    // no i18n
    def handleUndefined(transferDialog: Dialog, msg: Any) = Current.ui.withSessionLock {
      transferDialog.close()
      Current.page.showErrorNotification("An error occured while copying items", msg.toString)
    }

    new CancelDialog("dlg_title.progress".i) |>> { dlg =>
      val dialogMainComponent = new ItemsTransferProgressDialogView

      dlg.mainComponent = dialogMainComponent
      dialogMainComponent.lblMsg.value = "file_mgr_dlg.copy.progress.prepare.msg".i
      dialogMainComponent.pi.setPollingInterval(500)

      object CopyActor extends Actor {
        def act() {
          react {
            case itemsState @ ItemsState(Nil, _) =>
              Current.ui.withSessionLock {
                dialogMainComponent.pi.value = 1
              }

              handleFinished(dlg, itemsState)

            case itemsState @ ItemsState(remaining @ (item :: _), _) =>
              Current.ui.withSessionLock {
                dialogMainComponent.lblMsg.value = "file_mgr_dlg.copy.progress.msg".f(item.getName, destDir.getName)

                items.size.asInstanceOf[Float] |> { max =>
                  dialogMainComponent.pi.value = (max - remaining.size) / max
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

      dlg.setCancelButtonHandler {
        dlg.btnCancel.setEnabled(false)
        dialogMainComponent.lblMsg.value = "dlg.progress.cancelling.msg".i

        CopyActor ! 'cancel
      }

      CopyActor ! ItemsState(items, Nil)
      CopyActor.start()
    } |> Current.ui.addWindow
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
            case e: Exception => Current.ui.withSessionLock {
              new OkCancelErrorDialog("Unable to copy") |>> { dlg =>
                dlg.btnOk.setCaption("btn_caption.skip".i)
                dlg.mainComponent = new Label("file_mgr_dlg.copy.item.err.msg".f(item.getName)) with UndefinedSize

                dlg.setOkButtonHandler { stateHandler ! ItemsState(remaining, processed) }
                dlg.setCancelButtonHandler { stateHandler ! ItemsState(Nil, processed) }
              } |> Current.ui.addWindow
            }
          }
        } else {
          Current.ui.withSessionLock {
            new YesNoCancelDialog("file_mgr_dlg.copy.item.issue.title".i) |>> { dlg =>
              val dialogMainWidget = new ItemRenameDialogView |>> { mw =>
                mw.lblMsg.value = "file_mgr_dlg.transfer.item.exist.msg".f(destItemName, destDir.getName)
                mw.txtName.value = destItemName
              }

              dlg.btnYes.setCaption("btn_caption.rename".i)
              dlg.btnNo.setCaption("btn_caption.skip".i)

              dlg.mainComponent = dialogMainWidget
              dlg.setYesButtonHandler { copyItem(dialogMainWidget.txtName.value) }
              dlg.setNoButtonHandler { stateHandler ! ItemsState(remaining, processed) }
              dlg.setCancelButtonHandler { stateHandler ! ItemsState(Nil, processed) }
            } |> Current.ui.addWindow
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

    def handleFinished(transferDialog: Dialog, itemsState: ItemsState) = Current.ui.withSessionLock {
      transferDialog.close()

      if (itemsState.processed.isEmpty) {
        Current.page.showWarningNotification("file_mgr.move.nop.warn.msg".i)
      } else {
        new ConfirmationDialog("dlg_title.info".i, "file_mgr_dlg.move.summary.msg".f(itemsState.processed.size, destDir.getName)) |>> { dlg =>
          dlg.setOkButtonHandler { browser.select(destLocationRoot, destDir, itemsState.processed) }
          dlg.setCancelButtonHandler { browser.reloadLocation() }
        } |> Current.ui.addWindow
      }
    }
    // no i18n
    def handleUndefined(transferDialog: Dialog, msg: Any) = Current.ui.withSessionLock {
      transferDialog.close()
      Current.page.showErrorNotification("An error occured while moving items", msg.toString)
    }

    new CancelDialog("dlg_title.progress".i) |>> { dlg =>
      val dialogMainWidget = new ItemsTransferProgressDialogView

      dlg.mainComponent = dialogMainWidget
      dialogMainWidget.lblMsg.value = "file_mgr_dlg.move.progress.prepare.msg".i
      dialogMainWidget.pi.setPollingInterval(500)

      object MoveActor extends Actor {
        def act() {
          react {
            case itemsState @ ItemsState(Nil, _) =>
              Current.ui.withSessionLock {
                dialogMainWidget.pi.value = 1
              }

              handleFinished(dlg, itemsState)

            case itemsState @ ItemsState(remaining @ (item :: _), _) =>
              Current.ui.withSessionLock {
                dialogMainWidget.lblMsg.value = "file_mgr_dlg.move.progress.msg".f(item.getName, destDir.getName)

                items.size.asInstanceOf[Float] |> { max =>
                  dialogMainWidget.pi.value = (max - remaining.size) / max
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

      dlg.setCancelButtonHandler {
        dlg.btnCancel.setEnabled(false)
        dialogMainWidget.lblMsg.value = "dlg.progress.cancelling.msg".i

        MoveActor ! 'cancel
      }

      MoveActor ! ItemsState(items, Nil)
      MoveActor.start()
    } |> Current.ui.addWindow
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
            case e: Exception => Current.ui.withSessionLock {
              new OkCancelErrorDialog("file_mgr_dlg.move.item.err.msg".f(item.getName)) |>> { dlg =>
                dlg.setOkButtonHandler { stateHandler ! ItemsState(remaining, processed) }
                dlg.setCancelButtonHandler { stateHandler ! ItemsState(Nil, processed) }
              } |> Current.ui.addWindow
            }
          }
        } else {
          Current.ui.withSessionLock {
            new YesNoCancelDialog("file_mgr_dlg.move.item.issue.title".i) |>> { dlg =>
              val dialogMainWidget = new ItemRenameDialogView |>> { w =>
                w.lblMsg.value = "file_mgr_dlg.transfer.item.exist.msg".f(destItemName, destDir.getName)
                w.txtName.value = destItemName
              }

              dlg.btnYes.setCaption("btn_caption.rename".i)
              dlg.btnNo.setCaption("btn_caption.skip".i)

              dlg.mainComponent = dialogMainWidget
              dlg.setYesButtonHandler { moveItem(dialogMainWidget.txtName.value) }
              dlg.setNoButtonHandler { stateHandler ! ItemsState(remaining, processed) }
              dlg.setCancelButtonHandler { stateHandler ! ItemsState(Nil, processed) }
            } |> Current.ui.addWindow
          }
        }
      }

      moveItem(item.getName)

    case _ =>
      stateHandler ! itemsState
  }

}


class ItemsDeleteProgressDialogView extends FormLayout with Spacing with UndefinedSize {
  val lblMsg = new Label with UndefinedSize
  val pi = new ProgressIndicator

  addComponents(lblMsg, pi)
}


class ItemsTransferProgressDialogView extends FormLayout with Spacing with UndefinedSize {
  val lblMsg = new Label with UndefinedSize
  val pi = new ProgressIndicator

  addComponents(lblMsg, pi)
}


class ItemRenameDialogView extends FormLayout with Spacing with UndefinedSize {
  val lblMsg = new Label with UndefinedSize
  val txtName = new TextField("file_mgr_dlg.transfer.item.frm.fld.name".i)

  addComponents(lblMsg, txtName)
}