package com.imcode.imcms.vaadin.ui

import com.vaadin.ui.Component
import com.vaadin.server.Sizeable

object UIAsserts {
  def assertFixedSize(c: Component) {
    require(c.getWidthUnits != Sizeable.Unit.PERCENTAGE, "Component width must not be difined in percentage.")
    require(c.getHeightUnits != Sizeable.Unit.PERCENTAGE, "Component height must not be difined in percentage.")
  }
}