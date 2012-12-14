package com.imcode.imcms.vaadin.ui

import com.vaadin.ui.Button

// implicit
class ButtonWrapper(button: Button) {

  def addClickListener(listener: Button#ClickEvent => Unit): Unit =
    button.addListener(new Button.ClickListener {
      def buttonClick(event: Button#ClickEvent): Unit = listener(event)
    })

  def addClickHandler(handler: => Unit): Unit = addClickListener(_ => handler)
}