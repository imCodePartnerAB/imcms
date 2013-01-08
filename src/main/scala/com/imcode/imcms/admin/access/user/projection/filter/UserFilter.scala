package com.imcode
package imcms
package admin.access.user.projection.filter

import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import _root_.imcode.server.user.RoleId


class UserFilter extends ImcmsServicesSupport {

  val ui: UserFilterUI = new UserFilterUI |>> { ui =>
    ui.chkText.addValueChangeHandler {
      ProjectionFilterUtil.toggle(ui, "users_projection.filter.text", ui.chkText, ui.txtText)
    }

    ui.chkRoles.addValueChangeHandler {
      ProjectionFilterUtil.toggle(ui, "users_projection.filter.roles", ui.chkRoles, ui.tcsRoles)
    }
  }


  def reset(): Unit = setValues(UserFilterValues())


  def setValues(state: UserFilterValues) {
    ui.chkText.checked = state.text.isDefined
    ui.chkRoles.checked = state.roles.isDefined
    ui.chkShowInactive.checked = state.isShowInactive

    doto(ui.chkText, ui.chkRoles, ui.chkShowInactive)(_.fireValueChange(true))

    ui.txtText.value = state.text.getOrElse("")
    ui.tcsRoles.removeAllItems()
    for (role <- imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getAllRolesExceptUsersRole) {
      ui.tcsRoles.addItem(role.getId)
      ui.tcsRoles.setItemCaption(role.getId, role.getName)
    }
    ui.tcsRoles.value = state.roles.getOrElse(Set.empty[RoleId]).asJavaCollection
  }


  def getValues = UserFilterValues(
    whenOpt(ui.chkText.checked)(ui.txtText.trim),
    whenOpt(ui.chkRoles.checked)(ui.tcsRoles.value.asScala.toSet),
    ui.chkShowInactive.checked
  )  
}
