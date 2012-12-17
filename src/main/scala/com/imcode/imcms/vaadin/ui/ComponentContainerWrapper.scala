package com.imcode.imcms.vaadin.ui

import com.vaadin.ui.{Component, ComponentContainer}

// implicit
class ComponentContainerWrapper(componentContainer: ComponentContainer) {

  def addComponents(component: Component, components: Component*) {
    for (c <- component +: components) componentContainer.addComponent(c)
  }
}
