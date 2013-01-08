package com.imcode
package imcms
package admin.access.user.projection.filter

import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._
import imcode.server.user.RoleId


class UserFilterUI extends CustomLayout("admin/access/user/projection/filter") with UndefinedSize {
  val chkText = new CheckBox("user.search.frm.fld.chk_text".i) with ExposeValueChange with Immediate
  val txtText = new TextField {
    setInputPrompt("user.search.frm.fld.txt_text.prompt".i)
    setDescription("user.search.frm.fld.txt_text.tooltip".i)
  }

  val chkRoles = new CheckBox("user.search.frm.fld.chk_roles".i) with ExposeValueChange with Immediate
  val tcsRoles = new TwinColSelect with MultiSelect[RoleId] with TCSDefaultI18n
  val chkShowInactive = new CheckBox("user.search.frm.fld.chk_show_inactive".i) with ExposeValueChange with Immediate
  val lytButtons = new HorizontalLayout with Spacing with UndefinedSize {
    val btnReset = new Button("btn_reset".i) with SmallStyle
    val btnFilter = new Button("btn_search".i) with SmallStyle

    this.addComponents(btnReset, btnFilter)
  }

  this.addNamedComponents(
    "users_projection.filter.chk_text" -> chkText,
    "users_projection.filter.text" -> txtText,
    "users_projection.filter.chk_roles" -> chkRoles,
    "users_projection.filter.roles" -> tcsRoles,
    "users_projection.filter.misc" -> chkShowInactive,
    "users_projection.filter.buttons" -> lytButtons
  )
}
