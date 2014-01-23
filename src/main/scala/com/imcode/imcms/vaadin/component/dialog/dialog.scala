package com.imcode
package imcms
package vaadin.component
package dialog

import com.imcode.imcms.vaadin.{Editor, Current}
import com.imcode._
import com.vaadin.ui._
import com.vaadin.server.ThemeResource
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

 * Dialog widget is divided vertically into 2 areas - main widget and footer (buttons bar) widget.
 * Footer widget takes minimal required space and main widget takes the rest.
 *
 * By default:
 *   -footer widget (buttons) are centered.
 *   -size is adjusted automatically according to its content size.
 */
class Dialog(caption: String = "") extends Window(caption) with Modal {
  protected val mainComponentSizeAssert: Component => Unit = ComponentAsserts.assertSizeNotDefinedInPersentage
  protected val footerComponentSizeAssert: Component => Unit = ComponentAsserts.assertSizeNotDefinedInPersentage
  protected val content = new GridLayout(1, 2) with Spacing

  setContent(content)
  setResizable(false)

  def mainComponent: Component = content.getComponent(0, 0)
  def mainComponent_=(component: Component) {
    mainComponentSizeAssert(component)

    content.addComponent(component, 0, 0)
    content.setComponentAlignment(component, Alignment.TOP_LEFT)
  }


  def footerComponent: Component = content.getComponent(0, 1)
  def footerComponent_=(component: Component) {
    footerComponentSizeAssert(component)

    content.addComponent(component, 0, 1)
    content.setComponentAlignment(component, Alignment.MIDDLE_CENTER)
  }
}



/**
 * Size (both width and height) of this dialog MUST be set explicitly.
 */
trait CustomSizeDialog extends Dialog {
  override protected val mainComponentSizeAssert: Component => Unit = Function.const(Unit)

  content.setSizeFull()
  content.setColumnExpandRatio(0, 1f)
  content.setRowExpandRatio(0, 1f)
}

trait YesButton { this: Dialog =>
  val btnYes = new Button("btn_caption.yes".i) with SingleClickListener |>> { _.setIcon(new ThemeResource("icons/16/ok.png")) }

  def setYesButtonHandler(handler: => Unit): Unit = Dialog.wrapButtonClickHandler(this, btnYes, handler)
}

trait NoButton { this: Dialog =>
  val btnNo = new Button("btn_caption.no".i) with SingleClickListener |>> { _.setIcon(new ThemeResource("icons/16/cancel.png")) }

  def setNoButtonHandler(handler: => Unit): Unit = Dialog.wrapButtonClickHandler(this, btnNo, handler)
}

trait OKButton { this: Dialog =>
  val btnOk = new Button("btn_caption.ok".i) with SingleClickListener |>> { _.setIcon(new ThemeResource("icons/16/ok.png")) }

  def setOkButtonHandler(handler: => Unit): Unit = Dialog.wrapButtonClickHandler(this, btnOk, handler)
}

trait OKCaptionIsSave { this: OKButton =>
  btnOk.setCaption("btn_caption.save".i)
}


trait CancelButton { this: Dialog =>
  val btnCancel = new Button("btn_caption.cancel".i) with SingleClickListener { setIcon(new ThemeResource("icons/16/cancel.png")) }

  setCancelButtonHandler(close())

  def setCancelButtonHandler(handler: => Unit): Unit = Dialog.wrapButtonClickHandler(this, btnCancel, handler)
}

/** Empty dialog window. */
class OKDialog(caption: String = "") extends Dialog(caption) with OKButton {
  footerComponent = btnOk
}

/** Empty dialog window. */
class CancelDialog(caption: String = "") extends Dialog(caption) with CancelButton {
  footerComponent = btnCancel

  setCancelButtonHandler { close() }
}



trait MsgLabel { this: Dialog =>
  val lblMessage = new Label with UndefinedSize

  mainComponent = lblMessage
}

/** Message dialog window. */
class MsgDialog(caption: String = "", msg: String ="") extends OKDialog(caption) with MsgLabel {
  lblMessage.value = msg

  setOkButtonHandler { close() }
}

/** OKCancel dialog window. */
class OkCancelDialog(caption: String = "") extends Dialog(caption) with OKButton with CancelButton {

  val lytButtons = new GridLayout(2, 1) with Spacing |>> { lyt =>
    lyt.addComponents( btnOk, btnCancel)

    lyt.setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT)
    lyt.setComponentAlignment(btnCancel, Alignment.MIDDLE_LEFT)

    lyt.addStyleName("dialog-buttons")
  }

  footerComponent = lytButtons
}



/** YesNoCancel dialog window. */
class YesNoCancelDialog(caption: String = "") extends Dialog(caption) with YesButton with NoButton with CancelButton {

  val lytButtons = new GridLayout(3, 1) with Spacing {
    addComponents( btnYes, btnNo, btnCancel)

    setComponentAlignment(btnYes, Alignment.MIDDLE_RIGHT)
    setComponentAlignment(btnNo, Alignment.MIDDLE_CENTER)
    setComponentAlignment(btnCancel, Alignment.MIDDLE_LEFT)
  }

  footerComponent = lytButtons
}


/** Confirmation dialog window. */
class ConfirmationDialog(caption: String, msg: String) extends OkCancelDialog(caption) {
  def this(msg: String = "") = this("dlg_title.confirmaton".i, msg)

  val lblMessage = new Label(msg) with UndefinedSize

  mainComponent = lblMessage
}


/** Information dialog window. */
class InformationDialog(msg: String = "") extends MsgDialog("dlg_title.info".i, msg)

/** Error dialog window. */
class ErrorDialog(msg: String = "") extends MsgDialog("dlg_title.error".i, msg)

/** Error dialog window. */
class OkCancelErrorDialog(msg: String = "") extends OkCancelDialog("dlg_title.error".i) with MsgLabel {
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
   * Wraps button click handler: shows "system error" message in case of an error.
   */
  def wrapButtonClickHandler(dialog: Dialog, button: Button, handler: => Unit) {
    button.addClickHandler { _ =>
      try {
        handler
      } catch {
        case e: Exception =>
          logger.error("Dialog button click handler error", e)
          Current.page.showUnhandledExceptionNotification(e)
      }
    }
  }

  def asOKEditorDialog(dialog: Dialog with OKButton, editor: Editor)(validDataHandler: editor.Data => Unit) {
    dialog.mainComponent = editor.view
    dialog.setOkButtonHandler {
      editor.collectValues() match {
        case Left(errors) =>
          Current.page.showConstraintViolationNotification(errors)

        case Right(values) =>
          validDataHandler(values)
          dialog.close()
      }
    }
  }
}