package com.imcode
package imcms
package vaadin

import scala.util.control.{Exception => Ex}
import scala.collection.JavaConversions._
import com.vaadin.ui._
import java.util.concurrent.atomic.AtomicReference
import com.vaadin.Application
import com.vaadin.terminal.gwt.server.WebApplicationContext
import imcode.util.Utility
import com.vaadin.terminal.{Resource, Sizeable, ThemeResource, UserError}
import com.vaadin.ui.Window.Notification
import com.imcode.imcms.security._
import java.io._
import java.util.{Collection => JCollection}
import com.vaadin.data.Container
import com.vaadin.data.Container.ItemSetChangeListener
import com.vaadin.data.Container.ItemSetChangeListener._
import com.vaadin.ui.Layout.AlignmentHandler
import imcode.server.user.UserDomainObject
import javax.servlet.http.HttpSession
import javax.servlet.ServletContext

trait ImcmsApplication extends Application {

  def user(): UserDomainObject = Utility.getLoggedOnUser(this.session())

  /**
   * If permission is granted executes an action.
   * Otherwise shows error notification and throws an exception.
   */
  def privileged[T](permission: => Permission)(action: => T) {
    permission match {
      case PermissionGranted => action
      case PermissionDenied(reason) =>
        this.showErrorNotification(reason)
        sys.error(reason)
    }
  }
}

class ApplicationWrapper(app: Application) {

  def context(): WebApplicationContext = app.getContext.asInstanceOf[WebApplicationContext]

  def session(): HttpSession = context().getHttpSession

  def servletContext(): ServletContext = session().getServletContext

  def initAndShow[W <: Window](window: W, modal: Boolean=true, resizable: Boolean=false, draggable: Boolean=true)(init: W => Unit) {
    init(window)
    window.setModal(modal)
    window.setResizable(resizable)
    window.setDraggable(draggable)
    app.getMainWindow.addWindow(window)
  }

  def show(window: Window, modal: Boolean=true, resizable: Boolean=false, draggable: Boolean=true): Unit =
    initAndShow(window, modal, resizable, draggable) { _ => }

  def showNotification(caption: String, description: String, notificationType: Int): Unit =
    app.getMainWindow.showNotification(caption, description, notificationType)

  def showErrorNotification(caption: String, description: String = null): Unit =
    showNotification(caption, description, Notification.TYPE_ERROR_MESSAGE)

  def showWarningNotification(caption: String, description: String = null): Unit =
    showNotification(caption, description, Notification.TYPE_WARNING_MESSAGE)

  def showInfoNotification(caption: String, description: String = null): Unit =
    showNotification(caption, description, Notification.TYPE_HUMANIZED_MESSAGE)
}


class WindowWrapper(window: Window) {
  def showErrorNotification(caption: String, description: String = null): Unit =
    window.showNotification(caption, description, Notification.TYPE_ERROR_MESSAGE)

  def showWarningNotification(caption: String, description: String = null): Unit =
    window.showNotification(caption, description, Notification.TYPE_WARNING_MESSAGE)

  def showInfoNotification(caption: String, description: String = null): Unit =
    window.showNotification(caption, description, Notification.TYPE_HUMANIZED_MESSAGE)
}

class MenuBarWrapper(mb: MenuBar) {
  def addItem(caption: String, resource: Resource): MenuBar#MenuItem = mb.addItem(caption, resource, null)
  def addItem(caption: String): MenuBar#MenuItem = mb.addItem(caption, null)
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

