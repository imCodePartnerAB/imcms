package com.imcode
package imcms
package admin.doc.category

import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._


class CategoryManagerView extends VerticalLayout with UndefinedSize {

  val mb = new MenuBar with FullWidth
  val miNew = mb.addItem("New")
  val miEdit = mb.addItem("Edit")
  val miDelete = mb.addItem("Delete")
  val miReload = mb.addItem("Reload")
  val miHelp = mb.addItem("Help")
  val tblCategories = new Table with SingleSelect[CategoryId] with Immediate with FullSize

  addContainerProperties(tblCategories,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[String]("Description"),
    PropertyDescriptor[String]("Icon"),
    PropertyDescriptor[String]("Type"))

  this.addComponents(mb, tblCategories)
  setExpandRatio(tblCategories, 1f)
}