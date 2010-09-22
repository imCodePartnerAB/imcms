package com.imcode.imcms.servlet.superadmin.vaadin.ui

import scala.collection.JavaConversions._
import com.imcode._
import com.vaadin.event.ItemClickEvent
import com.vaadin.terminal.gwt.server.WebApplicationContext
import com.vaadin.ui._
import com.vaadin.data.Property
import com.vaadin.data.Property._
import com.imcode.imcms.dao.{MetaDao, SystemDao, LanguageDao, IPAccessDao}
import imcms.servlet.superadmin.AdminSearchTerms
import imcode.server.document.DocumentDomainObject
import com.imcode.imcms.api.Document.PublicationStatus
import com.vaadin.terminal.UserError
import imcode.util.Utility
import imcode.server.user._
import com.imcode.imcms.api.{SystemProperty, IPAccess, Document}
import imcode.server.{SystemData, Imcms}
import java.util.{Date, Collection => JCollection}
import com.vaadin.ui.Layout.MarginInfo
import java.io.{OutputStream, FileOutputStream, File}
import java.util.concurrent.atomic.AtomicReference
import java.lang.{String, Class => JClass, Boolean => JBoolean, Integer => JInteger}

object UI {

  type ButtonClickHandler = Button#ClickEvent => Unit
  type PropertyValueChangeHandler = ValueChangeEvent => Unit
  
  implicit def BlockToButtonClickListener(handler: => Unit): Button.ClickListener =
    new Button.ClickListener {
      def buttonClick(event: Button#ClickEvent) = handler
    }
  
//  def Button(caption: String="") = new Button(caption)
//
//  def TextField(caption: String="") = new TextField(caption)

//  def addButtonClickListener(button: Button)(handler: ButtonClickHandler) {
//    button addListener new Button.ClickListener {
//      def buttonClick(event: Button#ClickEvent) = handler(event)
//    }
//  }

  implicit def BlockToPropertyValueChangeListener(block: => Unit): Property.ValueChangeListener =
    new Property.ValueChangeListener {
      def valueChange(event: ValueChangeEvent) = block
    }

//  def addValueChangeHandler(target: AbstractField)(handler: ValueChangeEvent => Unit) {
//    target addListener new Property.ValueChangeListener {
//      def valueChange(event: ValueChangeEvent) = handler(event)
//    }
//  }

  def addComponents(container: AbstractComponentContainer, component: Component, components: Component*) = {
    component +: components foreach { c => container addComponent c }
    container
  }

  def addContainerProperties(table: Table, properties: (AnyRef, JClass[_], AnyRef)*) =
    for ((propertyId, propertyType, defaultValue) <- properties)
      table.addContainerProperty(propertyId, propertyType, defaultValue)
}

import UI._

class AbstractFieldWrapper(f: com.vaadin.ui.AbstractField) {
  def stringValue = f.getValue.asInstanceOf[String]
  def asList[T <: AnyRef] = f.getValue.asInstanceOf[JCollection[T]].toList
}

object AbstractFieldWrapper {
  implicit def wrapAbstractField(f: AbstractField) = new AbstractFieldWrapper(f)
}

class DialogWindow(caption: String = "") extends Window(caption) {
  val lytArea = new GridLayout(1, 2) {
    setMargin(true)
    setSpacing(true)
  }  

  def setMainAreaContent[C <: Component](c: C): C = {
    c.setSizeUndefined
    lytArea.addComponent(c, 0, 0)
    lytArea.setComponentAlignment(c, Alignment.BOTTOM_CENTER)
    c
  }

  def setButtonsAreaContent[C <: Component](c: C): C = {
    c.setSizeUndefined
    lytArea.addComponent(c, 0, 1)
    lytArea.setComponentAlignment(c, Alignment.TOP_CENTER)
    c
  }
         
  setContent(lytArea)
}


/** Message dialog window. */
class MsgDialog(caption: String = "", msg: String ="") extends DialogWindow(caption) {
  val btnOk = new Button("Ok")
  val lblMessage = new Label(msg)

