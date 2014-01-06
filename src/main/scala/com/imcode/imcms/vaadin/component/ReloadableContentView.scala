package com.imcode.imcms.vaadin.component

import com.vaadin.ui.{Alignment, Button, GridLayout, Component}

/**
 * Reload button is placed under the content with right alignment.
 */
@deprecated
class ReloadableContentView[T <: Component](val content: T) extends GridLayout(1,2) with Spacing {
  import Theme.Icon._

  val btnReload = new Button("Reload") with LinkStyle {
    setIcon(Reload16)
  }

  this.addComponents( content, btnReload)
  setComponentAlignment(content, Alignment.TOP_LEFT)
  setComponentAlignment(btnReload, Alignment.BOTTOM_RIGHT)
}