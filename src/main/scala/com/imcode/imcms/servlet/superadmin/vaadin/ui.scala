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
import imcode.util.Utility
import imcode.server.user._
import com.imcode.imcms.api.{SystemProperty, IPAccess, Document}
import imcode.server.{SystemData, Imcms}
import java.util.{Date, Collection => JCollection}
import com.vaadin.ui.Layout.MarginInfo
import java.util.concurrent.atomic.AtomicReference
import java.lang.{String, Class => JClass, Boolean => JBoolean, Integer => JInteger}
import java.io.{ByteArrayOutputStream, OutputStream, FileOutputStream, File}
import com.vaadin.terminal.{ThemeResource, UserError}
import com.vaadin.Application

//class ButtonWrapper(button: Button) {
//
//  def addListener(eventHandler: Button#ClickEvent => Unit) =
//    button add new Button.ClickListener {
//      def buttonClick(event: Button#ClickEvent) = eventHandler(event)
//    }
//
//  def addListener(block: => Unit) = addListener { _ => block }
//}
//
//object ButtonWrapper {
//  implicit def wrapButton(button: Button) = new ButtonWrapper(button)
//}

//class AbstractComponentContainerWrapper(container: AbstractComponentContainer) {  
//
//  def addComponents(component: Component, components: Component*) = {
//    component +: components foreach container.addComponent
//    container
//  }
//}

trait VaadinApplication { this: Application =>  

  def initAndShow[W <: Window](window: W, modal: Boolean=true, resizable: Boolean=false, draggable: Boolean=true)(init: W => Unit) {
    init(window)
    window setModal modal
    window setResizable resizable
    window setDraggable draggable
    getMainWindow addWindow window
  }

  def show[W <: Window](window: W, modal: Boolean=true, resizable: Boolean=false, draggable: Boolean=true) =
   initAndShow(window, modal, resizable, draggable) { _ => }
}

class AbstractFieldWrapper(f: com.vaadin.ui.AbstractField) {
  def stringValue = f.getValue.asInstanceOf[String]
  def booleanValue = Boolean unbox f.getValue.asInstanceOf[JBoolean] 
  def asList[T <: AnyRef] = f.getValue.asInstanceOf[JCollection[T]].toList
}

object AbstractFieldWrapper {
  implicit def wrapAbstractField(f: AbstractField) = new AbstractFieldWrapper(f)
}

import AbstractFieldWrapper._

// Fixed size dialog window with full margin   
// Buttons are centered
class Dialog(caption: String = "") extends Window(caption) {
  protected [this] val content = new GridLayout(1, 2) {
    setMargin(true)
    setSpacing(true)
    setColumnExpandRatio(0, 1f)
    setRowExpandRatio(0, 1f)    
  }  

  setContent(content)

  def mainContent = content.getComponent(0, 0)

  // deprecated, breaks LSP !!
  def mainContent_=[C <: Component](component: C): C = setMainContent(component)

  def setMainContent[C <: Component](component: C): C = {
    component.setSizeUndefined

    content.addComponent(component, 0, 0)
    content.setComponentAlignment(component, Alignment.TOP_LEFT)

    component    
  }


  def buttonsBarContent = content.getComponent(0, 1)

  def buttonsBarContent_=(component: Component) {
    component.setSizeUndefined
    
    content.addComponent(component, 0, 1)
    content.setComponentAlignment(component, Alignment.TOP_CENTER)
  }
}


trait CustomSizeDialog extends Dialog {
  content.setSizeFull
  
  override def mainContent_=[C <: Component](component: C): C = {
    super.mainContent = component
    component.setSizeFull

    component
  }
}

trait BottomMarginOnlyDialog extends Dialog {
  content.setMargin(false, false, true, false)
}


/** Message dialog window. */
class MsgDialog(caption: String = "", msg: String ="") extends Dialog(caption) {
  val btnOk = new Button("Ok") { setIcon(new ThemeResource("icons/16/ok.png")) }

  val lblMessage = new Label(msg)

  mainContent = lblMessage
  buttonsBarContent = btnOk

  btnOk addListener unit { close }
}


/** OKCancel dialog window. */
class OkCancelDialog(caption: String = "") extends Dialog(caption) {
  val btnOk = new Button("Ok") { setIcon(new ThemeResource("icons/16/ok.png")) }
  val btnCancel = new Button("Cancel") { setIcon(new ThemeResource("icons/16/cancel.png")) }
  val lytButtons = new GridLayout(2, 1) {
    setSpacing(true)
    addComponent(btnOk)
    addComponent(btnCancel)
    setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT)
    setComponentAlignment(btnCancel, Alignment.MIDDLE_LEFT)
  }

  buttonsBarContent = lytButtons

  btnCancel addListener unit { close }

  // refactor
