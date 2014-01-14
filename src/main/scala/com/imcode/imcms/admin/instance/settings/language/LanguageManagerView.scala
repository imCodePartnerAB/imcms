package com.imcode
package imcms
package admin.instance.settings.language

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._


class LanguageManagerView extends VerticalLayout with UndefinedSize {

  val mb = new MenuBar with FullWidth
  val miNew = mb.addItem("New")
  val miEdit = mb.addItem("Edit")
  val miDelete = mb.addItem("Delete")
  val miSetDefault = mb.addItem("Set default")
  val miReload = mb.addItem("Reload")
  val miHelp = mb.addItem("Help")
  val tblLanguages = new Table with SingleSelect[JInteger] with Immediate

  addContainerProperties(tblLanguages,
    PropertyDescriptor[JInteger]("Id"),
    PropertyDescriptor[String]("Code"),
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[String]("Native name"),
    PropertyDescriptor[JBoolean]("Enabled"),
    PropertyDescriptor[JBoolean]("Default"))

  this.addComponents(mb, tblLanguages)
}