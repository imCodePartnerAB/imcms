package com.imcode
package imcms
package admin.instance.file

import com.vaadin.ui._
import com.imcode.util.event.Publisher
import java.io._
import org.apache.commons.io.FileUtils
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._


case class UploadedFile(name: String, mimeType: String, file: File)


sealed trait UploadStatus
case object UploadReseted extends UploadStatus
case class UploadStarted(event: Upload.StartedEvent) extends UploadStatus
case class UploadProgressUpdated(readBytes: Long, contentLength: Long) extends UploadStatus
case class UploadSucceeded(event: Upload.SucceededEvent, uploadedFile: UploadedFile) extends UploadStatus
case class UploadFailed(event: Upload.FailedEvent) extends UploadStatus


/**
 * Client code should implement save logic in setOkButtonHandler.
 *
 * @param caption
 */
class FileUploaderDialog(caption: String = "") extends OkCancelDialog(caption) {
  val uploader = new FileUploader

  mainUI = uploader.ui

  uploader.listen { status => btnOk.setEnabled(status.isInstanceOf[UploadSucceeded]) }
  uploader.reset()

  setCancelButtonHandler {
    if (uploader.ui.upload.isUploading) {
      uploader.ui.upload.interruptUpload()
    }
  }
}


/**
 * Uploads a file chosen by a user into system-dependent default temporary-file directory.
 */
// todo: ??? opts: mayEditFilename, mayOverwrite ???
// todo: ??? set custom upload receiver ???
class FileUploader extends Publisher[UploadStatus] {
  private val uploadedFileOptRef = Atoms.OptRef[UploadedFile]

  /**
   * This function transforms uploaded file name to a save-as-name.
   * For ex. can be used to remove file extension, replace spaces and/or non ASCII characters.
   *
   * By default returns unmodified (original) filename.
   */
  var fileNameToSaveAsName: (String => String) = identity

  val ui = new FileUploaderUI |>> { ui =>
    // Temp file based receiver
    val receiver = new Upload.Receiver {
      val file = File.createTempFile("imcms_upload", null) |>> {
        _.deleteOnExit()
      }

      override def receiveUpload(filename: String, mimeType: String): OutputStream = new FileOutputStream(file)
    }

    ui.upload.setReceiver(receiver)
    ui.upload.addListener(new Upload.StartedListener {
      def uploadStarted(ev: Upload.StartedEvent) {
        reset()
        updateDisabled(ui.txtSaveAsName) { txtSaveAsName =>
          txtSaveAsName.value = fileNameToSaveAsName(ev.getFilename)
          txtSaveAsName.setInputPrompt(ui.txtSaveAsName.value)
        }
        ui.pgiBytesReceived.setEnabled(true)
        notifyListeners(UploadStarted(ev))
      }
    })
    ui.upload.addListener(new Upload.ProgressListener {
      def updateProgress(readBytes: Long, contentLength: Long) {
        ui.pgiBytesReceived.setValue(Float.box(readBytes.toFloat / contentLength))
        notifyListeners(UploadProgressUpdated(readBytes, contentLength))
      }
    })
    ui.upload.addListener(new Upload.FailedListener {
      def uploadFailed(ev: Upload.FailedEvent) {
        ui.txtSaveAsName.setEnabled(true)
        ui.txtSaveAsName.value = ""
        ui.txtSaveAsName.setInputPrompt(null)
        ui.txtSaveAsName.setEnabled(false)
        ui.pgiBytesReceived.setEnabled(false)
        FileUtils.deleteQuietly(receiver.file)
        ui.rootWindow.showWarningNotification("file.upload.interrupted.warn.msg".i)
        notifyListeners(UploadFailed(ev))
      }
    })
    ui.upload.addListener(new Upload.SucceededListener {
      def uploadSucceeded(ev: Upload.SucceededEvent) {
        ui.txtSaveAsName.setEnabled(true)
        ui.chkOverwrite.setEnabled(true)
        ui.pgiBytesReceived.setValue(1f)

        UploadedFile(ev.getFilename, ev.getMIMEType, receiver.file) |> { uploadedFile =>
          uploadedFileOptRef.set(Some(uploadedFile))
          notifyListeners(UploadSucceeded(ev, uploadedFile))
        }
      }
    })
  }

  reset()

  def reset() {
    uploadedFileOptRef.get.foreach(uploadedFile => FileUtils.deleteQuietly(uploadedFile.file))
    uploadedFileOptRef.set(None)
    updateDisabled(ui.chkOverwrite) { _.value = false }
    updateDisabled(ui.txtSaveAsName) { saveAsName =>
      ui.txtSaveAsName.value = ""
      ui.txtSaveAsName.setInputPrompt(null)
    }
    updateDisabled(ui.pgiBytesReceived) { pgiBytesReceived =>
      pgiBytesReceived.setValue(0f)
      pgiBytesReceived.setPollingInterval(500)
    }

    notifyListeners(UploadReseted)
  }

  def uploadedFile: Option[File] = uploadedFileOptRef.get

  def saveAsName: String = ui.txtSaveAsName.trim

  def mayOverwrite: Boolean = ui.chkOverwrite.checked
//  def mayOverwrite_=(value: Boolean) {
//    if (value) {
//      ui.chkOverwrite.setEnabled(true)
//    } else {
//      ui.chkOverwrite.uncheck()
//      ui.chkOverwrite.setEnabled(false)
//    }
//  }
}


class FileUploaderUI extends FormLayout with UndefinedSize {
  val upload = new Upload("file.upload.dlg.frm.fld.select".i, null) with Immediate
  val txtSaveAsName = new TextField("file.upload.dlg.frm.fld.save_as".i) with Required
  val pgiBytesReceived = new ProgressIndicator; pgiBytesReceived.setCaption("file.upload.dlg.frm.fld.progress".i)
  val chkOverwrite = new CheckBox("file.upload.dlg.frm.fld.overwrite".i)

  upload.setButtonCaption("...")
  this.addComponents(upload, pgiBytesReceived, txtSaveAsName, chkOverwrite)

  txtSaveAsName.setRequiredError("required")
}