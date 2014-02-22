package com.imcode
package imcms
package admin
package access.user.select

import com.imcode.imcms.vaadin.component.dialog.{Resizable, CustomSizeDialog, OkCancelDialog}
import com.imcode.imcms.admin.access.user.projection.UsersProjection

import com.imcode.imcms.vaadin.component._

abstract class UserSelectDialog(caption: String, multiSelect: Boolean) extends OkCancelDialog(caption) with CustomSizeDialog with Resizable {
  val projection = new UsersProjection(multiSelect) |>> {
    p =>
      p.usersView.setColumnCollapsingAllowed(true)

      Seq("users_projection.container_property.id", "users_projection.container_property.login",
        "users_projection.container_property.first_name", "users_projection.container_property.last_name",
        "users_projection.container_property.is_superadmin", "users_projection.container_property.is_inactive", ""
      ).foreach(id => p.usersView.setColumnCollapsible(id, false))

      Seq("users_projection.container_property.email", "users_projection.container_property.email").foreach {
        id =>
          p.usersView.setColumnCollapsible(id, true)
          p.usersView.setColumnCollapsed(id, true)
      }
  }

  mainComponent = projection.view

  projection.listen(selection => btnOk.setEnabled(selection.nonEmpty))
  projection.notifyListeners()

  this.setSize(600, 500)
}