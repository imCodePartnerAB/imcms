package com.imcode.imcms.vaadin.ui

import scala.annotation.tailrec
import com.vaadin.ui.{Window, Component}

/* implicit */
class ComponentWrapper(component: Component) {
  def rootWindow: Window = {
    @tailrec
    def findRootWindow(window: Window): Window = window.getParent match {
      case null => window
      case parent => findRootWindow(parent)
    }

    component.getWindow match {
      case null => null
      case window => findRootWindow(window)
    }
  }
}