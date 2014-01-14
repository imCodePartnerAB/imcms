package com.imcode
package imcms.admin.doc.template

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.vaadin.ui.themes.Reindeer

class TemplateManagerView extends VerticalLayout with FullSize {

  val mb = new MenuBar with FullWidth |>> { _.addStyleName("manager") }
  val miUpload = mb.addItem("Upload")
  val miDownload = mb.addItem("Download")
  val miRename = mb.addItem("Rename")
  val miDelete = mb.addItem("Delete")
  val miEditContent = mb.addItem("Edit content")
  val miDocuments = mb.addItem("Related documents")
  val miReload = mb.addItem("Reload")
  val miHelp = mb.addItem("Help")
  val tblTemplates = new Table with SingleSelect[TemplateName] with Selectable with Immediate with FullSize

  addContainerProperties(tblTemplates,
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[String]("Type"),
    PropertyDescriptor[JInteger]("Document count using this template"),
    PropertyDescriptor[Void]("")
  )

  this.addComponents(mb, tblTemplates)
  this.setExpandRatio(tblTemplates, 1f)
  tblTemplates.setColumnExpandRatio("", 1f)
  tblTemplates.setStyleName(Reindeer.TABLE_BORDERLESS)
  tblTemplates.setColumnAlignment("Document count using this template", Table.Align.RIGHT)
}