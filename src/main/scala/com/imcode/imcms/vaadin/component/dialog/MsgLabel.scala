package com.imcode.imcms.vaadin.component.dialog

import com.imcode.imcms.vaadin.component.UndefinedSize
import com.vaadin.ui.Label

trait MsgLabel { this: Dialog =>
  val lblMessage = new Label with UndefinedSize

  mainComponent = lblMessage
}