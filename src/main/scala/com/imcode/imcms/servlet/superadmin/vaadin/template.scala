package com.imcode.imcms.servlet.superadmin.vaadin.template

import com.imcode._
import com.imcode.imcms.servlet.superadmin.vaadin.ui._
import com.imcode.imcms.servlet.superadmin.vaadin.ui.{UploadEventHandler, MemoryUploadReceiver}
import com.vaadin.data.Property
import com.vaadin.data.Property._
import com.vaadin.ui._

class TemplateDialogContent extends FormLayout {
  val uploadReceiver = new MemoryUploadReceiver
  val txtName = new TextField
  val chkUseFilenameAsName = new CheckBox("Use filename as name")
  val chkOverwriteExisting = new CheckBox("Overwrite existing")

  val upload = new Upload("File", uploadReceiver) with UploadEventHandler {
    def handleEvent(e: com.vaadin.ui.Component.Event) = e match {
      case e: Upload#SucceededEvent =>
        alterNameTextField()
      case e: Upload#FailedEvent =>
        uploadReceiver.upload.set(None)
        alterNameTextField()
      case _ => 
    }
  }

  val lytName = new HorizontalLayout {
    setCaption("Name")
    addComponents(this, txtName, chkUseFilenameAsName, chkOverwriteExisting)
    setSpacing(true)
    setSizeUndefined
  }

  chkUseFilenameAsName addListener new ValueChangeListener {
    def valueChange(e: ValueChangeEvent) = alterNameTextField()
  }
  
  chkUseFilenameAsName addListener { alterNameTextField() }
  chkUseFilenameAsName setValue true
  addComponents(this, upload, lytName)  

  def alterNameTextField() {
    let(uploadReceiver.upload.get.isDefined) { uploaded =>
      forall(lytName, chkOverwriteExisting) {
        _ setEnabled uploaded
      }
    }

    let (chkUseFilenameAsName.booleanValue) { useFilenameAsName =>
      txtName setEnabled !useFilenameAsName
      if (useFilenameAsName) {
        txtName.setValue(uploadReceiver.upload.get match {
          case Some(memoryUpload) => let(memoryUpload.filename) { filename =>
            filename.slice(0, filename.lastIndexOf("."))
          }

          case _ => ""
        })
      }
    }
  }
}
