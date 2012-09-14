package com.imcode
package imcms
package admin.system.file

import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.imcode.imcms.vaadin._
import com.imcode.util.event.Publisher
import java.io._
import org.apache.commons.io.FileUtils


case class UploadedFile(name: String, mimeType: String, file: File)


sealed trait UploadStatus
case object UploadReseted extends UploadStatus
case class UploadStarted(event: Upload.StartedEvent) extends UploadStatus
case class UploadProgressUpdated(readBytes: Long, contentLength: Long) extends UploadStatus
case class UploadSucceeded(event: Upload.SucceededEvent, uploadedFile: UploadedFile) extends UploadStatus
case class UploadFailed(event: Upload.FailedEvent) extends UploadStatus


class FileUploaderDialog(caption: String = "") extends OkCancelDialog(caption) {
  val uploader = new FileUploader

  mainUI = uploader.ui

  uploader.listen { btnOk setEnabled _.isInstanceOf[UploadSucceeded] }
  uploader.reset()

  wrapCancelHandler {
    if (uploader.ui.upload.isUploading) {
      uploader.ui.upload.interruptUpload
    }
  }
}


/**
 * Uploads a file chosen by a user into system-dependent default temporary-file directory.
 */
// opts?:
// allows edit original filename
// allows overwrite existing file with the same filename
// upload receiver

class FileUploader extends Publisher[UploadStatus] {
  private val uploadedFileOptRef = Atoms.OptRef[UploadedFile]

  /**
   * This function transforms uploaded file name to a save-as-name.
   * For ex. can be used to remove file extension, replace spaces and/or non ASCII characters.
   *
   * By default returns unmodifed (original) filename.
   */
  var fileNameToSaveAsName = identity[String]_

  val ui = new FileUploaderUI |>> { ui =>
    // Temp file based receiver
    val receiver = new Upload.Receiver {
      val file = File.createTempFile("imcms_upload", null) |>> {
        _.deleteOnExit()
      }

      def receiveUpload(filename: String, mimeType: String) = new FileOutputStream(file)
    }

    ui.upload.setReceiver(receiver)
    ui.upload.addListener(new Upload.StartedListener {
      def uploadStarted(ev: Upload.StartedEvent) = {
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
        ui.getApplication.showWarningNotification("file.upload.interrupted.warn.msg".i)
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

  //todo: delete uploaded file???
  def reset() {
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

  def uploadedFile = uploadedFileOptRef.get

  def saveAsName = ui.txtSaveAsName.trim

  def isOverwrite = ui.chkOverwrite.booleanValue

  // init
  reset()
}


class FileUploaderUI extends FormLayout with UndefinedSize {
  val upload = new Upload("file.upload.dlg.frm.fld.select".i, null) with Immediate
  val txtSaveAsName = new TextField("file.upload.dlg.frm.fld.save_as".i) with Required
  val pgiBytesReceived = new ProgressIndicator; pgiBytesReceived.setCaption("file.upload.dlg.frm.fld.progress".i)
  val chkOverwrite = new CheckBox("file.upload.dlg.frm.fld.overwrite".i)

  upload.setButtonCaption("...")
  addComponentsTo(this, upload, pgiBytesReceived, txtSaveAsName, chkOverwrite)

  txtSaveAsName.setRequiredError("achtung")
}