  setMainAreaContent(lblMessage)
  setButtonsAreaContent(btnOk)

  btnOk addListener close
}


/** OKCancel dialog window. */
class OkCancelDialog(caption: String = "") extends DialogWindow(caption) {
  val btnOk = new Button("Ok")
  val btnCancel = new Button("Cancel")
  val lytButtons = new GridLayout(2, 1) {
    setSpacing(true)
    addComponent(btnOk)
    addComponent(btnCancel)
    setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT)
    setComponentAlignment(btnCancel, Alignment.MIDDLE_LEFT)
  }

  setButtonsAreaContent(lytButtons)

  btnCancel addListener close

  def addOkButtonClickListener(block: => Unit) {
    btnOk addListener {
      try {
        block
        close
      } catch {
        case ex: Exception => using(new java.io.StringWriter) { w =>
          ex.printStackTrace(new java.io.PrintWriter(w))
          //show(new MsgDialog("ERROR", "%s  ##  ##  ##  ## ## %s" format (ex.getMessage, w.getBuffer)))
          throw ex
        }
      }
    }
  }
}


/** Confirmation dialog window. */
class ConfirmationDialog(caption: String, msg: String) extends OkCancelDialog(caption) {
  def this(msg: String = "") = this("Confirmation", msg)

  val lblMessage = new Label(msg)

  setMainAreaContent(lblMessage)
}


/** Creates root item; root is not displayed */
class MenuItem(val parent: MenuItem = null, val handler: () => Unit = () => {}) {

  import collection.mutable.ListBuffer

  private val itemsBuffer = new ListBuffer[MenuItem]

  def items = itemsBuffer.toList

  override val toString = getClass.getName split '$' last

  val id = {
    def pathToRoot(m: MenuItem): List[MenuItem] = m :: (if (m.parent == null) Nil else pathToRoot(m.parent))

    pathToRoot(this).reverse map (_.toString) map camelCaseToUnderscore mkString "."
  }

  if (parent != null) parent.itemsBuffer += this

  // forces initialization of items declared as inner objects
  for (m <- getClass.getDeclaredMethods if m.getParameterTypes().length == 0)
    m.invoke(this)
}


class TwinSelect(caption: String = "") extends GridLayout(3, 1) {
  setCaption(caption)

  val btnAdd = new Button("<< Add")
  val btnRemove = new Button("Remove >>")
  val lstAvailable = new ListSelect("Available")
  val lstChosen = new ListSelect("Chosen")
  val lytButtons = new VerticalLayout {
    addComponents(this, btnAdd, btnRemove)
  }

  forlet(btnAdd, btnRemove) (_.setSizeUndefined)

  addComponents(this, lstChosen, lytButtons, lstAvailable)
  setComponentAlignment(lytButtons, Alignment.MIDDLE_CENTER)

  forlet(lstAvailable, lstChosen) { l =>
    l setMultiSelect true
    l setImmediate true
    l setColumns 11
  }

  def move(src: ListSelect, dest: ListSelect) = src.getValue.asInstanceOf[JCollection[_]] foreach { item =>
    src removeItem item
    dest addItem item
  }

  btnAdd addListener move(lstAvailable, lstChosen)

  btnRemove addListener move(lstChosen, lstAvailable)

  lstAvailable addListener new ValueChangeListener {
    def valueChange(e: com.vaadin.data.Property.ValueChangeEvent) {
      btnAdd.setEnabled(lstAvailable.getValue.asInstanceOf[JCollection[_]].size > 0)
    }
  }

  lstChosen addListener new ValueChangeListener {
    def valueChange(e: com.vaadin.data.Property.ValueChangeEvent) {
      btnRemove.setEnabled(lstChosen.getValue.asInstanceOf[JCollection[_]].size > 0)
    }
  }

  //def reset = forlet(lstAvailable, lstChosen) (l => l.)
}

/** Vertical layout containing tab sheet. */
class TabSheetView extends VerticalLayout {
  val tabSheet = new TabSheet

  addComponent(tabSheet)
  setMargin(true)

  // short cut
  def addTab(c: Component) = tabSheet.addTab(c)
}

