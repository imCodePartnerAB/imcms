package com.imcode
package imcms
package admin.doc.category

import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._


class CategoryTypeManagerView extends VerticalLayout with FullSize {

  val mb = new MenuBar with MenuBarInTabStyle with FullWidth
  val miNew = mb.addItem("New")
  val miEdit = mb.addItem("Edit")
  val miDelete = mb.addItem("Delete")
  val miReload = mb.addItem("Reload")
  val miHelp = mb.addItem("Help")

  override def setId(id: String): Unit = super.setId(id)

  val tblTypes = new Table with BorderlessStyle with SingleSelect[CategoryTypeId] with Immediate with FullSize |>> { tbl =>
    addContainerProperties(tbl,
      PropertyDescriptor[JInteger]("Id"),
      PropertyDescriptor[String]("Name"),
      PropertyDescriptor[JBoolean]("Multi select?"),
      PropertyDescriptor[JBoolean]("Inherited to new documents?"),
      PropertyDescriptor[JBoolean]("Used by image archive?"),
      PropertyDescriptor[Void]("")
    )

    tbl.setColumnExpandRatio("", 1f)
    tbl.setColumnAlignment("Id", Table.Align.RIGHT)
  }


  addComponents(mb, tblTypes)
  setExpandRatio(tblTypes, 1f)
}