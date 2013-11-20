package com.imcode.imcms.vaadin.component

import com.vaadin.ui.MenuBar
import com.vaadin.server.Resource

// implicit
class MenuBarWrapper(mb: MenuBar) {
  def addItem(caption: String, resource: Resource): MenuBar#MenuItem = mb.addItem(caption, resource, null)
  def addItem(caption: String): MenuBar#MenuItem = mb.addItem(caption, null)
}