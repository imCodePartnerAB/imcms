package com.imcode
package imcms
package vaadin.component.dialog

import com.imcode.imcms.vaadin.component.SingleClickListener
import com.vaadin.server.ThemeResource
import com.vaadin.ui.Button

trait YesButton { this: Dialog =>
  val btnYes = new Button("btn_caption.yes".i) with SingleClickListener |>> { _.setIcon(new ThemeResource("icons/16/ok.png")) }

  def setYesButtonHandler(handler: => Unit): Unit = Dialog.wrapButtonClickHandler(this, btnYes, handler)
}
