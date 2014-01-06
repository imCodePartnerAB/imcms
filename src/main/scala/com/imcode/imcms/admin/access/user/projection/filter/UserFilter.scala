package com.imcode
package imcms
package admin.access.user.projection.filter

import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import _root_.imcode.server.user.RoleId


class UserFilter extends ImcmsServicesSupport {

  val view: UserFilterView = new UserFilterView |>> { w =>
    w.chkText.addValueChangeHandler { _ =>
      ProjectionFilterUtil.toggle(w, "users_projection.filter.text", w.chkText, w.txtText)
    }

    w.chkRoles.addValueChangeHandler { _ =>
      ProjectionFilterUtil.toggle(w, "users_projection.filter.roles", w.chkRoles, w.tcsRoles)
    }
  }


  def reset(): Unit = setValues(UserFilterValues())


  def setValues(state: UserFilterValues) {
    view.chkText.checked = state.text.isDefined
    view.chkRoles.checked = state.roles.isDefined
    view.chkShowInactive.checked = state.isShowInactive

    Seq(view.chkText, view.chkRoles, view.chkShowInactive).foreach(_.fireValueChange(true))

    view.txtText.value = state.text.getOrElse("")
    view.tcsRoles.removeAllItems()
    for (role <- imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getAllRolesExceptUsersRole) {
      view.tcsRoles.addItem(role.getId)
      view.tcsRoles.setItemCaption(role.getId, role.getName)
    }
    view.tcsRoles.value = state.roles.getOrElse(Set.empty[RoleId]).asJavaCollection
  }


  def getValues = UserFilterValues(
    when(view.chkText.checked)(view.txtText.trim),
    when(view.chkRoles.checked)(view.tcsRoles.value.asScala.toSet),
    view.chkShowInactive.checked
  )  
}
