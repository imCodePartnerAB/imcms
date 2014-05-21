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
import scala.util.control.NonFatal

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
  protected val content = new GridLayout(1, 2)

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


object Dialog extends Log4jLoggerSupport {

  /**
   * Wraps button click handler: shows "system error" message in case of an error.
   */
  def wrapButtonClickHandler(dialog: Dialog, button: Button, handler: => Unit) {
    button.addClickHandler { _ =>
      try {
        handler
      } catch {
        case NonFatal(e) =>
          logger.error("Dialog button click handler error", e)
          Current.page.showUnhandledExceptionNotification(e)
      }
    }
  }
}