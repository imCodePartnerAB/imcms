package com.imcode
package imcms
package admin.access.ip

import scala.util.control.{Exception => Ex}
import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.data.PropertyDescriptor


class IPAccessManagerView extends VerticalLayout with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miDelete = mb.addItem("Delete", Delete16)
  val miReload = mb.addItem("Reload", Reload16)
  val miHelp = mb.addItem("Help", Help16)

  val tblIP = new Table with SingleSelect[JInteger] with Immediate

  addContainerProperties(tblIP,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[String]("IP range from"),
    PropertyDescriptor[String]("IP range to"))

  this.addComponents(mb, tblIP)
}