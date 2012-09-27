package com.imcode
package imcms
package vaadin.ui
package dialog

import com.imcode._
import com.imcode.imcms.vaadin._
import com.vaadin.terminal.ThemeResource
import com.vaadin.ui._
//import com.vaadin.ui.AbstractComponent._
//import com.vaadin.ui.GridLayout._
//import com.vaadin.ui.Panel._
//import com.vaadin.ui.Window._
import scala.util.control.{Exception => Ex}

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
  protected val mainUISizeAssert: Component => Unit = Asserts.assertFixedSize
  protected val buttonsBarUISizeAssert: Component => Unit = Asserts.assertFixedSize
  protected val content = new GridLayout(1, 2) with Spacing with Margin

  setContent(content)

  def mainUI = content.getComponent(0, 0)
  def mainUI_=(component: Component) {
    mainUISizeAssert(component)

    content.addComponent(component, 0, 0)
    content.setComponentAlignment(component, Alignment.TOP_LEFT)
  }


  def buttonsBarUI = content.getComponent(0, 1)
  def buttonsBarUI_=(component: Component) {
    buttonsBarUISizeAssert(component)

    content.addComponent(component, 0, 1)
    content.setComponentAlignment(component, Alignment.TOP_CENTER)
  }

  /** Exposes close method. */
  override def close(): Unit = super.close()
}


/**
 * Size (both width and height) of this dialog MUST be set explicitly.
 */
trait CustomSizeDialog extends Dialog {
  override protected val mainUISizeAssert: Component => Unit = Function.const(Unit)

  content.setSizeFull()
  content.setColumnExpandRatio(0, 1f)
  content.setRowExpandRatio(0, 1f)
}

trait YesButton { this: Dialog =>
  val btnYes = new Button("btn_yes".i) with SingleClickListener |>> { _.setIcon(new ThemeResource("icons/16/ok.png")) }

  def setYesHandler(handler: => Unit): Unit = DialogUtil.wrapButtonClickHandler(this, btnYes, handler)
}

trait NoButton { this: Dialog =>
  val btnNo = new Button("btn_no".i) with SingleClickListener |>> { _.setIcon(new ThemeResource("icons/16/cancel.png")) }

  def setNoHandler(handler: => Unit): Unit = DialogUtil.wrapButtonClickHandler(this, btnNo, handler)
}

trait OKButton { this: Dialog =>
  val btnOk = new Button("btn_ok".i) with SingleClickListener |>> { _.setIcon(new ThemeResource("icons/16/ok.png")) }

  def setOkHandler(handler: => Unit): Unit = DialogUtil.wrapButtonClickHandler(this, btnOk, handler)
  def setOkCustomHandler(handler: => Unit): Unit = btnOk.addClickHandler(handler)
}


trait CancelButton { this: Dialog =>
  val btnCancel = new Button("btn_cancel".i) with SingleClickListener { setIcon(new ThemeResource("icons/16/cancel.png")) }

  def setCancelHandler(handler: => Unit): Unit = DialogUtil.wrapButtonClickHandler(this, btnCancel, handler)
  def setCancelCustomHandler(handler: => Unit): Unit = btnCancel.addClickHandler(handler)
}

/** Empty dialog window. */
class OKDialog(caption: String = "") extends Dialog(caption) with OKButton {
  buttonsBarUI = btnOk
}

/** Empty dialog window. */
class CancelDialog(caption: String = "") extends Dialog(caption) with CancelButton {
  buttonsBarUI = btnCancel

  setCancelCustomHandler { close() }
}



trait MsgLabel { this: Dialog =>
  val lblMessage = new Label with UndefinedSize

  mainUI = lblMessage
}

/** Message dialog window. */
class MsgDialog(caption: String = "", msg: String ="") extends OKDialog(caption) with MsgLabel {
  lblMessage.value = msg

  setOkCustomHandler { close() }
}

/** OKCancel dialog window. */
class OkCancelDialog(caption: String = "") extends Dialog(caption) with OKButton with CancelButton {

  val lytButtons = new GridLayout(2, 1) with Spacing {
    addComponentsTo(this, btnOk, btnCancel)

    setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT)
    setComponentAlignment(btnCancel, Alignment.MIDDLE_LEFT)
  }

  buttonsBarUI = lytButtons
}

/** YesNoCancel dialog window. */
class YesNoCancelDialog(caption: String = "") extends Dialog(caption) with YesButton with NoButton with CancelButton {

  val lytButtons = new GridLayout(3, 1) with Spacing {
    addComponentsTo(this, btnYes, btnNo, btnCancel)

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

trait BottomMarginDialog { this: Dialog =>
  content.setMargin(false, false, true, false)
}

trait NoMarginDialog { this: Dialog =>
  content.setMargin(false)
}

object DialogUtil extends Log4jLoggerSupport {

  /**
   * Wraps button click handler: invokes original handler and closes dialog if there is no exception.
   */
  def wrapButtonClickHandler(dialog: Dialog, button: Button, handler: => Unit) {
    button.addClickHandler {
      try {
        handler
        dialog.close()
      } catch {
        case e =>
          using(new java.io.StringWriter) { w =>
            e.printStackTrace(new java.io.PrintWriter(w))
            dialog.topWindow.showErrorNotification("Unexpected error: %s".format(e.getMessage), w.toString)
          }

          logger.error("Dialog button click hander error", e)

          throw e
      }
    }
  }
}