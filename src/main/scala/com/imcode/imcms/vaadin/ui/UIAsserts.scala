package com.imcode.imcms.vaadin.ui

import com.vaadin.ui.Component
import com.vaadin.terminal.Sizeable

object UIAsserts {
  def assertFixedSize(c: Component) {
    require(c.getWidthUnits != Sizeable.UNITS_PERCENTAGE, "Component width must not be difined in percentage.")
    require(c.getHeightUnits != Sizeable.UNITS_PERCENTAGE, "Component height must not be difined in percentage.")
  }
}