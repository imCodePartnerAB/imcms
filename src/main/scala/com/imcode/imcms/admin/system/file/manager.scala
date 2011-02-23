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
      new ItemsTransfer(app, browser) copy
    }

    ui.miEditMove setCommandHandler {
      new ItemsTransfer(app, browser) copy
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
  val miEditDelete = miEdit.addItem("Delete")
  val miView = mb.addItem("View")
  val miViewReload = miView.addItem("Reload")
  val miHelp = mb.addItem("Help")

  val lblDirTreePath = new Label

  addComponents(this, mb, browserUI, lblDirTreePath)
  setExpandRatio(browserUI, 1.0f)
}



class ItemsTransfer(app: ImcmsApplication, browser: FileBrowser) {

  case class Transfer(remaining: Seq[File], processed: Seq[File])

  def copy() {
    def copy(actor: Actor, destDir: File, transferState: Transfer) = transferState match {
      case Transfer(item :: remaining, processed) =>
        def copyItem(destItemName: String): Unit = let(new File(destDir, destItemName)) { destItem =>
          if (!destItem.exists) {
            // try/catch - handle error
            if (item.isFile) FileUtils.copyFile(item, destItem)
            else FileUtils.copyDirectory(item, destItem)

            actor ! Transfer(remaining, destItem +: processed)
            // try/catch - handle error
          } else {
            app.initAndShow(new YesNoCancelDialog("Item with name %s allready exists" format destItemName)) { dlg =>
              val dlgUI = letret(new ItemRenameDialogUI) { dlgUI =>
                dlgUI.lblMsg.value = "Please provide different name"
                dlgUI.txtName.value = destItemName
              }

              dlg.mainUI = dlgUI
              dlg.wrapYesHandler { copyItem(dlgUI.txtName.value) }
              dlg.wrapNoHandler { actor ! Transfer(remaining, processed) }
              dlg.setCancelHandler { actor ! Transfer(Nil, processed) }
            }
          }
        }

        copyItem(item.getName)

      case _ =>
        actor ! transferState
    }


    /**
     * Asynchronously copies selected browser items into destination dir.
     * Client-side is periodically updated using progress indicator pooling feature - this means that
     * all UI updates must be synchronized against Application.
     */
    def asyncCopyItems(destLocationRoot: File, destDir: File, items: Seq[File]) {

      def finish(transferDialog: Dialog, transferState: Transfer) = app.synchronized {
        transferDialog.close()

        if (transferState.processed.isEmpty) {
          app.showWarningNotification("No items where copied")
        } else {
          app.initAndShow(new ConfirmationDialog("Finished", "%d items where copied. Would you like to preview" format transferState.processed.size)) { dlg =>
            dlg.wrapOkHandler { browser.select(destLocationRoot, destDir, transferState.processed) }
          }
        }
      }

      def handleUndefinedEvent(transferDialog: Dialog, event: Any) = app.synchronized {
        transferDialog.close()
        app.showErrorNotification("An error occured while copying items", event.toString)
      }


      app.initAndShow(new CancelDialog("Copying files into %s" format destDir)) { dlg =>
        val dlgUI = new ItemsTransferDialogUI

        dlg.mainUI = dlgUI
        dlgUI.lblMsg.value = "Preparing to copy"

        object CopyActor extends Actor {

          def act() {
            react {
              case transferState @ Transfer(Nil, _) => finish(dlg, transferState)

              case transferState @ Transfer(item :: _, _) =>
                app.synchronized {
                  dlgUI.lblMsg.value = "Copying " + item.getName
                }

                Actor.actor {
                  Thread.sleep(5000)
                  copy(CopyActor, destDir, transferState)
                }

                act()

              case 'cancel =>
                react {
                  case transferState: Transfer => finish(dlg, transferState)
                  case undefined => handleUndefinedEvent(dlg, undefined)
                }

              case undefined => handleUndefinedEvent(dlg, undefined)
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

        CopyActor ! TransferState(items, Nil)
        CopyActor.start()
      }
    }

    // refactor into dest dir selection method??
    for (selection <- browser.selection if selection.hasItems) {
      val dirSelectBrowser = letret(new FileBrowser(isSelectable = false)) { dsb =>
        dsb.addLocation("Home", LocationConf(Imcms.getPath))
      }

      app.initAndShow(new DirSelectionDialog("Select distination directory", dirSelectBrowser, Seq(selection.dir))) { dlg =>
        dlg wrapOkHandler {
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