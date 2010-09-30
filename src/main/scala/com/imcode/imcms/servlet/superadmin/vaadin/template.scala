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
  val chkUseFilenameAsName = new CheckBox("Use filename") { setImmediate(true) }
  val chkOverwriteExisting = new CheckBox("Replace existing")

  val upload = new Upload(null, uploadReceiver) with UploadEventHandler {
    setImmediate(true)
    setButtonCaption("Select")
    
    def handleEvent(e: com.vaadin.ui.Component.Event) = e match {
      case e: Upload#SucceededEvent =>
        alterNameTextField()
      case e: Upload#FailedEvent =>
        uploadReceiver.uploadRef.set(None)
        alterNameTextField()
      case _ => 
    }
  }
  val lblUploadStatus = new Label

  val lytName = new HorizontalLayout {
    setCaption("Name")
    setSpacing(true)
    setSizeUndefined
    addComponents(this, chkUseFilenameAsName, txtName)
    setComponentAlignment(chkUseFilenameAsName, Alignment.MIDDLE_LEFT)
  }

  val lytUpload = new HorizontalLayout {
    setCaption("File")
    setSpacing(true)
    setSizeUndefined    
    addComponents(this, upload, lblUploadStatus)
    setComponentAlignment(lblUploadStatus, Alignment.MIDDLE_LEFT)
  }

  chkUseFilenameAsName addListener new ValueChangeListener {
    def valueChange(e: ValueChangeEvent) = alterNameTextField()
  }
  
  //chkUseFilenameAsName addListener { alterNameTextField() }
  chkUseFilenameAsName setValue true
  addComponents(this, lytUpload, lytName, chkOverwriteExisting)  

  def alterNameTextField() {
    let(uploadReceiver.uploadRef.get) { uploadOpt =>
      forlet(lytName, chkOverwriteExisting) {
        _ setEnabled uploadOpt.isDefined
      }

      uploadOpt match {
        case Some(upload) => lblUploadStatus.setValue(upload.filename)
        case _ => lblUploadStatus.setValue("No file selected")
      }

      let (chkUseFilenameAsName.booleanValue) { useFilenameAsName =>
        txtName setEnabled !useFilenameAsName
        if (useFilenameAsName) {
          txtName.setValue(uploadOpt match {
            case Some(upload) => let(upload.filename) { filename =>
              filename.slice(0, filename.lastIndexOf("."))
            }

            case _ => ""
          })
        }
      }
    }
  }
}
