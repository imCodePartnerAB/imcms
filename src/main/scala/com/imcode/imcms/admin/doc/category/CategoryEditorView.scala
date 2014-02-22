package com.imcode
package imcms
package admin.doc.category

import com.imcode.imcms.admin.instance.file.ImagePickerComponent
import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._

class CategoryEditorView(val imagePickerComponent: ImagePickerComponent) extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled {
    setColumns(11)
  }
  val txtName = new TextField("Name") with Required
  val txaDescription = new TextArea("Description") |>> {
    t =>
      t.setRows(5)
      t.setColumns(11)
  }

  val sltType = new ComboBox("Type") with SingleSelect[String] with Required with NoNullSelection

  addComponents(txtId, txtName, sltType, imagePickerComponent, txaDescription)
  imagePickerComponent.setCaption("Icon")
}