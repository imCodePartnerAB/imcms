package com.imcode
package imcms
package vaadin

import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.util.Utility
import com.imcode.imcms.security.{PermissionDenied, PermissionGranted, Permission}
import com.imcode.imcms.vaadin.ui._

/* implicit */
trait ImcmsApplication extends com.vaadin.Application {

  def imcmsUser(): UserDomainObject = Utility.getLoggedOnUser(this.session)

  def imcmsDocUrl(docId: DocId) = this.resourceUrl(docId.toString)

  def imcmsDocUrl(docAlias: String) = this.resourceUrl(docAlias)

  /**
   * If permission is granted executes an action.
   * Otherwise shows error notification and throws an exception.
   */
  def privileged[T](permission: => Permission)(action: => T) {
    permission match {
      case PermissionGranted => action
      case PermissionDenied(reason) =>
        getMainWindow.showErrorNotification(reason)
        sys.error(reason)
    }
  }
}