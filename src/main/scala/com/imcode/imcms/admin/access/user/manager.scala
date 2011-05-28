package com.imcode
package imcms
package admin.access.user

import imcode.server.user._
import com.imcode.imcms.vaadin._
import com.vaadin.ui._

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

// todo add security check, add editAndSave, add external UI
class UserManager(app: ImcmsApplication) extends ImcmsServicesSupport {
  private val search = new UserSearch

  val ui = letret(new UserManagerUI(search.ui)) { ui =>
    val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper

    ui.miNew setCommandHandler {
      app.initAndShow(new OkCancelDialog("user.dlg.new.caption".i)) { dlg =>
        dlg.mainUI = letret(new UserEditorUI) { c =>
          for (role <- roleMapper.getAllRoles if role.getId != RoleId.USERS) {
            c.tslRoles.addItem(role.getId, role.getName)
          }

          let(imcmsServices.getLanguageMapper.getDefaultLanguage) { l =>
            c.sltUILanguage.addItem(l)
            c.sltUILanguage.select(l)
          }

          c.chkActivated.setValue(true)

          dlg wrapOkHandler {
            let(new UserDomainObject) { u =>
              u setActive c.chkActivated.booleanValue
              u setFirstName c.txtFirstName.value
              u setLastName c.txtLastName.value
              u setLoginName c.txtLogin.value
              u setPassword c.txtPassword.value
              u setRoleIds c.tslRoles.value.toSeq.toArray
              u setLanguageIso639_2 c.sltUILanguage.value

              roleMapper.addUser(u)
              search.reset()
            }
          }
        }
      }
    }

    ui.miEdit setCommandHandler {
      whenSingle(search.selection) { user =>
        app.initAndShow(new OkCancelDialog("user.dlg.edit.caption".f(user.getLoginName))) { dlg =>
          dlg.mainUI = letret(new UserEditorUI) { c =>
            c.chkActivated setValue user.isActive
            c.txtFirstName setValue user.getFirstName
            c.txtLastName setValue user.getLastName
            c.txtLogin setValue user.getLoginName
            c.txtPassword setValue user.getPassword

            for (role <- roleMapper.getAllRoles if role.getId != RoleId.USERS) {
              c.tslRoles.addItem(role.getId, role.getName)
            }

            c.tslRoles.value = user.getRoleIds.filterNot(RoleId.USERS ==).toSeq

            let(imcmsServices.getLanguageMapper.getDefaultLanguage) { l =>
              c.sltUILanguage.addItem(l)
            }

            c.sltUILanguage.select(user.getLanguageIso639_2)

            dlg wrapOkHandler {
              user setActive c.chkActivated.booleanValue
              user setFirstName c.txtFirstName.value
              user setLastName c.txtLastName.value
              user setLoginName c.txtLogin.value
              user setPassword c.txtPassword.value
              user setRoleIds c.tslRoles.value.toSeq.toArray
              user setLanguageIso639_2 c.sltUILanguage.value

              roleMapper.saveUser(user)
              search.reset()
            }
          }
        }
      }
    }

    search.listen { ui.miEdit setEnabled _.size == 1 }
    search.notifyListeners()
  }
}


class UserManagerUI(val searchUI: Component) extends VerticalLayout with Spacing {
  import com.imcode.imcms.vaadin.Theme.Icons._

  val mb = new MenuBar
  val miNew = mb.addItem("mi.new".i, New16)
  val miEdit = mb.addItem("mi.edit".i, Edit16)
  val miHelp = mb.addItem("mi.help".i, Help16)

  addComponents(this, mb, searchUI)
}


/**
 * Add/Edit user dialog content.
 */
class UserEditorUI extends FormLayout with UndefinedSize {
  val txtLogin = new TextField("user.editor.frm.fld.txt_login".i)
  val txtPassword = new PasswordField("user.editor.frm.fld.pwd_password".i)
  val txtVerifyPassword = new PasswordField("user.editor.frm.fld.pwd_password_retype".i)
  val txtFirstName = new TextField("user.editor.frm.fld.txt_first_name".i)
  val txtLastName = new TextField("user.editor.frm.fld.txt_last_name".i)
  val chkActivated = new CheckBox("user.editor.frm.fld.chk_activated".i)
  val tslRoles = new TwinColSelect("user.editor.frm.fld.tcs_roles".i) with MultiSelect2[RoleId] with DefaultI18nTCS
  val sltUILanguage = new Select("user.editor.frm.fld.interface_language".i) with ValueType[String] with NoNullSelection
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
    addComponents(this, txtLogin, chkActivated)
    setComponentAlignment(chkActivated, Alignment.BOTTOM_LEFT)
  }

  val btnEditContacts = new Button("user.editor.frm.fld.btn_edit_contacts".i) with LinkStyle with Disabled

  val lytContacts = new HorizontalLayoutUI("user.editor.frm.fld.contacts".i) with UndefinedSize {
    addComponent(btnEditContacts)
  }

  forlet(txtLogin, txtPassword, txtVerifyPassword, txtEmail) { _ setRequired true }

  addComponents(this, lytLogin, lytPassword, lytName, txtEmail, sltUILanguage, tslRoles, lytContacts)
}