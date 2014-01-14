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
  val txtLogin = new TextField("user.editor.frm.fld.txt_login".i)
  val txtPassword = new PasswordField("user.editor.frm.fld.pwd_password".i)
  val txtVerifyPassword = new PasswordField("user.editor.frm.fld.pwd_password_retype".i)
  val txtFirstName = new TextField("user.editor.frm.fld.txt_first_name".i)
  val txtLastName = new TextField("user.editor.frm.fld.txt_last_name".i)
  val chkActivated = new CheckBox("user.editor.frm.fld.chk_activated".i)
  val tcsRoles = new TwinColSelect("user.editor.frm.fld.tcs_roles".i) with MultiSelect[RoleId] with TCSDefaultI18n
  val sltUILanguage = new ComboBox("user.editor.frm.fld.interface_language".i) with SingleSelect[String] with NoNullSelection
  val txtEmail = new TextField("user.editor.frm.fld.email".i)

  val lytPassword = new HorizontalLayoutUI("user.editor.frm.fld.password".i) with UndefinedSize {
    addComponent(txtPassword)
    addComponent(txtVerifyPassword)
  }

  val lytName = new HorizontalLayoutUI("user.editor.frm.fld.name".i) with UndefinedSize {
    addComponent(txtFirstName)
    addComponent(txtLastName)
  }

  val lytLogin = new HorizontalLayoutUI("user.editor.frm.fld.account".i) with UndefinedSize {
    this.addComponents(txtLogin, chkActivated)
    setComponentAlignment(chkActivated, Alignment.BOTTOM_LEFT)
  }

  val btnEditContacts = new Button("user.editor.frm.fld.btn_edit_contacts".i) with LinkStyle with Disabled

  val lytContacts = new HorizontalLayoutUI("user.editor.frm.fld.contacts".i) with UndefinedSize {
    addComponent(btnEditContacts)
  }

  Seq(txtLogin, txtPassword, txtVerifyPassword, txtEmail).foreach(_.setRequired(true))

  this.addComponents(lytLogin, lytPassword, lytName, txtEmail, sltUILanguage, tcsRoles, lytContacts)
}