/** Vertical layout with margin, spacing and optional caption. */
class VerticalLayoutView(caption: String = "", spacing: Boolean=true, margin: Boolean=true) extends VerticalLayout {
  setCaption(caption)
  setMargin(margin)
  setSpacing(spacing)
}

/** Horizontal layout with optional margin, spacing and caption. */
class HorizontalLayoutView(caption: String = "", spacing: Boolean=true, margin: Boolean=false) extends HorizontalLayout {
  setCaption(caption)
  setMargin(margin)
  setSpacing(spacing)
}


class FileUploadReceiver(uploadDir: String) extends Upload.Receiver {
  type MIMEType = String
  
  val upload: AtomicReference[Option[(File, MIMEType)]] = new AtomicReference(None)

  def receiveUpload(filename :String, mimeType: MIMEType) =
    let(new File(uploadDir+"/"+filename)) { file =>
      new FileOutputStream(file) {
        override def close() {
          super.close
          upload set Some(file, mimeType)
          println("Uploaded: " + upload.get)
        }
      }
    }
}


//sealed abstract class UploadStatus
//case class NotUploaded extends UploadStatus
//case class FailedUpload(event: Upload#FailedEvent) extends UploadStatus
//case class SuccessfulUpload(event: Upload#SucceededEvent) extends UploadStatus
//
//class FileUpload(caption: String, uploadDir: String) extends Upload(caption, new FileUploadReceiver(uploadDir))
//    with Upload.SucceededListener with Upload.FailedListener {
//  val uploadEvent: AtomicReference[Option[com.vaadin.ui.Component.Event]] = new AtomicReference(None)
//
//  def uploadSucceeded(event: Upload#SucceededEvent) = uploadEvent set event
//
//  def uploadFailed(event: Upload#FailedEvent) = uploadEvent set event
//
//  addListener(this: Upload.SucceededListener)
//
//  addListener(this: Upload.FailedListener)
//}

trait UploadEventHandler extends Upload.SucceededListener with Upload.FailedListener { this: Upload =>

  type E = com.vaadin.ui.Component.Event

  val event: AtomicReference[Option[E]] = new AtomicReference(None)

  def clearEvent = event set None

  def processEvent(e: E) {
    event set Some(e)
    handleEvent(e)
  }

  def uploadSucceeded(e: Upload#SucceededEvent) = processEvent(e)

  def uploadFailed(e: Upload#FailedEvent) = processEvent(e)

  def handleEvent(e: E): Unit

  addListener(this: Upload.SucceededListener)
  addListener(this: Upload.FailedListener)  
}


//
//class FileUploader extends Panel with Upload.SucceededListener with Upload.FailedListener with Upload.Receiver {
//  val file: AtomicReference[Option[File]] = new AtomicReference(None)
//
//  val upload = new Upload("Upload the file here", this)
//
//  addComponent(upload)
//
//  upload.addListener(this: Upload.SucceededListener)
//  upload.addListener(this: Upload.FailedListener)
//
//  // callback
//  def receiveUpload(filename :String, MIMEType: String): OutputStream = {
//      new FileOutputStream("/tmp/uploads/" + filename)
//  }

//  // This is called if the upload is finished.
//  def uploadSucceeded(event: Upload#SucceededEvent) {
//    file set new File(event.getFilename)
////        // Log the upload on screen.
////        root.addComponent(new Label("File " + event.getFilename()
////                + " of type '" + event.getMIMEType()
////                + "' uploaded."));
////
////        // Display the uploaded file in the image panel.
////        final FileResource imageResource =
////                new FileResource(file, getApplication());
////        imagePanel.removeAllComponents();
////        imagePanel.addComponent(new Embedded("", imageResource));
//  }
//
//  // This is called if the upload fails.
//  def uploadFailed(event: Upload#FailedEvent) {
//     println("FAILED!!!! " + event.getReason)
////        // Log the failure on screen.
////        root.addComponent(new Label("Uploading "
////                + event.getFilename() + " of type '"
////                + event.getMIMEType() + "' failed."));
//  }
//}