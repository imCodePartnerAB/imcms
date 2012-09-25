package com.imcode.imcms.vaadin.ui

import com.vaadin.ui.VerticalLayout

/** Vertical layout with margin, spacing and optional caption. */
@deprecated("prototype")
class VerticalLayoutUI(caption: String = null, spacing: Boolean=true, margin: Boolean=true) extends VerticalLayout {
  setCaption(caption)
  setMargin(margin)
  setSpacing(spacing)
}