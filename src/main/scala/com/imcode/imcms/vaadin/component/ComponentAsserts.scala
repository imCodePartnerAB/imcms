package com.imcode.imcms.vaadin.component

import com.vaadin.ui.Component
import com.vaadin.server.Sizeable

object ComponentAsserts {
  def assertFixedSize(c: Component) {
    require(c.getWidthUnits != Sizeable.Unit.PERCENTAGE, "Component width must not be defined in percentage.")
    require(c.getHeightUnits != Sizeable.Unit.PERCENTAGE, "Component height must not be defined in percentage.")
  }
}