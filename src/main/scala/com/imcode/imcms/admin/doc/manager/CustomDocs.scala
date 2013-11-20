package com.imcode.imcms.admin.doc.manager

import imcode.server.user.UserDomainObject
import com.imcode.imcms.admin.doc.projection.DocsProjection
import com.vaadin.event.Action
import scala.Array
import com.vaadin.ui.Table

/**
 * Custom docs.
 */
class CustomDocs(user: UserDomainObject) {
  val projection = new DocsProjection(user)
  val widget = new CustomDocsWidget(projection.widget)

  projection.docsWidget.addActionHandler(new Action.Handler {

    def getActions(target: AnyRef, sender: AnyRef) = Array(Actions.ExcludeFromSelection, Actions.Delete)

    def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
      action match {
        case Actions.ExcludeFromSelection => sender.asInstanceOf[Table].removeItem(target)
        case _ =>
      }
  })
}
