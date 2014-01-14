package com.imcode
package imcms
package admin.doc.category

import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.vaadin.ui.themes.Reindeer


class CategoryManagerView extends VerticalLayout with FullSize {

  val mb = new MenuBar with FullWidth |>> { _.addStyleName("manager") }
  val miNew = mb.addItem("New")
  val miEdit = mb.addItem("Edit")
  val miDelete = mb.addItem("Delete")
  val miReload = mb.addItem("Reload")
  val miHelp = mb.addItem("Help")
  val tblCategories = new Table with SingleSelect[CategoryId] with Immediate with FullSize |>> { tbl =>
    addContainerProperties(tbl,
      PropertyDescriptor[JInteger]("Id"),
      PropertyDescriptor[String]("Name"),
      PropertyDescriptor[String]("Description"),
      PropertyDescriptor[String]("Icon"),
      PropertyDescriptor[String]("Type"),
      PropertyDescriptor[Void]("")
    )

    tbl.setColumnExpandRatio("", 1f)
    tbl.setStyleName(Reindeer.TABLE_BORDERLESS)
    tbl.setColumnAlignment("Id", Table.Align.RIGHT)
  }


  this.addComponents(mb, tblCategories)
  this.setExpandRatio(tblCategories, 1f)
}