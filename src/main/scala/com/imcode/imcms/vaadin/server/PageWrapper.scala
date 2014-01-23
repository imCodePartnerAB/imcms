package com.imcode
package imcms.vaadin.server

import com.vaadin.ui.Notification
import com.vaadin.server.Page

/* implicit */
class PageWrapper(page: Page) {

  private def showNotification(caption: String, description: String, notificationType: Notification.Type, htmlContentAllowed: Boolean): Unit =
    new Notification(caption, description, notificationType, htmlContentAllowed).show(page)

  def showErrorNotification(caption: String, description: String = null): Unit =
    showNotification(caption, description, Notification.Type.ERROR_MESSAGE, htmlContentAllowed = false)

  def showWarningNotification(caption: String, description: String = null): Unit =
    showNotification(caption, description, Notification.Type.WARNING_MESSAGE, htmlContentAllowed = false)

  def showInfoNotification(caption: String, description: String = null): Unit =
    showNotification(caption, description, Notification.Type.HUMANIZED_MESSAGE, htmlContentAllowed = false)

  def showHtmlErrorNotification(caption: String, description: String = null): Unit =
    showNotification(caption, description, Notification.Type.ERROR_MESSAGE, htmlContentAllowed = true)

  def showHtmlWarningNotification(caption: String, description: String = null): Unit =
    showNotification(caption, description, Notification.Type.WARNING_MESSAGE, htmlContentAllowed = true)

  def showHtmlInfoNotification(caption: String, description: String = null): Unit =
    showNotification(caption, description, Notification.Type.HUMANIZED_MESSAGE, htmlContentAllowed = true)

  def showUnhandledExceptionNotification(exception: Exception): Unit = showHtmlErrorNotification(
    s"""
        SYSTEM ERROR
        <div style='margin:0; padding:0; overflow:hidden; text-overflow:ellipsis;'>
            ${exception.getMessage}
        </div>
     """,
    s"""
        <div style="height:500px; overflow:auto; border: 1px solid white">
            ${exception.getStackTraceString}
        </div>
     """
  )

  def showConstraintViolationNotification(violations: Seq[String]): Unit = showHtmlWarningNotification(
    "Validation error(s)",
    "<ul>" + violations.iterator.map(v => s"<li>$v</li>").mkString + "</ul>"
  )
}