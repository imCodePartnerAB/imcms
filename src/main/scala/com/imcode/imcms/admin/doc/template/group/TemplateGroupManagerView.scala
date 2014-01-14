package com.imcode
package imcms
package admin.doc.template
package group

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._


class TemplateGroupManagerView extends VerticalLayout with UndefinedSize {

  val mb = new MenuBar with FullWidth
  val miNew = mb.addItem("New")
  val miEdit = mb.addItem("Edit")
  val miDelete = mb.addItem("Delete")
  val miHelp = mb.addItem("Help")
  val miReload = mb.addItem("Reload")
  val tblGroups = new Table with SingleSelect[TemplateGroupId] with Selectable with Immediate

  addContainerProperties(tblGroups,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[JInteger]("Templates count"))

  this.addComponents(mb, tblGroups)
}
