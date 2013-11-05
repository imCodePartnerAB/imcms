package com.imcode.imcms.vaadin.ui

import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.util.Utility
import com.imcode.imcms.security.{PermissionDenied, PermissionGranted, Permission}
import com.imcode.imcms.vaadin.server._
import com.vaadin.ui.UI
import com.imcode.imcms._
import com.imcode.imcms.security.PermissionDenied

/* implicit */
class ImcmsUIOps(ui: UI) {

  def imcmsUser: UserDomainObject = Utility.getLoggedOnUser(ui.session)

  def imcmsDocUrl(metaId: MetaId) = ui.resourceUrl(metaId.toString)

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