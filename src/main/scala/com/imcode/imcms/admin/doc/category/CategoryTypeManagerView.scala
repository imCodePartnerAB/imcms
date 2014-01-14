package com.imcode
package imcms
package admin.doc.category

import scala.util.control.{Exception => Ex}
import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._


class CategoryTypeManagerView extends VerticalLayout with UndefinedSize {

  val mb = new MenuBar with FullWidth
  val miNew = mb.addItem("New")
  val miEdit = mb.addItem("Edit")
  val miDelete = mb.addItem("Delete")
  val miReload = mb.addItem("Reload")
  val miHelp = mb.addItem("Help")
  val tblTypes = new Table with SingleSelect[CategoryTypeId] with Immediate with FullSize

  addContainerProperties(tblTypes,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[JBoolean]("Multi select?"),
    PropertyDescriptor[JBoolean]("Inherited to new documents?"),
    PropertyDescriptor[JBoolean]("Used by image archive?"))

  this.addComponents(mb, tblTypes)
  setExpandRatio(tblTypes, 1f)
}