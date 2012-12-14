package com.imcode.imcms.vaadin.ui

import com.vaadin.ui.MenuBar
import com.vaadin.terminal.Resource

// implicit
class MenuItemWrapper(mi: MenuBar#MenuItem) {
  def addItem(caption: String, resource: Resource): MenuBar#MenuItem = mi.addItem(caption, resource, null)
  def addItem(caption: String): MenuBar#MenuItem = mi.addItem(caption, null)

  def setCommandListener(listener: MenuBar#MenuItem => Unit): Unit =
    mi.setCommand(new MenuBar.Command {
      def menuSelected(mi: MenuBar#MenuItem): Unit = listener(mi)
    })

  def setCommandHandler(handler: => Unit): Unit = setCommandListener(_ => handler)
}