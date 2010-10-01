package com.imcode.imcms.servlet.superadmin.vaadin.template

import com.imcode._
import com.imcode.imcms.servlet.superadmin.vaadin.ui._
import com.imcode.imcms.servlet.superadmin.vaadin.ui.{UploadEventHandler, MemoryUploadReceiver}
//import com.vaadin.data.Property
//import com.vaadin.data.Property._
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
    addComponents(this, lblUploadStatus, upload)
    setComponentAlignment(lblUploadStatus, Alignment.MIDDLE_LEFT)
  }

  chkUseFilenameAsName addListener unit {
    alterNameTextField()
  }
  
  chkUseFilenameAsName setValue true
  addComponents(this, lytUpload, lytName, chkOverwriteExisting)  

  def alterNameTextField() {
    def formatStatusMsg(Length: Int, msg: String): String = {
      val formattedMsg = let(wrapString(msg)) { s =>
        s.length match {
          case Length => s
          case n if n > Length => s take (Length - 3) padTo (Length, ".")
          case _ => s.padTo(Length, " ")
        }
      }

      formattedMsg.mkString
    }

    let(uploadReceiver.uploadRef.get) { uploadOpt =>
      forlet(lytName, chkOverwriteExisting) {
        _ setEnabled uploadOpt.isDefined
      }

      lblUploadStatus setValue formatStatusMsg(30, 
        uploadOpt match {
          case Some(upload) => upload.filename
          case _ => "No file selected"
        })

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


class EditTemplateDialogContent extends FormLayout {
  val txtName = new TextField("Name")
  addComponent(txtName)
}


class EditTemplateContentDialogContent extends VerticalLayout {
  val pnlContent = new Panel {setSizeFull; setStyleName(Panel.STYLE_LIGHT); getContent.setSizeFull}
  val txtContent = new TextField {setRows(20); setSizeFull}

  pnlContent addComponent txtContent
  addComponent(pnlContent)
}


class TemplateGroupDialogContent extends FormLayout {
  val txtId = new TextField("Id") {setEnabled(false)}
  val txtName = new TextField("Name")
  val twsTemplates = new TwinSelect("Templates")

  addComponents(this, txtId, txtName, twsTemplates)
}
