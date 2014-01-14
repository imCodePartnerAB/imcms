package com.imcode
package imcms.admin.instance.settings.property

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._


class PropertyEditorView extends FormLayout with UndefinedSize {
  class ContactComponent(caption: String) extends VerticalLayout() with UndefinedSize with Spacing {
    val txtName = new TextField("Name")
    val txtEmail = new TextField("e-mail")

    setCaption(caption)
    this.addComponents(txtName, txtEmail)
  }

  val txtStartPageNumber = new TextField("Start page number")
  val txaSystemMessage = new TextArea("System message") |>> { _.setRows(5) }
  val serverMaster = new ContactComponent("Server master")
  val webMaster = new ContactComponent("Web master")

  this.addComponents(txtStartPageNumber, txaSystemMessage, serverMaster, webMaster)
}