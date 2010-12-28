package com.imcode
package imcms.admin.document.template

import com.imcode.imcms.vaadin._
import com.vaadin.ui._
import scala.collection.JavaConversions._

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

  chkUseFilenameAsName addListener block {
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


//class CheckBoxList[T <: AnyRef](caption: String="") extends Panel(caption) {
//  let(getContent.asInstanceOf[VerticalLayout]) { lyt =>
//    lyt setSpacing true
//    lyt setMargin false
//  }
//
//  setStyleName(Panel.STYLE_LIGHT)
//  setSizeFull
//
//  def selected: List[T] =
//    for { component <- getComponentIterator.toList
//          item <- component match {
//            case checkBox: CheckBox if checkBox.booleanValue => Some(checkBox.getData.asInstanceOf[T])
//            case _ => None
//          }
//    } yield item
//
//  def addItem(item: T)(implicit ev: T =:= String): Unit = addItem(caption=item, item)
//
//  def addItem(caption: String, item: T) {
//    let(new CheckBox(caption)) { c =>
//      c setData item
//      addComponent(c)
//    }
//  }
//
//  def select(item: T*) = getComponentIterator foreach {
//    case c: CheckBox if c.getData == item => c setValue true
//    case _ =>
//  }
//}