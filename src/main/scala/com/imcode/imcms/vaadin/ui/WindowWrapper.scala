package com.imcode.imcms.vaadin.ui

import com.vaadin.ui.{Notification, Window}

/* implicit */
class WindowWrapper(window: Window) {

  def showNotification(caption: String, description: String, notificationType: Int): Unit =
    window.showNotification(caption, description, notificationType)

  def showErrorNotification(caption: String, description: String = null): Unit =
    window.showNotification(caption, description, Notification.TYPE_ERROR_MESSAGE)

  def showWarningNotification(caption: String, description: String = null): Unit =
    window.showNotification(caption, description, Notification.TYPE_WARNING_MESSAGE)

  def showInfoNotification(caption: String, description: String = null): Unit =
    window.showNotification(caption, description, Notification.TYPE_HUMANIZED_MESSAGE)
}