//  def addOkButtonClickListener(listener: Button.ClickListener) {
//    btnOk addListener { e: Button#ClickEvent =>
//      try {
//        listener buttonClick e
//        close
//      } catch {
//        case ex: Exception => using(new java.io.StringWriter) { w =>
//          ex.printStackTrace(new java.io.PrintWriter(w))
//          //show(new MsgDialog("ERROR", "%s  ##  ##  ##  ## ## %s" format (ex.getMessage, w.getBuffer)))
//          throw ex
//        }
//      }
//    }
//  }

  def addOkButtonClickListener(block: => Unit) {
    btnOk addListener unit {
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

  mainContent = lblMessage
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


class TwinSelect[T <: AnyRef](caption: String = "") extends GridLayout(3, 1) {
  setCaption(caption)

  val btnAdd = new Button("<<")
  val btnRemove = new Button(">>")
  val lstAvailable = new ListSelect("Available")
  val lstChosen = new ListSelect("Chosen")
  val lytButtons = new VerticalLayout {
    addComponents(this, btnAdd, btnRemove)
  }

  addComponents(this, lstChosen, lytButtons, lstAvailable)
  setComponentAlignment(lytButtons, Alignment.MIDDLE_CENTER)

  forlet(lstAvailable, lstChosen) { l =>
    l setMultiSelect true
    l setImmediate true
    l setColumns 10
    l setRows 5
    l setItemCaptionMode AbstractSelect.ITEM_CAPTION_MODE_EXPLICIT
  }

  btnAdd addListener unit { move(lstAvailable, lstChosen) }
  btnRemove addListener unit { move(lstChosen, lstAvailable) }

  lstAvailable addListener unit { reset() }
  lstChosen addListener unit { reset() }

  reset()

  def reset() {
    btnAdd.setEnabled(lstAvailable.asList[T].size > 0)
    btnRemove.setEnabled(lstChosen.asList[T].size > 0)
  }

  private [this] def move(src: ListSelect, dest: ListSelect) = src.asList[T]  foreach { itemId =>
    let (src getItemCaption itemId) { itemCaption =>
      addItem(dest, itemId, itemCaption)
    }

    src removeItem itemId
  }

  def availableItemIds = lstAvailable.getItemIds.asInstanceOf[JCollection[T]].toList
  def chosenItemIds = lstChosen.getItemIds.asInstanceOf[JCollection[T]].toList

  def addAvailableItem(itemId: T)(implicit ev: T =:= String): Unit = addAvailableItem(itemId, itemId)
  
  def addAvailableItem(itemId: T, caption: String) = addItem(lstAvailable, itemId, caption)

  def addChosenItem(itemId: T)(implicit ev: T =:= String): Unit = addChosenItem(itemId, itemId)

  def addChosenItem(itemId: T, caption: String)  = addItem(lstChosen, itemId, caption)

  private [this] def addItem(listSelect: ListSelect, itemId: T, caption: String) {
    listSelect.addItem(itemId)
    listSelect.setItemCaption(itemId, caption)    
  }

  def setListRows(count: Int) = forlet(lstAvailable, lstChosen) { _ setRows count }
  def setListColumns(count: Int) = forlet(lstAvailable, lstChosen) { _ setColumns count }
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


case class MemoryUpload(filename: String, mimeType: String, content: Array[Byte])

class MemoryUploadReceiver extends Upload.Receiver {

  val uploadRef: AtomicReference[Option[MemoryUpload]] = new AtomicReference(None)

  def receiveUpload(filename: String, mimeType: String) =
    new ByteArrayOutputStream {
      override def close() {
        super.close
        uploadRef set Some(MemoryUpload(filename, mimeType, toByteArray))
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


class TableView extends VerticalLayout {

  val table = new Table {
    setSelectable(true)
    setImmediate(true)
    setPageLength(10)

    this addListener unit { resetComponents }

    tableFields foreach { addContainerProperties(this, _) }
  }

  val btnReload = new Button("Reload") {
    this addListener unit { reloadTable }
    setStyleName(Button.STYLE_LINK)
    setIcon(new ThemeResource("icons/16/reload.png"))
  }

  val lytToolBar = new HorizontalLayout {
    setWidth("100%")
    setSpacing(true)
  }

  val lytHeader = new HorizontalLayout {
    setWidth("100%")
    setSpacing(true)
  }

  val lytTable = new VerticalLayout {
    setSpacing(true)
    addComponent(table)
  }

  addComponents(lytHeader, lytToolBar, btnReload)
  lytHeader.setExpandRatio(lytToolBar, 1.0f)

  setSpacing(true)
  addComponents(this, lytHeader, lytTable)

  reloadTable()
  resetComponents()

  type RowId = AnyRef
  type RowData = Seq[AnyRef]
  type Row = (RowId, RowData)
  
  type PropertyId = AnyRef
  type PropertyType = JClass[_]
  type PropertyDefaultValue = AnyRef
  type Property = (PropertyId, PropertyType, PropertyDefaultValue)

  def tableRows: Seq[Row] = List.empty

  def tableFields: Seq[Property] = List.empty

  def reloadTable() {
    table.removeAllItems

    for((id, data) <- tableRows) table.addItem(data.toArray, id)
  }

  def resetComponents() {}
}