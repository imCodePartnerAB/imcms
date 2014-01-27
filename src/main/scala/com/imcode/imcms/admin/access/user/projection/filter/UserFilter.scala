package com.imcode
package imcms
package admin.access.user.projection.filter

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._


class UserFilter extends ImcmsServicesSupport {

  val view: UserFilterView = new UserFilterView |>> { w =>
    w.chkText.addValueChangeHandler { _ =>
      ProjectionFilterUtil.toggle(w, "users_projection.filter.text", w.chkText, w.txtText)
    }

    w.chkRoles.addValueChangeHandler { _ =>
      ProjectionFilterUtil.toggle(w, "users_projection.filter.roles", w.chkRoles, w.tcsRoles)
    }
  }


  def reset(): Unit = setFilterParameters(UserFilterParameters())


  def setFilterParameters(parameters: UserFilterParameters) {
    view.chkText.checked = parameters.text.isDefined
    view.chkRoles.checked = parameters.roles.isDefined
    view.chkShowDisabled.checked = parameters.isShowInactive

    Seq(view.chkText, view.chkRoles, view.chkShowDisabled).foreach(_.fireValueChange(repaintIsNotNeeded = true))

    view.txtText.value = parameters.text.getOrElse("")
    view.tcsRoles.removeAllItems()
    for (role <- imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getAllRolesExceptUsersRole) {
      view.tcsRoles.addItem(role.getId)
      view.tcsRoles.setItemCaption(role.getId, role.getName)
    }
    view.tcsRoles.selection = parameters.roles.toSeq.flatten
  }


  def getFilterParameters = UserFilterParameters(
    when(view.chkText.checked)(view.txtText.trimmedValue),
    when(view.chkRoles.checked)(view.tcsRoles.selection.to[Set]),
    view.chkShowDisabled.checked
  )  
}
