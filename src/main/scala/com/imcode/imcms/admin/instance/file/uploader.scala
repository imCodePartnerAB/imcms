package com.imcode
package imcms
package admin.instance.file

import com.imcode.imcms.vaadin.Current
import com.vaadin.ui._
import com.imcode.util.event.Publisher
import java.io._
import org.apache.commons.io.FileUtils
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.server._
import com.imcode.imcms.vaadin.data._
import com.vaadin.server.Page
import java.util.concurrent.atomic.AtomicReference


case class UploadedFile(name: String, mimeType: String, file: File)


sealed trait UploadStatus
case object UploadReseted extends UploadStatus
case class UploadStarted(event: Upload.StartedEvent) extends UploadStatus
case class UploadProgressUpdated(readBytes: Long, contentLength: Long) extends UploadStatus
case class UploadSucceeded(event: Upload.SucceededEvent, uploadedFile: UploadedFile) extends UploadStatus
case class UploadFailed(event: Upload.FailedEvent) extends UploadStatus


/**
 * Client code should implement uploaded file save logic.
 *
 * @param caption
 */
class FileUploaderDialog(caption: String = "") extends OkCancelDialog(caption) {
  val uploader = new FileUploader

  mainComponent = uploader.view

  setCancelButtonHandler {
    if (uploader.view.upload.isUploading) {
      uploader.view.upload.interruptUpload()
    }

    uploader.deleteUploadedFile()

    close()
  }

  uploader.listen { status => btnOk.setEnabled(status.isInstanceOf[UploadSucceeded]) }
  uploader.reset()
}


/**
 * Uploads a file chosen by a user into system-dependent default temporary-file directory.
 */
// todo: ??? opts: mayEditFilename, mayOverwrite ???
// todo: ??? set custom upload receiver ???
class FileUploader extends Publisher[UploadStatus] {
  private val uploadedFileOptRef: AtomicReference[Option[UploadedFile]] = new AtomicReference(None)

  /**
   * This function transforms uploaded file name to a save-as-name.
   * For ex. can be used to remove file extension, replace spaces and/or non ASCII characters.
   *
   * By default returns unmodified (original) filename.
   */
  var fileNameToSaveAsName: (String => String) = identity

  val view = new FileUploaderView |>> { w =>
    // Temp file based receiver
    val receiver = new Upload.Receiver {
      val file = File.createTempFile("imcms_upload", null) |>> {
        _.deleteOnExit()
      }

      override def receiveUpload(filename: String, mimeType: String): OutputStream = new FileOutputStream(file)
    }

    w.upload.setReceiver(receiver)
    w.upload.addListener(new Upload.StartedListener {
      def uploadStarted(ev: Upload.StartedEvent) {
        reset()
        updateDisabled(w.txtSaveAsName) { txtSaveAsName =>
          txtSaveAsName.value = fileNameToSaveAsName(ev.getFilename)
          txtSaveAsName.setInputPrompt(w.txtSaveAsName.value)
        }
        w.pgiBytesReceived.setEnabled(true)
        notifyListeners(UploadStarted(ev))
      }
    })
    w.upload.addListener(new Upload.ProgressListener {
      def updateProgress(readBytes: Long, contentLength: Long) {
        w.pgiBytesReceived.value = readBytes.toFloat / contentLength
        notifyListeners(UploadProgressUpdated(readBytes, contentLength))
      }
    })
    w.upload.addListener(new Upload.FailedListener {
      def uploadFailed(ev: Upload.FailedEvent) {
        w.txtSaveAsName.setEnabled(true)
        w.txtSaveAsName.value = ""
        w.txtSaveAsName.setInputPrompt(null)
        w.txtSaveAsName.setEnabled(false)
        w.pgiBytesReceived.setEnabled(false)
        FileUtils.deleteQuietly(receiver.file)
        Current.page.showWarningNotification("file.upload.interrupted.warn.msg".i)
        notifyListeners(UploadFailed(ev))
      }
    })
    w.upload.addListener(new Upload.SucceededListener {
      def uploadSucceeded(ev: Upload.SucceededEvent) {
        w.txtSaveAsName.setEnabled(true)
        w.chkOverwrite.setEnabled(true)
        w.pgiBytesReceived.value = 1

        UploadedFile(ev.getFilename, ev.getMIMEType, receiver.file) |> { uploadedFile =>
          uploadedFileOptRef.set(Some(uploadedFile))
          notifyListeners(UploadSucceeded(ev, uploadedFile))
        }
      }
    })
  }

  reset()

  def reset() {
    deleteUploadedFile()
    updateDisabled(view.chkOverwrite) { _.value = false }
    updateDisabled(view.txtSaveAsName) { saveAsName =>
      view.txtSaveAsName.value = ""
      view.txtSaveAsName.setInputPrompt(null)
    }
    updateDisabled(view.pgiBytesReceived) { pgiBytesReceived =>
      pgiBytesReceived.value = 1
      pgiBytesReceived.setPollingInterval(500)
    }

    notifyListeners(UploadReseted)
  }

  def uploadedFile: Option[UploadedFile] = uploadedFileOptRef.get

  def saveAsName: String = view.txtSaveAsName.trimmedValue

  def mayOverwrite: Boolean = view.chkOverwrite.checked
//  def mayOverwrite_=(value: Boolean) {
//    if (value) {
//      ui.chkOverwrite.setEnabled(true)
//    } else {
//      ui.chkOverwrite.uncheck()
//      ui.chkOverwrite.setEnabled(false)
//    }
//  }

  def deleteUploadedFile() {
    for (uploadedFile <- uploadedFileOptRef.getAndSet(None)) {
      FileUtils.deleteQuietly(uploadedFile.file)
    }
  }
}


class FileUploaderView extends FormLayout with UndefinedSize {
  val upload = new Upload("file_upload_dlg.frm.fld.select".i, null) with Immediate
  val txtSaveAsName = new TextField("file_upload_dlg.frm.fld.save_as".i) with Required
  val pgiBytesReceived = new ProgressIndicator; pgiBytesReceived.setCaption("file_upload_dlg.frm.fld.progress".i)
  val chkOverwrite = new CheckBox("file_upload_dlg.frm.fld.overwrite".i)

  upload.setButtonCaption("...")
  addComponents(upload, pgiBytesReceived, txtSaveAsName, chkOverwrite)

  txtSaveAsName.setRequiredError("required")
}