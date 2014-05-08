package com.imcode
package imcms
package vaadin.component.dialog

import com.imcode.imcms.vaadin.component.SingleClickListener
import com.vaadin.server.ThemeResource
import com.vaadin.ui.Button

trait CancelButton { this: Dialog =>
  val btnCancel = new Button("btn_caption.cancel".i) with SingleClickListener { setIcon(new ThemeResource("icons/16/cancel.png")) }

  setCancelButtonHandler(close())

  def setCancelButtonHandler(handler: => Unit): Unit = Dialog.wrapButtonClickHandler(this, btnCancel, handler)
}
