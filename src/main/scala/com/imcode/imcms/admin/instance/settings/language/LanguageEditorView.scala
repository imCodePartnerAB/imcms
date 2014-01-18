package com.imcode
package imcms
package admin.instance.settings.language

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._


class LanguageEditorView extends FormLayout with UndefinedSize {
  val txtId = new TextField("Id") with Disabled
  val txtCode = new TextField("Code")
  val txtName = new TextField("Name")
  val txtNativeName = new TextField("Native name")
  val chkEnabled = new CheckBox("Enabled")

  addComponents(txtId, txtCode, txtName, txtNativeName, chkEnabled)
}