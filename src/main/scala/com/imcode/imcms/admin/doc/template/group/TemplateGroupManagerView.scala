package com.imcode
package imcms
package admin.doc.template
package group

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.vaadin.ui.themes.Reindeer


class TemplateGroupManagerView extends VerticalLayout with FullSize {

  val mb = new MenuBar with FullWidth |>> { _.addStyleName("manager") }
  val miNew = mb.addItem("New")
  val miEdit = mb.addItem("Edit")
  val miDelete = mb.addItem("Delete")
  val miHelp = mb.addItem("Help")
  val miReload = mb.addItem("Reload")
  val tblGroups = new Table with SingleSelect[TemplateGroupId] with Selectable with Immediate with FullSize

  addContainerProperties(tblGroups,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[JInteger]("Templates count"),
    PropertyDescriptor[Void]("")
  )

  this.addComponents(mb, tblGroups)
  this.setExpandRatio(tblGroups, 1f)
  tblGroups.setColumnExpandRatio("", 1f)
  tblGroups.setStyleName(Reindeer.TABLE_BORDERLESS)
  tblGroups.setColumnAlignment("Id", Table.Align.RIGHT)
  tblGroups.setColumnAlignment("Templates count", Table.Align.RIGHT)
}
