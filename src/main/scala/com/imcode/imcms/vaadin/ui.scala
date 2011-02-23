package com.imcode
package imcms.vaadin

import scala.collection.JavaConversions._
import com.vaadin.ui._
import java.util.concurrent.atomic.AtomicReference
import java.io.{ByteArrayOutputStream, OutputStream, FileOutputStream, File}
import com.vaadin.Application
import java.util.{Collections, LinkedList, ResourceBundle, Date, Collection => JCollection}
import com.vaadin.terminal.gwt.server.WebApplicationContext
import imcode.util.Utility
import com.vaadin.terminal.{Resource, Sizeable, ThemeResource, UserError}
import com.vaadin.ui.Window.Notification
import com.imcode.imcms.security._

trait ImcmsApplication extends Application {

  def content = getContext.asInstanceOf[WebApplicationContext]

  def session = content.getHttpSession

  def user = Utility.getLoggedOnUser(session)

  // todo: implement
  val resourceBundle = new ResourceBundle {
    def handleGetObject(key: String)  = "<" + key + ">"

    val getKeys = asJavaEnumeration(List.empty[String].iterator)
  }



  /**
   * If permission is granted executes an action.
   * Otherwise shows error notification and throws an exception.
   */
  def privileged[T](permission: => Permission)(action: => T) {
    permission match {
      case PermissionGranted => action
      case PermissionDenied(reason) =>
        this.showErrorNotification(reason)
        error(reason)
    }
  }
}

class ApplicationWrapper(app: Application) {

  def initAndShow[W <: Window](window: W, modal: Boolean=true, resizable: Boolean=false, draggable: Boolean=true)(init: W => Unit) {
    init(window)
    window setModal modal
    window setResizable resizable
    window setDraggable draggable
    app.getMainWindow addWindow window
  }

  def show(window: Window, modal: Boolean=true, resizable: Boolean=false, draggable: Boolean=true) =
    initAndShow(window, modal, resizable, draggable) { _ => }

  def showNotification(caption: String, description: String, notificationType: Int) =
    app.getMainWindow.showNotification(caption, description, notificationType)

  def showErrorNotification(caption: String, description: String = null) =
    showNotification(caption, description, Notification.TYPE_ERROR_MESSAGE)

  def showWarningNotification(caption: String, description: String = null) =
    showNotification(caption, description, Notification.TYPE_WARNING_MESSAGE)

  def showInfoNotification(caption: String, description: String = null) =
    showNotification(caption, description, Notification.TYPE_HUMANIZED_MESSAGE)
}

class MenuBarWrapper(mb: MenuBar) {
  def addItem(caption: String, resource: Resource) = mb.addItem(caption, resource, null)
  def addItem(caption: String) = mb.addItem(caption, null)
}

