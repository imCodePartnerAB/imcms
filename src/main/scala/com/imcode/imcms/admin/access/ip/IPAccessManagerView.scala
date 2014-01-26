package com.imcode
package imcms
package admin.access.ip

import scala.util.control.{Exception => Ex}
import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.data.PropertyDescriptor


class IPAccessManagerView extends VerticalLayout with FullSize {

  val mb = new MenuBar with FullWidth with MenuBarInTabStyle
  val miNew = mb.addItem("Add new")
  val miEdit = mb.addItem("Edit")
  val miDelete = mb.addItem("Delete")
  val miReload = mb.addItem("Reload")
  val miHelp = mb.addItem("Help")

  val tblIP = new Table with BorderlessStyle with SingleSelect[JInteger] with Immediate with FullSize |>> { tbl =>
    addContainerProperties(tbl,
      PropertyDescriptor[JInteger]("Id"),
      PropertyDescriptor[String]("Name"),
      PropertyDescriptor[String]("IP range from"),
      PropertyDescriptor[String]("IP range to"),
      PropertyDescriptor[Void]("")
    )

    tbl.setColumnExpandRatio("", 1f)
    tbl.setColumnAlignment("Id", Table.Align.RIGHT)
  }


  addComponents(mb, tblIP)
  setExpandRatio(tblIP, 1f)
}