package com.imcode
package imcms
package admin.access.user.projection.filter

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import imcode.server.user.RoleId


class UserFilterView extends CustomLayout("admin/access/user/projection/filter") with FullWidth {
  val chkText = new CheckBox("users_projection.filter.chk_text".i) with ExposeValueChange[JBoolean] with Immediate
  val txtText = new TextField with FullWidth |>> { txt =>
    txt.setInputPrompt("users_projection.filter.txt_text.prompt".i)
    txt.setDescription("users_projection.filter.txt_text.tooltip".i)
  }

  val chkRoles = new CheckBox("users_projection.filter.chk_roles".i) with ExposeValueChange[JBoolean] with Immediate
  val tcsRoles = new TwinColSelect with MultiSelect[RoleId] with TCSDefaultI18n
  val chkShowDisabled = new CheckBox("users_projection.filter.chk_show_disabled".i) with ExposeValueChange[JBoolean] with Immediate

  val btnReset = new Button("btn_reset".i) with SmallStyle
  val btnFilter = new Button("btn_search".i) with SmallStyle

  private val lytButtons = new HorizontalLayout(btnReset, btnFilter) with Spacing with UndefinedSize

  this.addNamedComponents(
    "users_projection.filter.chk_text" -> chkText,
    "users_projection.filter.text" -> txtText,
    "users_projection.filter.chk_roles" -> chkRoles,
    "users_projection.filter.roles" -> tcsRoles,
    "users_projection.filter.misc" -> chkShowDisabled,
    "users_projection.filter.buttons" -> lytButtons
  )
}
