package com.imcode
package imcms
package admin.instance.settings.language

import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._


class LanguageEditorView extends FormLayout with UndefinedSize {

  val txtId = new TextField("doc_lng_editor.frm_fld.id".i) with Disabled
  val txtCode = new TextField("doc_lng_editor.frm_fld.code".i)
  val txtName = new TextField("doc_lng_editor.frm_fld.name".i)
  val txtNativeName = new TextField("doc_lng_editor.frm_fld.native_name".i)
  val chkEnabled = new CheckBox("doc_lng_editor.frm_fld.is_enabled".i)

  addComponents(txtId, txtCode, txtName, txtNativeName, chkEnabled)
}