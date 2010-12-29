package com.imcode
package imcms.admin.filesystem

import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.imcode.imcms.vaadin._
import java.util.concurrent.atomic.AtomicReference
import com.imcode.util.event.Publisher
import java.io._
import com.vaadin.ui.Window.Notification

case class UploadedData(filename: String, mimeType: String, content: Array[Byte])

sealed trait UploadStatus
case object UploadNew extends UploadStatus
case class UploadStarted(event: Upload#StartedEvent) extends UploadStatus
case class UploadProgress(readBytes: Long, contentLength: Long) extends UploadStatus
case class UploadSucceeded(event: Upload#SucceededEvent, data: UploadedData) extends UploadStatus
case class UploadFailed(event: Upload#FailedEvent) extends UploadStatus



class FileUploadDialog(caption: String = "") extends OkCancelDialog(caption) {
  val upload = new FileUpload

  mainContent = upload.ui

  upload.listen { btnOk setEnabled _.isInstanceOf[UploadSucceeded] }

  btnCancel addListener block { upload.ui.upload.interruptUpload }
}

class FileUpload extends Publisher[UploadStatus] {
  private val dataRef = new AtomicReference[Option[UploadedData]](None)

  /** Creates file save as name from original filename. */
  var fileNameToSaveAsName = identity[String]_
  val ui = letret(new FileUploadUI) { ui =>
    val receiver = new Upload.Receiver {
      val out = new ByteArrayOutputStream
      def receiveUpload(filename: String, mimeType: String) = out
    }

    ui.upload.setReceiver(receiver)
    ui.upload.addListener(new Upload.StartedListener {
      def uploadStarted(ev: Upload#StartedEvent) = {
        reset()
        ui.txtSaveAsName.value = fileNameToSaveAsName(ev.getFilename)
        notifyListeners(UploadStarted(ev))
      }
    })
    ui.upload.addListener(new Upload.ProgressListener {
      def updateProgress(readBytes: Long, contentLength: Long) {
        ui.pi.setValue(Float.box(readBytes.toFloat / contentLength))
        notifyListeners(UploadProgress(readBytes, contentLength))
      }
    })
    ui.upload.addListener(new Upload.FailedListener {
      def uploadFailed(ev: Upload#FailedEvent) {
        ui.getApplication.getMainWindow.showNotification("Upload has been interrupted", Notification.TYPE_ERROR_MESSAGE)
        notifyListeners(UploadFailed(ev))
      }
    })
    ui.upload.addListener(new Upload.SucceededListener {
      def uploadSucceeded(ev: Upload#SucceededEvent) {
        let(UploadedData(ev.getFilename, ev.getMIMEType, receiver.out.toByteArray)) { data =>
          dataRef.set(Some(data))
          ui.txtSaveAsName.setEnabled(true)
          ui.txtSaveAsName.value = ev.getFilename
          notifyListeners(UploadSucceeded(ev, data))
        }
      }
    })
  }

  def reset() {
    dataRef.set(None)
    ui.txtSaveAsName.value = ""
    ui.txtSaveAsName.setEnabled(false)
    ui.pi.setValue(0f)
    ui.pi.setPollingInterval(500)
    notifyListeners(UploadNew)
  }

  def data = dataRef.get
}

class FileUploadUI extends FormLayout with UndefinedSize {
  val upload = new Upload("Choose file", null) with Immediate
  val txtSaveAsName = new TextField("Save as")
  val pi = new ProgressIndicator; pi.setCaption("Progress")
  val chkOverwrite = new CheckBox("Overwrite existing")

  upload.setButtonCaption("...")
  addComponents(this, upload, txtSaveAsName, chkOverwrite, pi)
}