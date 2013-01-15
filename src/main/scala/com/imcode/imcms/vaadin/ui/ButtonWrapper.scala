package com.imcode.imcms.vaadin.ui

import com.vaadin.ui.Button

// implicit
class ButtonWrapper(button: Button) {

  def addClickHandler(handler: => Unit): Unit = button.addClickListener { _: Button.ClickEvent => handler }
}