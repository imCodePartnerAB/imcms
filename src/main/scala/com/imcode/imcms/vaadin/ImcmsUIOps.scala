package com.imcode
package imcms
package vaadin

import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.util.Utility
import com.imcode.imcms.security.{PermissionDenied, PermissionGranted, Permission}
import com.imcode.imcms.vaadin.server._
import com.vaadin.ui.UI

/* implicit */
class ImcmsUIOps(ui: UI) {

  // todo: fix
  def imcmsUser: UserDomainObject = Utility.getLoggedOnUser(ui.session)

  def imcmsDocUrl(docId: DocId) = ui.resourceUrl(docId.toString)

  def imcmsDocUrl(docAlias: String) = ui.resourceUrl(docAlias)

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