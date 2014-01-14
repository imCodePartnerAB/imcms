package com.imcode
package imcms.admin.access.role

import _root_.imcode.server.user.RoleId
import scala.util.control.{Exception => Ex}
import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._


class RoleManagerView extends VerticalLayout with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miReload = mb.addItem("Reload", Reload16)
  val miHelp = mb.addItem("Help", Help16)
  val tblRoles = new Table with SingleSelect[RoleId] with Immediate

  addContainerProperties(tblRoles,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Name"))

  this.addComponents(mb, tblRoles)
}