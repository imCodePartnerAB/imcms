package com.imcode
package imcms
package admin.doc.content
package filedoc

import com.vaadin.ui.{ComboBox, TextField, FormLayout}
import com.imcode.imcms.vaadin.component._

class FileDocFilePropertiesEditorView extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Required
  val txtName = new TextField("Name") with Required
  val cbType = new ComboBox("Type") with Required with SingleSelect[String] with NoTextInput with NoNullSelection

  this.addComponents(txtId, txtName, cbType)
}
