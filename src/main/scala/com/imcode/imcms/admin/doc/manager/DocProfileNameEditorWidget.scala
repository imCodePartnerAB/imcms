package com.imcode.imcms.admin.doc.manager

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._

class DocProfileNameEditorWidget extends FormLayout with UndefinedSize {
  val txtName = new TextField("Name")

  addComponent(txtName)
}