  def addClickListener(listener: Button#ClickEvent => Unit): Unit =
    button.addListener(new Button.ClickListener {
      def buttonClick(event: Button#ClickEvent) = listener(event)
    })

  def addClickHandler(handler: => Unit): Unit = addClickListener(_ => handler)
}

/**
 * Ensures this button have no more than one click listener.
 */
trait SingleClickListener extends Button {
  private val clickListenerRef = new AtomicReference(Option.empty[Button.ClickListener])

  override def addListener(listener: Button.ClickListener) {
    clickListenerRef.synchronized {
      for (currentListener <- clickListenerRef.getAndSet(listener |> opt)) {
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
 * Auto-adjustable size dialog window with full margin.

 * Dialog UI is divided vertically into 2 areas - main UI and buttons bar UI.
 * Buttons bar UI takes minimal required space and main UI takes the rest.
 * 
 * By default:
 *   -buttons bar UI (buttons) are centered.
 *   -size is adjusted automatically according to its content size.
 */
class Dialog(caption: String = "") extends Window(caption) {
  protected val mainUICheck: Component => Unit = Checks.assertFixedSize
  protected val buttonsBarUICheck: Component => Unit = Checks.assertFixedSize
  protected val content = new GridLayout(1, 2) with Spacing with Margin

  setContent(content)

  def mainUI = content.getComponent(0, 0)
  /** By default rejects components with width and/or height in percentage. */
  def mainUI_=[C <: Component](component: C): C = component |>> { component =>
    mainUICheck(component)

    content.addComponent(component, 0, 0)
    content.setComponentAlignment(component, Alignment.TOP_LEFT)
  }


  def buttonsBarUI = content.getComponent(0, 1)
  /** By default rejects components with width and/or height in percentage. */
  def buttonsBarUI_=[C <: Component](component: C): C = component |>> { component =>
    buttonsBarUICheck(component)

    content.addComponent(component, 0, 1)
    content.setComponentAlignment(component, Alignment.TOP_CENTER)
  }

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
  val btnYes = new Button("btn_yes".i) with SingleClickListener { setIcon(new ThemeResource("icons/16/ok.png")) }

  def wrapYesHandler(handler: => Unit) {
    btnYes addClickHandler {
      Ex.allCatch.either(handler) match {
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
  val btnNo = new Button("btn_no".i) with SingleClickListener { setIcon(new ThemeResource("icons/16/cancel.png")) }

  def wrapNoHandler(handler: => Unit) {
    btnNo addClickHandler {
      Ex.allCatch.either(handler) match {
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
  val btnOk = new Button("btn_ok".i) with SingleClickListener { setIcon(new ThemeResource("icons/16/ok.png")) }

  wrapOkHandler {}

  /**
   * Wraps Ok handler: invokes original handler and closes dialog if there is no exception.
   */
  def wrapOkHandler(handler: => Unit) {
    setOkHandler {
      Ex.allCatch.either(handler) match {
        case Right(_) => close()
        case Left(ex) => using(new java.io.StringWriter) { w =>
          ex.printStackTrace(new java.io.PrintWriter(w))
          getApplication.showErrorNotification("Unexpected error: %s".format(ex.getMessage), w.toString)
          throw ex
        }
      }
    }
  }

  def setOkHandler(handler: => Unit) = btnOk.addClickHandler(handler)

  // todo: ??? setOKEitherHandler
  //
  //  def setOkEitherHandler(handler: => Either[Option[String], Option[String]]) {
  //    handler match {
  //      case Left
  //    }
  //  }
  // Left, Right: Option[Info|Warning|Error]
  // if one of them - show notification/ or msg dialog
  // close if close is set to true:
  // sealed trait HandlerStatus {
  //
  // }
  // case class StatusInfo(autoClose: Boolean = true, show: notification | dialog)
  // case class StatusWarning(autoClose: Boolean = false)
  // case class StatusError(autoClose: Boolean = false)
}





trait CancelButton extends Dialog {
  val btnCancel = new Button("btn_cancel".i) with SingleClickListener { setIcon(new ThemeResource("icons/16/cancel.png")) }

  wrapCancelHandler {}

  def wrapCancelHandler(handler: => Unit) {
    btnCancel addClickHandler {
      Ex.allCatch.either(handler) match {
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



trait MsgLabel { this: Dialog =>
  val lblMessage = new Label with UndefinedSize

  mainUI = lblMessage
}

/** Message dialog window. */
class MsgDialog(caption: String = "", msg: String ="") extends OKDialog(caption) with MsgLabel {
  lblMessage.value = msg
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
  def this(msg: String = "") = this("dlg.confirm.title".i, msg)

  val lblMessage = new Label(msg) with UndefinedSize

  mainUI = lblMessage
}


/** Information dialog window. */
class InformationDialog(msg: String = "") extends MsgDialog("dlg.info.title".i, msg)

/** Error dialog window. */
class ErrorDialog(msg: String = "") extends MsgDialog("dlg.err.title".i, msg)

/** Error dialog window. */
class OkCancelErrorDialog(msg: String = "") extends OkCancelDialog("dlg.err.title".i) with MsgLabel {
  lblMessage.value = msg
}


/** Tree item descriptor */
class TreeMenuItem(val id: String = null, val icon: Resource = null) {

  val children: Seq[TreeMenuItem] = {
    val isMenuItemType: Class[_] => Boolean = classOf[TreeMenuItem].isAssignableFrom

    getClass.getDeclaredMethods
      .filter(_.getReturnType |> isMenuItemType)
      .sortBy(_.getAnnotation(classOf[OrderedMethod]) |> opt map(_.value()) getOrElse 0)
      .map(_.invoke(this).asInstanceOf[TreeMenuItem])
  }
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

  doto(lstAvailable, lstChosen) { l =>
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
    src.getItemCaption(itemId) |> { itemCaption =>
      addItem(dest, itemId, itemCaption)
    }

    src.removeItem(itemId)
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

  def setListRows(count: Int) = doto(lstAvailable, lstChosen) { _ setRows count }
  def setListColumns(count: Int) = doto(lstAvailable, lstChosen) { _ setColumns count }
}


/** Vertical layout with margin, spacing and optional caption. */
class VerticalLayoutUI(caption: String = null, spacing: Boolean=true, margin: Boolean=true) extends VerticalLayout {
  setCaption(caption)
  setMargin(margin)
  setSpacing(spacing)
}

/** Horizontal layout with optional margin, spacing and caption. */
class HorizontalLayoutUI(caption: String = null, spacing: Boolean=true, margin: Boolean=false, defaultAlignment: Alignment=Alignment.TOP_LEFT) extends HorizontalLayout {
  setCaption(caption)
  setMargin(margin)
  setSpacing(spacing)

  override def addComponent(c: Component) {
    super.addComponent(c)
    setComponentAlignment(c, defaultAlignment)
  }
}

trait DefaultAlignment extends ComponentContainer with AlignmentHandler {
  protected def defaultAlignment: Alignment

  abstract override def addComponent(c: Component) {
    super.addComponent(c)
    setComponentAlignment(c, defaultAlignment)
  }
}

trait LeftBottomAlignment extends DefaultAlignment {
  protected def defaultAlignment = Alignment.BOTTOM_LEFT
}

trait MiddleLeftAlignment extends DefaultAlignment {
  protected def defaultAlignment = Alignment.MIDDLE_LEFT
}


trait NoChildrenAllowed extends Tree {
  override def addItem(itemId: AnyRef) = super.addItem(itemId) |>> { _ =>
    setChildrenAllowed(itemId, false)
  }
}


/**
 * Reload button is placed under the content with right alignment.
 */
class ReloadableContentUI[T <: Component](val content: T) extends GridLayout(1,2) with Spacing {
  import com.imcode.imcms.vaadin.Theme.Icon._

  val btnReload = new Button("Reload") with LinkStyle {
    setIcon(Reload16)
  }

  addComponents(this, content, btnReload)
  setComponentAlignment(content, Alignment.TOP_LEFT)
  setComponentAlignment(btnReload, Alignment.BOTTOM_RIGHT)
}

trait ContainerItemSetChangeNotifier extends Container.ItemSetChangeNotifier { container: Container =>

  private var listeners = Set.empty[ItemSetChangeListener]

  def removeListener(listener: ItemSetChangeListener) {
    listeners -= listener
  }

  def addListener(listener: ItemSetChangeListener) {
    listeners += listener
  }

  protected def notifyItemSetChanged() {
    val event = new Container.ItemSetChangeEvent {
      def getContainer = container
    }

    listeners foreach { _ containerItemSetChange event }
  }
}