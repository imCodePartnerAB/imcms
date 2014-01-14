package com.imcode
package imcms
package admin.doc.category

import com.vaadin.ui._

import com.imcode.imcms.vaadin.component._

class CategoryTypeEditorView extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtName = new TextField("Name") with Required
  val chkMultiSelect = new CheckBox("Multiselect")
  val chkInherited = new CheckBox("Inherited to new documents")
  val chkImageArchive = new CheckBox("Used by image archive")

  this.addComponents(txtId, txtName, chkMultiSelect, chkInherited, chkImageArchive)
}