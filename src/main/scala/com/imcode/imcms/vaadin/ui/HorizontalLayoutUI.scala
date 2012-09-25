package com.imcode.imcms.vaadin.ui

import com.vaadin.ui.{Component, HorizontalLayout, Alignment}

/** Horizontal layout with optional margin, spacing and caption. */
@deprecated("prototype")
class HorizontalLayoutUI(caption: String = null, spacing: Boolean=true, margin: Boolean=false, defaultAlignment: Alignment=Alignment.TOP_LEFT) extends HorizontalLayout {
  setCaption(caption)
  setMargin(margin)
  setSpacing(spacing)

  override def addComponent(c: Component) {
    super.addComponent(c)
    setComponentAlignment(c, defaultAlignment)
  }
}