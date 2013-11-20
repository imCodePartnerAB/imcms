package com.imcode.imcms.vaadin.component

import com.vaadin.ui.Button

// implicit
class ButtonWrapper(button: Button) {

  def addClickHandler(handler: Button.ClickEvent => Unit): Unit = button.addClickListener(
    new Button.ClickListener {
      def buttonClick(event: Button.ClickEvent): Unit = handler(event)
    }
  )
}