package com.imcode
package imcms
package admin.access.user.projection.filter

import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import _root_.imcode.server.user.RoleId


class UserFilter extends ImcmsServicesSupport {

  val widget: UserFilterWidget = new UserFilterWidget |>> { w =>
    w.chkText.addValueChangeHandler { _ =>
      ProjectionFilterUtil.toggle(w, "users_projection.filter.text", w.chkText, w.txtText)
    }

    w.chkRoles.addValueChangeHandler { _ =>
      ProjectionFilterUtil.toggle(w, "users_projection.filter.roles", w.chkRoles, w.tcsRoles)
    }
  }


  def reset(): Unit = setValues(UserFilterValues())


  def setValues(state: UserFilterValues) {
    widget.chkText.checked = state.text.isDefined
    widget.chkRoles.checked = state.roles.isDefined
    widget.chkShowInactive.checked = state.isShowInactive

    Seq(widget.chkText, widget.chkRoles, widget.chkShowInactive).foreach(_.fireValueChange(true))

    widget.txtText.value = state.text.getOrElse("")
    widget.tcsRoles.removeAllItems()
    for (role <- imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getAllRolesExceptUsersRole) {
      widget.tcsRoles.addItem(role.getId)
      widget.tcsRoles.setItemCaption(role.getId, role.getName)
    }
    widget.tcsRoles.value = state.roles.getOrElse(Set.empty[RoleId]).asJavaCollection
  }


  def getValues = UserFilterValues(
    when(widget.chkText.checked)(widget.txtText.trim),
    when(widget.chkRoles.checked)(widget.tcsRoles.value.asScala.toSet),
    widget.chkShowInactive.checked
  )  
}
