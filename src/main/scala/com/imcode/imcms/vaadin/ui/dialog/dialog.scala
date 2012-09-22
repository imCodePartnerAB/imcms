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
  override def close(): Unit = super.close()
}


/**
 * Size (both width and height) of this dialog MUST be set explicitly.
 */
trait CustomSizeDialog extends Dialog {
  override protected val mainUICheck: Component => Unit = Function.const(Unit)

  content.setSizeFull()
  content.setColumnExpandRatio(0, 1f)
  content.setRowExpandRatio(0, 1f)
}

trait YesButton { this: Dialog =>
  val btnYes = new Button("btn_yes".i) with SingleClickListener |>> { _.setIcon(new ThemeResource("icons/16/ok.png")) }

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

trait NoButton { this: Dialog =>
  val btnNo = new Button("btn_no".i) with SingleClickListener |>> { _.setIcon(new ThemeResource("icons/16/cancel.png")) }

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

trait OKButton { this: Dialog =>
  val btnOk = new Button("btn_ok".i) with SingleClickListener |>> { _.setIcon(new ThemeResource("icons/16/ok.png")) }

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





trait CancelButton { this: Dialog =>
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