class MenuItemWrapper(mi: MenuBar#MenuItem) {
  def addItem(caption: String, resource: Resource) = mi.addItem(caption, resource, null)
  def addItem(caption: String) = mi.addItem(caption, null)

  def setCommandListener(listener: MenuBar#MenuItem => Unit) =
    mi.setCommand(new MenuBar.Command {
      def menuSelected(mi: MenuBar#MenuItem) = listener(mi)
    })

  def setCommandHandler(handler: => Unit) = setCommandListener(_ => handler)
}

class ButtonWrapper(button: Button) {

  def addClickListener(listener: Button#ClickEvent => Unit) =
    button.addListener(new Button.ClickListener {
      def buttonClick(event: Button#ClickEvent) = listener(event)
    })

  def addClickHandler(handler: => Unit) = addClickListener(_ => handler)
}

trait SingleClickListener extends Button {
  private val clickListenerRef = new AtomicReference(Option.empty[Button.ClickListener])

  override def addListener(listener: Button.ClickListener) {
    clickListenerRef.synchronized {
      for (currentListener <- clickListenerRef.getAndSet(?(listener))) {
        super.removeListener(currentListener)
      }

      super.addListener(listener)
    }
  }

  override def removeListener(listener: Button.ClickListener) {
    clickListenerRef.synchronized {
      for (currentListener <- clickListenerRef.get if currentListener eq listener) {
        super.removeListener(currentListener)
        clickListenerRef.set(None)
      }
    }
  }
}

/**
 * Must be mixed-in into a component which parent is ImcmsApplication.
 */
trait ResourceCaption extends AbstractComponent {
  
  /**
   * Returns resource string using original caption (assigned by setCaption) as a resource key or original caption if
   * corresponding resource does not exists.
   *
   * @throws java.lang.ClassCastException if application is not an instance of ImcmsApplication
   *
   * @return caption.
   */
  override def getCaption() = getApplication.resourceBundle.getString(super.getCaption)
}


/**
 * Auto-adjustable size dialog window with full margin.

 * Dialog UI is divided vertically into 2 areas - main UI and buttons bar UI.
 * Buttons bar UI takes minimal required space and main UI takes the rest.
 * 
 * By default:
 *   -buttons bar UI (buttons) are centered.
 *   -size is adjusted automatically according to its content size.
 */
class Dialog(caption: String = "") extends Window(caption) {
  protected val mainUICheck: Component => Unit = Checks.checkNoWidthOrHeightInPercentage
  protected val buttonsBarUICheck: Component => Unit = Checks.checkNoWidthOrHeightInPercentage

  protected [this] val content = new GridLayout(1, 2) with Spacing with Margin

  setContent(content)

  def mainUI = mainContent
  def mainContent = content.getComponent(0, 0)

  /** By default rejects components with width and/or height in percentage. */
  def mainUI_=(component: Component) = mainContent_=(component)
  def mainContent_=(component: Component) {
    mainUICheck(component)

    content.addComponent(component, 0, 0)
    content.setComponentAlignment(component, Alignment.TOP_LEFT)
  }

  def buttonsBarUI = buttonsBarContent
  def buttonsBarContent = content.getComponent(0, 1)

  /** By default rejects components with width and/or height in percentage. */
  def buttonsBarUI_=(component: Component) = buttonsBarContent = component
  def buttonsBarContent_=(component: Component) {
    buttonsBarUICheck(component)

    content.addComponent(component, 0, 1)
    content.setComponentAlignment(component, Alignment.TOP_CENTER)
  }

  @deprecated("prototype")
  def setMainContent[C <: Component](component: C): C = letret(component) { mainContent = _ }

  /** Exposes close method. */
  override def close() = super.close()
}


/**
 * Size (both width and height) of this dialog MUST be set explicitly.
 */
trait CustomSizeDialog extends Dialog {
  override protected val mainUICheck: Component => Unit = Function.const(Unit)

  content.setSizeFull
  content.setColumnExpandRatio(0, 1f)
  content.setRowExpandRatio(0, 1f)
}

trait YesButton extends Dialog {
  val btnYes = new Button("Yes") with SingleClickListener { setIcon(new ThemeResource("icons/16/ok.png")) }

  def wrapYesHandler(handler: => Unit) {
    btnYes addClickHandler {
      EX.allCatch.either(handler) match {
        case Right(_) => close()
        case Left(ex) => using(new java.io.StringWriter) { w =>
          ex.printStackTrace(new java.io.PrintWriter(w))
          throw ex
        }
      }
    }
  }
}

trait NoButton extends Dialog {
  val btnNo = new Button("No") with SingleClickListener { setIcon(new ThemeResource("icons/16/cancel.png")) }

  def wrapNoHandler(handler: => Unit) {
    btnNo addClickHandler {
      EX.allCatch.either(handler) match {
        case Right(_) => close()
        case Left(ex) => using(new java.io.StringWriter) { w =>
          ex.printStackTrace(new java.io.PrintWriter(w))
          throw ex
        }
      }
    }
  }
}

trait OKButton extends Dialog {
  val btnOk = new Button("Ok") with SingleClickListener { setIcon(new ThemeResource("icons/16/ok.png")) }

  wrapOkHandler {}

  /**
   * Adds Ok button listener which invokes a handler and closes dialog if there is no exception.
   */
  def wrapOkHandler(handler: => Unit) {
    btnOk addClickHandler {
      EX.allCatch.either(handler) match {
        case Right(_) => close()
        case Left(ex) => using(new java.io.StringWriter) { w =>
          ex.printStackTrace(new java.io.PrintWriter(w))
          throw ex
        }
      }
    }
  }

  def setOkHandler(handler: => Unit) = btnOk.addClickHandler(handler)
}





trait CancelButton extends Dialog {
  val btnCancel = new Button("Cancel") with SingleClickListener { setIcon(new ThemeResource("icons/16/cancel.png")) }

  wrapCancelHandler {}

  def wrapCancelHandler(handler: => Unit) {
    btnCancel addClickHandler {
      EX.allCatch.either(handler) match {
        case Right(_) => close()
        case Left(ex) => using(new java.io.StringWriter) { w =>
          ex.printStackTrace(new java.io.PrintWriter(w))
          throw ex
        }
      }
    }
  }

  def setCancelHandler(handler: => Unit) = btnCancel.addClickHandler(handler)
}

/** Empty dialog window. */
class OKDialog(caption: String = "") extends Dialog(caption) with OKButton {
  buttonsBarUI = btnOk
}

/** Empty dialog window. */
class CancelDialog(caption: String = "") extends Dialog(caption) with CancelButton {
  buttonsBarUI = btnCancel
}


/** Message dialog window. */
class MsgDialog(caption: String = "", msg: String ="") extends Dialog(caption) {
  val btnOk = new Button("Ok") { setIcon(new ThemeResource("icons/16/ok.png")) }

  val lblMessage = new Label(msg) with UndefinedSize

  mainUI = lblMessage
  buttonsBarUI = btnOk

  btnOk addClickHandler { close() }
}


/** OKCancel dialog window. */
class OkCancelDialog(caption: String = "") extends Dialog(caption) with OKButton with CancelButton {

  val lytButtons = new GridLayout(2, 1) with Spacing {
    addComponents(this, btnOk, btnCancel)

    setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT)
    setComponentAlignment(btnCancel, Alignment.MIDDLE_LEFT)
  }

  buttonsBarUI = lytButtons
}

/** YesNoCancel dialog window. */
class YesNoCancelDialog(caption: String = "") extends Dialog(caption) with YesButton with NoButton with CancelButton {

  val lytButtons = new GridLayout(3, 1) with Spacing {
    addComponents(this, btnYes, btnNo, btnCancel)

    setComponentAlignment(btnYes, Alignment.MIDDLE_RIGHT)
    setComponentAlignment(btnNo, Alignment.MIDDLE_CENTER)
    setComponentAlignment(btnCancel, Alignment.MIDDLE_LEFT)
  }

  buttonsBarUI = lytButtons
}


/** Confirmation dialog window. */
class ConfirmationDialog(caption: String, msg: String) extends OkCancelDialog(caption) {
  def this(msg: String = "") = this("Confirmation", msg)

  val lblMessage = new Label(msg) with UndefinedSize

  mainUI = lblMessage
}


/** Creates root item; root is not displayed */
@deprecated
class MenuItem(val parent: MenuItem = null, val icon: Option[Resource]=None, val handler: () => Unit = () => {}) {

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


/**
 * @param T component select item and value type 
 */
class TwinSelect[T <: AnyRef](caption: String = "") extends GridLayout(3, 1) {
  setCaption(caption)

  val btnAdd = new Button("<<")
  val btnRemove = new Button(">>")
  val lstAvailable = new ListSelect("Available") with ValueType[JCollection[T]]
  val lstChosen = new ListSelect("Chosen") with ValueType[JCollection[T]]
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

  btnAdd addClickHandler { move(lstAvailable, lstChosen) }
  btnRemove addClickHandler { move(lstChosen, lstAvailable) }

  lstAvailable addValueChangeHandler { reset() }
  lstChosen addValueChangeHandler { reset() }

  reset()

  def reset() {
    btnAdd.setEnabled(lstAvailable.value.size > 0)
    btnRemove.setEnabled(lstChosen.value.size > 0)
  }

  private [this] def move(src: ListSelect with ValueType[JCollection[T]], dest: ListSelect with ValueType[JCollection[T]]) = src.value foreach { itemId =>
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
@deprecated("prototype")
class TabSheetView extends VerticalLayout {
  val tabSheet = new TabSheet

  addComponent(tabSheet)
  setMargin(true)

  // short cut
  def addTab(c: Component) = tabSheet.addTab(c)
}

/** Vertical layout with margin, spacing and optional caption. */
class VerticalLayoutUI(caption: String = "", spacing: Boolean=true, margin: Boolean=true) extends VerticalLayout {
  setCaption(caption)
  setMargin(margin)
  setSpacing(spacing)
}

/** Horizontal layout with optional margin, spacing and caption. */
class HorizontalLayoutUI(caption: String = "", spacing: Boolean=true, margin: Boolean=false) extends HorizontalLayout {
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

  def uploadSucceeded(e: Upload.SucceededEvent) = processEvent(e)

  def uploadFailed(e: Upload.FailedEvent) = processEvent(e)

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


@deprecated("prototype code")
class TableView extends VerticalLayout {

  val table = new Table with ValueType[JInteger] {
    setSelectable(true)
    setImmediate(true)
    setPageLength(10)

    this addValueChangeHandler { resetComponents }

    tableFields foreach { addContainerProperties(this, _) }
  }

  val btnReload = new Button("Reload") {
    this addClickHandler { reloadTable }
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


/**
 * Reload button is placed under the content with right alignment.
 */
class ReloadableContentUI[T <: Component](val content: T) extends GridLayout(1,2) with Spacing {
  import com.imcode.imcms.vaadin.Theme.Icons._

  val btnReload = new Button("Reload") with LinkStyle {
    setIcon(Reload16)
  }

  addComponents(this, content, btnReload)
  setComponentAlignment(content, Alignment.TOP_LEFT)
  setComponentAlignment(btnReload, Alignment.BOTTOM_RIGHT)
}