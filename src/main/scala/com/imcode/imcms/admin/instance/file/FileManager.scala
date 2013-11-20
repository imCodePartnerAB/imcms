package com.imcode
package imcms
package admin.instance.file

import com.imcode.imcms.vaadin.Current
import scala.collection.JavaConverters._
import com.vaadin.ui._

import java.io.File
import org.apache.commons.io.FileUtils
import actors.Actor
import scala.concurrent.ops.{spawn}
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.server._
import com.imcode.imcms.vaadin.component.dialog._
import com.vaadin.server.{Page, UserError}

class FileManager(app: UI) {
  val browser = ImcmsFileBrowser.addAllLocations(new FileBrowser(isMultiSelect = true))

  val preview = new FilePreview(browser) |>> { preview =>
    preview.widget.previewWidget.setSize(256, 256)
  }

  val widget = new FileManagerWidget(browser.widget, preview.widget) |>> { w =>

    w.miEditRename.setCommandHandler { _ =>
      for (LocationSelection(dir, Seq(item)) <- browser.selection; if item.isFile) {
        new OkCancelDialog("file.mgr.dlg.rename.item.title".f(item.getName)) |>> { dlg =>
          dlg.btnOk.setCaption("file.mgr.dlg.transfer.item.btn.rename".i)

          val dlgMainWidget = new ItemRenameDialogMainWidget
          dlg.mainWidget = dlgMainWidget
          dlgMainWidget.txtName.value = item.getName

          dlg.setOkButtonHandler {
            val forbiddenChars = """?"\/:;%*|>>>"""
            val forbiddenCharsSet = forbiddenChars.toSet
            dlgMainWidget.txtName.value.trim |> {
              case name if name.isEmpty || name.head == '.' || name.exists(forbiddenCharsSet(_)) =>
                val msg = "file.mgr.dlg.illegal.item.name.msg".f(forbiddenChars)
                dlgMainWidget.lblMsg.value = msg
                dlgMainWidget.lblMsg.setComponentError(new UserError(msg))
                sys.error(msg)

              case name => new File(dir, name) match {
                case file if file.exists =>
                  val msg = "file.mgr.dlg.transfer.item.exist.msg".f(name, dir.getName)
                  dlgMainWidget.lblMsg.value = msg
                  dlgMainWidget.lblMsg.setComponentError(new UserError(msg))
                  sys.error(msg)

                case file => item.renameTo(file)
              }
            }

            Current.page.showInfoNotification("file.mgr.rename.item.info.msg".i)
          }
        } |> Current.ui.addWindow
      }
    }

    w.miEditDelete.setCommandHandler { _ =>
      new ItemsDeleteHelper(app, browser) delete()
    }

    w.miEditCopy.setCommandHandler { _ =>
      new ItemsTransferHelper(app, browser) copy()
    }

    w.miEditMove.setCommandHandler { _ =>
      new ItemsTransferHelper(app, browser) move()
    }

    w.miFileShow.setCommandHandler { _ =>
      for (LocationSelection(_, Seq(item)) <- browser.selection; if item.isFile && FileOps.isShowable(item))
        FileOps.default(item)
    }

    w.miFileEdit.setCommandHandler { _ =>
      for (LocationSelection(_, Seq(item)) <- browser.selection; if item.isFile) {
        new OkCancelDialog("file.edit.dlg.title".f(item)) with CustomSizeDialog with Resizable { dlg =>
          dlg.btnOk.setCaption("btn_save".i)

          val textArea = new TextArea("", scala.io.Source.fromFile(item).mkString) with FullSize
          dlg.mainWidget = textArea
          dlg.setSize(500, 500)

          dlg.setOkButtonHandler {
            FileUtils.writeStringToFile(item, textArea.value)
          }
        } |> Current.ui.addWindow
      }
    }

    w.miFileUpload.setCommandHandler { _ =>
      for (LocationSelection(dir, _) <- browser.selection) {
        new FileUploaderDialog("file.upload.dlg.title".i) |>> { dlg =>
          dlg.setOkButtonHandler {
            for {
              UploadedFile(_, _, file) <- dlg.uploader.uploadedFile
              destFile = new File(dir, dlg.uploader.saveAsName)
            } {
              if (destFile.exists && !dlg.uploader.mayOverwrite) {
                new MsgDialog("file.mgr.dlg.upload.item.exist.title".i, "file.mgr.dlg.upload.item.exist.msg".i) |> Current.ui.addWindow
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
        new OkCancelDialog("file.mgr.dlg.new_dir.title".i) |>> { dlg =>
          val txtName = new TextField("file.mgr.dlg.new_dir.frm.fld.name".i)
          val lblMsg = new Label with UndefinedSize
          dlg.mainWidget = new FormLayout with UndefinedSize { this.addComponents(lblMsg, txtName) }

          // refactor
          val forbiddenChars = """?"\/:;%*|>>>"""
          val forbiddenCharsSet = forbiddenChars.toSet

          dlg.setOkButtonHandler {
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
        } |> Current.ui.addWindow
      }
    }
  }
}


class FileManagerWidget(browserUI: FileBrowserWidget, previewWidget: FilePreviewWidget) extends GridLayout(2, 2) with Spacing with FullSize {
  import Theme.Icon._

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
  this.addComponents(browserUI, previewWidget)
  setComponentAlignment(previewWidget, Alignment.MIDDLE_CENTER)
  setColumnExpandRatio(0, 1f)
  setRowExpandRatio(1, 1f)
}


// helpers - AsyncItemsHandlers

case class ItemsState(remaining: Seq[File], processed: Seq[File])


class ItemsDeleteHelper(app: UI, browser: FileBrowser) {

  def delete() = for (selection <- browser.selection if selection.hasItems) {
    new ConfirmationDialog("file.mgr.dlg.delete.confirm.msg".i) |>> { dlg =>
      dlg.setOkButtonHandler { asyncDeleteItems(selection.items) }
    } |> Current.ui.addWindow
  }

  private def asyncDeleteItems(items: Seq[File]) {
    def handleFinished(progressDialog: Dialog, itemsState: ItemsState) = app.withSessionLock {
      progressDialog.close()

      if (itemsState.processed.isEmpty) {
        Current.page.showWarningNotification("file.mgr.delete.nop.warn.msg".i)
      } else {
        browser.reloadLocation()

        new InformationDialog("file.mgr.dlg.delete.summary.msg".f(itemsState.processed.size)) |> Current.ui.addWindow
      }
    }
    // no i18n
    def handleUndefined(progressDialog: Dialog, msg: Any) = app.withSessionLock {
      progressDialog.close()
      Current.page.showErrorNotification("An error occured while deleting items", msg.toString)
    }

    new CancelDialog("dlg.progress.title".i) |>> { dlg =>
      val dialogMainWidget = new ItemsDeleteProgressDialogMainWidget
      dlg.mainWidget = dialogMainWidget
      dialogMainWidget.lblMsg.value = "file.mgr.dlg.delete.progress.prepare.msg".i
      dialogMainWidget.pi.setPollingInterval(500)

      object DeleteActor extends Actor {
        def act() {
          react {
            case itemsState @ ItemsState(Nil, _) =>
              app.withSessionLock {
                dialogMainWidget.pi.setValue(1)
              }

              handleFinished(dlg, itemsState)

            case itemsState @ ItemsState(remaining @ (item :: _), _) =>
              app.withSessionLock {
                items.size.asInstanceOf[Float] |> { max =>
                  dialogMainWidget.pi.setValue((max - remaining.size) / max)
                }

                val parentName = item.getParentFile.asOption.map(_.getName).getOrElse(".")
                dialogMainWidget.lblMsg.value = "file.mgr.dlg.delete.progress.msg".f(item.getName, parentName)
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
        case e: Exception => app.withSessionLock {
          new OkCancelErrorDialog("file.mgr.dlg.delete.item.err.msg".f(item.getName)) |>> { dlg =>
            dlg.btnOk.setCaption("btn_skip".i)

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
class ItemsTransferHelper(app: UI, browser: FileBrowser) {

  def copy() {
    // refactor into dest dir selection method??
    for (selection <- browser.selection if selection.hasItems) {
      val dirSelectBrowser = ImcmsFileBrowser.addAllLocations(new FileBrowser(isSelectable = false))

      new DirSelectionDialog("file.mgr.dlg.select.copy.dest.title".i, dirSelectBrowser, Seq(selection.dir)) with Resizable |>>  { dlg =>
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

      new DirSelectionDialog("file.mgr.dlg.select.move.dest.title", dirSelectBrowser, Seq(selection.dir)) with Resizable |>> { dlg =>
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

    def handleFinished(transferDialog: Dialog, itemsState: ItemsState) = app.withSessionLock {
      transferDialog.close()

      if (itemsState.processed.isEmpty) {
        Current.page.showWarningNotification("file.mgr.copy.nop.warn.msg".i)
      } else {
        new ConfirmationDialog("dlg.info.title".i, "file.mgr.dlg.copy.summary.msg".f(itemsState.processed.size, destDir.getName)) |>> { dlg =>
          dlg.setOkButtonHandler { browser.select(destLocationRoot, destDir, itemsState.processed) }
        } |> Current.ui.addWindow
      }
    }
    // no i18n
    def handleUndefined(transferDialog: Dialog, msg: Any) = app.withSessionLock {
      transferDialog.close()
      Current.page.showErrorNotification("An error occured while copying items", msg.toString)
    }

    new CancelDialog("dlg.progress.title".i) |>> { dlg =>
      val dialogMainWidget = new ItemsTransferProgressDialogMainWidget

      dlg.mainWidget = dialogMainWidget
      dialogMainWidget.lblMsg.value = "file.mgr.dlg.copy.progress.prepare.msg".i
      dialogMainWidget.pi.setPollingInterval(500)

      object CopyActor extends Actor {
        def act() {
          react {
            case itemsState @ ItemsState(Nil, _) =>
              app.withSessionLock {
                dialogMainWidget.pi.setValue(1)
              }

              handleFinished(dlg, itemsState)

            case itemsState @ ItemsState(remaining @ (item :: _), _) =>
              app.withSessionLock {
                dialogMainWidget.lblMsg.value = "file.mgr.dlg.copy.progress.msg".f(item.getName, destDir.getName)

                items.size.asInstanceOf[Float] |> { max =>
                  dialogMainWidget.pi.setValue((max - remaining.size) / max)
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
        dialogMainWidget.lblMsg.value = "dlg.progress.cancelling.msg".i

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
            case e: Exception => app.withSessionLock {
              new OkCancelErrorDialog("Unable to copy") |>> { dlg =>
                dlg.btnOk.setCaption("btn_skip".i)
                dlg.mainWidget = new Label("file.mgr.dlg.copy.item.err.msg".f(item.getName)) with UndefinedSize

                dlg.setOkButtonHandler { stateHandler ! ItemsState(remaining, processed) }
                dlg.setCancelButtonHandler { stateHandler ! ItemsState(Nil, processed) }
              } |> Current.ui.addWindow
            }
          }
        } else {
          app.withSessionLock {
            new YesNoCancelDialog("file.mgr.dlg.copy.item.issue.title".i) |>> { dlg =>
              val dialogMainWidget = new ItemRenameDialogMainWidget |>> { mw =>
                mw.lblMsg.value = "file.mgr.dlg.transfer.item.exist.msg".f(destItemName, destDir.getName)
                mw.txtName.value = destItemName
              }

              dlg.btnYes.setCaption("btn_rename".i)
              dlg.btnNo.setCaption("btn_skip".i)

              dlg.mainWidget = dialogMainWidget
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

    def handleFinished(transferDialog: Dialog, itemsState: ItemsState) = app.withSessionLock {
      transferDialog.close()

      if (itemsState.processed.isEmpty) {
        Current.page.showWarningNotification("file.mgr.move.nop.warn.msg".i)
      } else {
        new ConfirmationDialog("dlg.info.title".i, "file.mgr.dlg.move.summary.msg".f(itemsState.processed.size, destDir.getName)) |>> { dlg =>
          dlg.setOkButtonHandler { browser.select(destLocationRoot, destDir, itemsState.processed) }
          dlg.setCancelButtonHandler { browser.reloadLocation() }
        } |> Current.ui.addWindow
      }
    }
    // no i18n
    def handleUndefined(transferDialog: Dialog, msg: Any) = app.withSessionLock {
      transferDialog.close()
      Current.page.showErrorNotification("An error occured while moving items", msg.toString)
    }

    new CancelDialog("dlg.progress.title".i) |>> { dlg =>
      val dialogMainWidget = new ItemsTransferProgressDialogMainWidget

      dlg.mainWidget = dialogMainWidget
      dialogMainWidget.lblMsg.value = "file.mgr.dlg.move.progress.prepare.msg".i
      dialogMainWidget.pi.setPollingInterval(500)

      object MoveActor extends Actor {
        def act() {
          react {
            case itemsState @ ItemsState(Nil, _) =>
              app.withSessionLock {
                dialogMainWidget.pi.setValue(1)
              }

              handleFinished(dlg, itemsState)

            case itemsState @ ItemsState(remaining @ (item :: _), _) =>
              app.withSessionLock {
                dialogMainWidget.lblMsg.value = "file.mgr.dlg.move.progress.msg".f(item.getName, destDir.getName)

                items.size.asInstanceOf[Float] |> { max =>
                  dialogMainWidget.pi.setValue((max - remaining.size) / max)
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
            case e: Exception => app.withSessionLock {
              new OkCancelErrorDialog("file.mgr.dlg.move.item.err.msg".f(item.getName)) |>> { dlg =>
                dlg.setOkButtonHandler { stateHandler ! ItemsState(remaining, processed) }
                dlg.setCancelButtonHandler { stateHandler ! ItemsState(Nil, processed) }
              } |> Current.ui.addWindow
            }
          }
        } else {
          app.withSessionLock {
            new YesNoCancelDialog("file.mgr.dlg.move.item.issue.title".i) |>> { dlg =>
              val dialogMainWidget = new ItemRenameDialogMainWidget |>> { w =>
                w.lblMsg.value = "file.mgr.dlg.transfer.item.exist.msg".f(destItemName, destDir.getName)
                w.txtName.value = destItemName
              }

              dlg.btnYes.setCaption("btn_rename".i)
              dlg.btnNo.setCaption("btn_skip".i)

              dlg.mainWidget = dialogMainWidget
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


class ItemsDeleteProgressDialogMainWidget extends FormLayout with Spacing with UndefinedSize {
  val lblMsg = new Label with UndefinedSize
  val pi = new ProgressIndicator

  this.addComponents(lblMsg, pi)
}


class ItemsTransferProgressDialogMainWidget extends FormLayout with Spacing with UndefinedSize {
  val lblMsg = new Label with UndefinedSize
  val pi = new ProgressIndicator

  this.addComponents(lblMsg, pi)
}


class ItemRenameDialogMainWidget extends FormLayout with Spacing with UndefinedSize {
  val lblMsg = new Label with UndefinedSize
  val txtName = new TextField("file.mgr.dlg.transfer.item.frm.fld.name".i)

  this.addComponents(lblMsg, txtName)
}