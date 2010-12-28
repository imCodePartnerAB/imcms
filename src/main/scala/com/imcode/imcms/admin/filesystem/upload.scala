package com.imcode
package imcms.admin.filesystem

import scala.collection.JavaConversions._
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._

import imcode.util.Utility
import imcode.server.user._
import scala.collection.mutable.{Map => MMap}
import imcode.server.{SystemData, Imcms}
import java.util.{Date}
import com.vaadin.ui.Layout.MarginInfo
import imcode.server.document.{CategoryDomainObject, CategoryTypeDomainObject, DocumentDomainObject}
import com.vaadin.terminal.{FileResource, Resource, UserError}
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}
import com.vaadin.data.util.FilesystemContainer
import java.io.{FilenameFilter, OutputStream, FileOutputStream, File}
import java.util.concurrent.atomic.AtomicReference
import com.imcode.util.event.Publisher


class FileUpload {
  val receiver = new MemoryUploadReceiver

  val ui = letret(new FileUploadUI) { ui =>
    ui.upload.setReceiver(receiver)
    ui.btnCancel addListener block { ui.upload.interruptUpload }

    ui.upload.addListener(new Upload.StartedListener {
      def uploadStarted(ev: Upload#StartedEvent) {
        receiver.uploadRef.set(None)
          // this method gets called immediatly after upload is
          // started
          ui.pi.setValue(0f)
          //ui.pi.setVisible(true);
          ui.pi.setPollingInterval(500) // hit server frequently to get
          //ui.lblProgress.setVisible(true);
          // updates to client
          ui.lblStatus.value = "Uploading"
          ui.txtFileName.value = ev.getFilename /////////// <- clear if intrrupted

          //cancelProcessing.setVisible(true);
      }
    })

    ui.upload.addListener(new Upload.ProgressListener {
      def updateProgress(readBytes: Long, contentLength: Long) {
        // this method gets called several times during the update
        ui.pi.setValue(new JFloat(readBytes / contentLength.asInstanceOf[Float]))
        ui.lblProgress.value = "Processed " + readBytes + " bytes of " + contentLength
      }
    })


    ui.upload.addListener(new Upload.SucceededListener {
      def uploadSucceeded(sv: Upload#SucceededEvent) {
          //result.setValue(counter.getLineBreakCount() + " (total)");
      }
    })

    ui.upload.addListener(new Upload.FailedListener {
      def uploadFailed(ev: Upload#FailedEvent) {
        // reason: interrupted
      }
    })

    ui.upload.addListener(new Upload.FinishedListener {
      def uploadFinished(ev: Upload#FinishedEvent) {
        ui.lblStatus.value = "idle"
        //pi.setVisible(false);
        //textualProgress.setVisible(false);
        //cancelProcessing.setVisible(false);
      }
    })
  }
}

class FileUploadUI extends FormLayout with UndefinedSize {
  val upload = new Upload("Choose file...", null) with Immediate //{setStyleName("small")}
  val txtFileName = new Label("File name") with UndefinedSize
  val btnCancel = new Button("Cancel")  {setStyleName("small")}
  val pi = new ProgressIndicator
  val lblProgress = new Label
  val lblStatus = new Label("idle")

  addComponents(this, upload, lblStatus, txtFileName, pi, lblProgress, btnCancel)
}