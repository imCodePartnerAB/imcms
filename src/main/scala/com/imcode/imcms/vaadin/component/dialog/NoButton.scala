package com.imcode.imcms.vaadin.component.dialog

import com.imcode.imcms.vaadin.component.SingleClickListener
import com.vaadin.server.ThemeResource
import com.vaadin.ui.Button

trait NoButton { this: Dialog =>
  val btnNo = new Button("btn_caption.no".i) with SingleClickListener |>> { _.setIcon(new ThemeResource("icons/16/cancel.png")) }

  def setNoButtonHandler(handler: => Unit): Unit = Dialog.wrapButtonClickHandler(this, btnNo, handler)
}
