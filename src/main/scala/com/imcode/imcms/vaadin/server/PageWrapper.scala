package com.imcode
package imcms.vaadin.server

import com.vaadin.ui.Notification
import com.vaadin.server.Page

/* implicit */
class PageWrapper(page: Page) {

  private def showNotification(caption: String, description: String, notificationType: Notification.Type): Unit =
    new Notification(caption, description, notificationType).show(page)

  def showErrorNotification(caption: String, description: String = null): Unit =
    showNotification(caption, description, Notification.Type.ERROR_MESSAGE)

  def showWarningNotification(caption: String, description: String = null): Unit =
    showNotification(caption, description, Notification.Type.WARNING_MESSAGE)

  def showInfoNotification(caption: String, description: String = null): Unit =
    showNotification(caption, description, Notification.Type.HUMANIZED_MESSAGE)
}