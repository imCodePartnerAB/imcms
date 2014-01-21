package com.imcode
package imcms.admin.access.role

import _root_.imcode.server.user.RoleId
import com.vaadin.ui.themes.Reindeer
import scala.util.control.{Exception => Ex}
import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._


class RoleManagerView extends VerticalLayout with FullSize {

  val mb = new MenuBar with FullWidth |>> { _.addStyleName("manager") }
  val miNew = mb.addItem("Add new")
  val miEdit = mb.addItem("Edit")
  val miDelete = mb.addItem("Delete")
  val miReload = mb.addItem("Reload")
  val miHelp = mb.addItem("Help")
  val tblRoles = new Table with SingleSelect[RoleId] with Immediate with FullSize |>> { tbl =>
    addContainerProperties(tbl,
      PropertyDescriptor[JInteger]("Id"),
      PropertyDescriptor[String]("Name"),
      PropertyDescriptor[Void]("")
    )

    tbl.setColumnExpandRatio("", 1f)
    tbl.addStyleName(Reindeer.TABLE_BORDERLESS)
    tbl.setColumnAlignment("Id", Table.Align.RIGHT)
  }


  addComponents(mb, tblRoles)
  setExpandRatio(tblRoles, 1f)
}