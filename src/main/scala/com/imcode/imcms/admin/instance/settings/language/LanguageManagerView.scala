package com.imcode
package imcms
package admin.instance.settings.language

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.vaadin.ui.themes.Reindeer


class LanguageManagerView extends VerticalLayout with FullSize {

  val mb = new MenuBar with FullWidth |>> { _.addStyleName("manager") }
  val miNew = mb.addItem("New")
  val miEdit = mb.addItem("Edit")
  val miDelete = mb.addItem("Delete")
  val miSetDefault = mb.addItem("Set default")
  val miReload = mb.addItem("Reload")
  val miHelp = mb.addItem("Help")
  val tblLanguages = new Table with SingleSelect[JInteger] with Immediate with FullSize |>> { tbl =>
    addContainerProperties(tbl,
      PropertyDescriptor[JInteger]("Id"),
      PropertyDescriptor[String]("Code"),
      PropertyDescriptor[String]("Name"),
      PropertyDescriptor[String]("Native name"),
      PropertyDescriptor[JBoolean]("Enabled"),
      PropertyDescriptor[JBoolean]("Default"),
      PropertyDescriptor[Void]("")
    )

    tbl.setStyleName(Reindeer.TABLE_BORDERLESS)
    tbl.setColumnExpandRatio("", 1f)
    tbl.setColumnAlignment("Id", Table.Align.RIGHT)
  }

  this.addComponents(mb, tblLanguages)
  this.setExpandRatio(tblLanguages, 1f)
}