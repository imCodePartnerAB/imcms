package com.imcode
package imcms.admin.doc.template

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._

class TemplateManagerView extends VerticalLayout with Spacing with UndefinedSize {
  import Theme.Icon._

  val mb = new MenuBar
  val miUpload = mb.addItem("Upload", New16, null)
  val miDownload = mb.addItem("Download", New16, null)
  val miRename = mb.addItem("Rename", Edit16, null)
  val miDelete = mb.addItem("Delete", Delete16, null)
  val miEditContent = mb.addItem("Edit content", EditContent16, null)
  val miDocuments = mb.addItem("Related documents", Documents16, null)
  val miHelp = mb.addItem("Help", Help16, null)
  val tblTemplates = new Table with SingleSelect[TemplateName] with Selectable with Immediate
  val rc = new ReloadableContentView(tblTemplates)

  addContainerProperties(tblTemplates,
    PropertyDescriptor[String]("Name"),
    PropertyDescriptor[String]("Type"),
    PropertyDescriptor[JInteger]("Document count using this template"))

  this.addComponents(mb, rc)
}