package com.imcode.imcms.vaadin.ui

import com.vaadin.ui.{Alignment, Button, GridLayout, Component}
import com.imcode.imcms.vaadin._
import com.imcode.imcms.vaadin.Theme

/**
 * Reload button is placed under the content with right alignment.
 */
@deprecated
class ReloadableContentUI[T <: Component](val content: T) extends GridLayout(1,2) with Spacing {
  import Theme.Icon._

  val btnReload = new Button("Reload") with LinkStyle {
    setIcon(Reload16)
  }

  addComponentsTo(this, content, btnReload)
  setComponentAlignment(content, Alignment.TOP_LEFT)
  setComponentAlignment(btnReload, Alignment.BOTTOM_RIGHT)
}