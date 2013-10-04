package com.imcode.imcms.admin.doc.manager

import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._

class DocProfileNameEditorUI extends FormLayout with UndefinedSize {
  val txtName = new TextField("Name")

  addComponent(txtName)
}