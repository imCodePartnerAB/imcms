package com.imcode
package imcms.vaadin.component

import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.util.Utility

import com.imcode.imcms._
import com.imcode.imcms.vaadin.server._
import com.imcode.imcms.security.{PermissionDenied, PermissionGranted, Permission}
import com.imcode.imcms.vaadin.Current

import java.net.URL
import java.util.concurrent.Future

/* implicit */
class UIWrapper(ui: com.vaadin.ui.UI) {

  def resourceUrl(resourcePath: String): URL = ui.getPage.getLocation |> { url =>
    new URL(url.getScheme, url.getHost, url.getPort, s"${Current.contextPath}/$resourcePath")
  }

  def withSessionLock(block: => Unit): Future[Void] = ui.access(
    new Runnable {
      override def run() {
        block
      }
    }
  )

  def imcmsDocUrl(docId: DocId) = ui.resourceUrl(docId.toString)

  def imcmsDocUrl(alias: String) = ui.resourceUrl(alias)

  /**
   * If permission is granted executes an action.
   * Otherwise shows error notification and throws an exception.
   */
  def privileged[T](permission: => Permission)(action: => T) {
    permission match {
      case PermissionGranted => action
      case PermissionDenied(reason) =>
        ui.getPage.showErrorNotification(reason)
        sys.error(reason)
    }
  }

}