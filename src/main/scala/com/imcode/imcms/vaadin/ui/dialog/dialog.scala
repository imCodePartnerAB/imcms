package com.imcode
package imcms
package vaadin.ui
package dialog

import com.imcode._
import com.vaadin.ui._
import com.vaadin.server.{Page, ThemeResource}
import com.imcode.imcms.vaadin.server._
import com.imcode.imcms.vaadin.data._
import com.vaadin.shared.ui.MarginInfo


trait Modal { this: Window =>
  setModal(true)
}

trait NonModal { this: Window =>
  setModal(false)
}

trait Resizable { this: Window =>
  setResizable(true)
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
class Dialog(caption: String = "") extends Window(caption) with Modal {
  protected val mainUISizeAssert: Component => Unit = UIAsserts.assertFixedSize
  protected val buttonsBarUISizeAssert: Component => Unit = UIAsserts.assertFixedSize
  protected val content = new GridLayout(1, 2) with Spacing with Margin

  setContent(content)
  setResizable(false)

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

  def setYesButtonHandler(handler: => Unit): Unit = Dialog.wrapButtonClickHandler(this, btnYes, handler)
}

trait NoButton { this: Dialog =>
  val btnNo = new Button("btn_no".i) with SingleClickListener |>> { _.setIcon(new ThemeResource("icons/16/cancel.png")) }

  def setNoButtonHandler(handler: => Unit): Unit = Dialog.wrapButtonClickHandler(this, btnNo, handler)
}

trait OKButton { this: Dialog =>
  val btnOk = new Button("btn_ok".i) with SingleClickListener |>> { _.setIcon(new ThemeResource("icons/16/ok.png")) }

  def setOkButtonHandler(handler: => Unit): Unit = Dialog.wrapButtonClickHandler(this, btnOk, handler)
}


trait CancelButton { this: Dialog =>
  val btnCancel = new Button("btn_cancel".i) with SingleClickListener { setIcon(new ThemeResource("icons/16/cancel.png")) }

  setCancelButtonHandler(close())

  def setCancelButtonHandler(handler: => Unit): Unit = Dialog.wrapButtonClickHandler(this, btnCancel, handler)
}

/** Empty dialog window. */
class OKDialog(caption: String = "") extends Dialog(caption) with OKButton {
  buttonsBarUI = btnOk
}

/** Empty dialog window. */
class CancelDialog(caption: String = "") extends Dialog(caption) with CancelButton {
  buttonsBarUI = btnCancel

  setCancelButtonHandler { close() }
}



trait MsgLabel { this: Dialog =>
  val lblMessage = new Label with UndefinedSize

  mainUI = lblMessage
}

/** Message dialog window. */
class MsgDialog(caption: String = "", msg: String ="") extends OKDialog(caption) with MsgLabel {
  lblMessage.value = msg

  setOkButtonHandler { close() }
}

/** OKCancel dialog window. */
class OkCancelDialog(caption: String = "") extends Dialog(caption) with OKButton with CancelButton {

  val lytButtons = new GridLayout(2, 1) with Spacing {
    this.addComponents( btnOk, btnCancel)

    setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT)
    setComponentAlignment(btnCancel, Alignment.MIDDLE_LEFT)
  }

  buttonsBarUI = lytButtons
}

/** YesNoCancel dialog window. */
class YesNoCancelDialog(caption: String = "") extends Dialog(caption) with YesButton with NoButton with CancelButton {

  val lytButtons = new GridLayout(3, 1) with Spacing {
    this.addComponents( btnYes, btnNo, btnCancel)

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

trait BottomContentMarginDialog { this: Dialog =>
  content.setMargin(new MarginInfo(false, false, true, false))
}

trait NoContentMarginDialog { this: Dialog =>
  content.setMargin(false)
}

object Dialog extends Log4jLoggerSupport {

  /**
   * Wraps button click handler: shows "server error" message in case of an error.
   */
  def wrapButtonClickHandler(dialog: Dialog, button: Button, handler: => Unit) {
    button.addClickHandler { _ =>
      try {
        handler
      } catch {
        case e: Exception =>
          Page.getCurrent.showErrorNotification(s"Server Error: ${e.getMessage}")

          logger.error("Dialog button click handler error", e)

          throw e
      }
    }
  }
}