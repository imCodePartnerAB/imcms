package com.imcode.imcms.vaadin.ui

import com.vaadin.ui.{CustomLayout, Component}

class CustomLayoutWrapper(customLayout: CustomLayout) {

  def addNamedComponents(component: (String, Component), components: (String, Component)*) {
    for ((location, component) <- component +: components) customLayout.addComponent(component, location)
  }
}