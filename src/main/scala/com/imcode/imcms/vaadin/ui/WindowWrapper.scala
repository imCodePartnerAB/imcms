package com.imcode.imcms.vaadin.ui

import com.vaadin.ui.Window
import com.vaadin.ui.Window.Notification

/* implicit */
class WindowWrapper(window: Window) {

  def initAndShow[W <: Window](childWindow: W, modal: Boolean=true, resizable: Boolean=false, draggable: Boolean=true)(init: W => Unit) {
    init(childWindow)
    childWindow.setModal(modal)
    childWindow.setResizable(resizable)
    childWindow.setDraggable(draggable)
    window.addWindow(childWindow)
  }

  def show(window: Window, modal: Boolean=true, resizable: Boolean=false, draggable: Boolean=true): Unit =
    initAndShow(window, modal, resizable, draggable) { _ => }

  def showNotification(caption: String, description: String, notificationType: Int): Unit =
    window.showNotification(caption, description, notificationType)

  def showErrorNotification(caption: String, description: String = null): Unit =
    window.showNotification(caption, description, Notification.TYPE_ERROR_MESSAGE)

  def showWarningNotification(caption: String, description: String = null): Unit =
    window.showNotification(caption, description, Notification.TYPE_WARNING_MESSAGE)

  def showInfoNotification(caption: String, description: String = null): Unit =
    window.showNotification(caption, description, Notification.TYPE_HUMANIZED_MESSAGE)
}