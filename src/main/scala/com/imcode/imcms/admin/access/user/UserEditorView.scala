package com.imcode
package imcms
package admin.access.user

import _root_.imcode.server.user._

import com.imcode.imcms.vaadin.component._
import com.vaadin.ui._


/**
 * Add/Edit user dialog content.
 */
class UserEditorView extends FormLayout with UndefinedSize {
  val txtLogin = new TextField("user_editor.frm_fld.txt_login".i)
  val txtPassword = new PasswordField("user_editor.frm_fld.pwd_password".i)
  val txtPasswordCheck = new PasswordField("user_editor.frm_fld.pwd_password_check".i)
  val txtFirstName = new TextField("user_editor.frm_fld.txt_first_name".i)
  val txtLastName = new TextField("user_editor.frm_fld.txt_last_name".i)
  val chkEnabled = new CheckBox("user_editor.frm_fld.chk_enabled".i)
  val tcsRoles = new TwinColSelect("user_editor.frm_fld.tcs_roles".i) with MultiSelect[RoleId] with TCSDefaultI18n
  val sltUILanguage = new ComboBox("user_editor.frm_fld.language".i) with SingleSelect[String] with NoNullSelection
  val txtEmail = new TextField("user_editor.frm_fld.email".i)
  val btnEditContacts = new Button("user_editor.frm_fld.btn_caption.edit_contacts".i) with LinkStyle

  val lytPassword = new HorizontalLayout(txtPassword, txtPasswordCheck) with UndefinedSize with Spacing |>> { lyt =>
    lyt.setCaption("user_editor.frm_fld.password".i)
  }

  val lytName = new HorizontalLayout(txtFirstName, txtLastName) with UndefinedSize with Spacing |>> { lyt =>
    lyt.setCaption("user_editor.frm_fld.name".i)
  }

  val lytLogin = new HorizontalLayout(txtLogin, chkEnabled) with UndefinedSize with Spacing |>> { lyt =>
    lyt.setCaption("user_editor.frm_fld.account".i)
    lyt.setComponentAlignment(chkEnabled, Alignment.BOTTOM_LEFT)
  }

  val lytContacts = new HorizontalLayout(btnEditContacts) with UndefinedSize with Spacing |>> { lyt =>
    lyt.setCaption("user_editor.frm_fld.contacts".i)
  }

  Seq(txtLogin, txtPassword, txtPasswordCheck, txtEmail).foreach(_.setRequired(true))

  addComponents(lytLogin, lytPassword, lytName, txtEmail, sltUILanguage, tcsRoles, lytContacts)
}