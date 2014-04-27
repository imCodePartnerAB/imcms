package com.imcode.imcms.vaadin.component.dialog

import com.imcode.imcms.vaadin.component.SingleClickListener
import com.vaadin.server.ThemeResource
import com.vaadin.ui.Button

trait OKButton { this: Dialog =>
  val btnOk = new Button("btn_caption.ok".i) with SingleClickListener |>> { _.setIcon(new ThemeResource("icons/16/ok.png")) }

  def setOkButtonHandler(handler: => Unit): Unit = Dialog.wrapButtonClickHandler(this, btnOk, handler)
}
