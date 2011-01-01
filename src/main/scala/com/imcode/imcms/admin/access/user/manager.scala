package com.imcode
package imcms.admin.access.user

import com.vaadin.ui._
import imcode.server.user._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin._

// todo add security check, add editAndSave, add external UI
class UserManager(app: ImcmsApplication) {
  val userSelect = new UserSelect

  val ui = letret(new UserManagerUI(userSelect.ui)) { ui =>
    val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper

    ui.miNew setCommand block {
      app.initAndShow(new OkCancelDialog("New user")) { dlg =>
        dlg.mainUI = letert(new UserEditorUI) { c =>
          for (role <- roleMapper.getAllRoles if role.getId != RoleId.USERS) {
            c.tslRoles.addAvailableItem(role.getId, role.getName)
          }

          let(Imcms.getServices.getLanguageMapper.getDefaultLanguage) { l =>
            c.sltUILanguage.addItem(l)
            c.sltUILanguage.select(l)
          }

          c.chkActivated.setValue(true)

          dlg setOkHandler {
            let(new UserDomainObject) { u =>
              u setActive c.chkActivated.booleanValue
              u setFirstName c.txtFirstName.value
              u setLastName c.txtLastName.value
              u setLoginName c.txtLogin.value
              u setPassword c.txtPassword.value
              u setRoleIds c.tslRoles.chosenItemIds.toArray
              u setLanguageIso639_2 c.sltUILanguage.value

              roleMapper.addUser(u)
              userSelect.reload()
            }
          }
        }
      }
    }

   ui.miEdit setCommand block {
      whenSelected(ui.userSelectUI.tblUsers) { userId =>
        app.initAndShow(new OkCancelDialog("Edit user")) { dlg =>
          let(dlg setMainContent new UserEditorUI) { c =>
            val user = roleMapper.getUser(userId.intValue)
            val userRoleIds = user.getRoleIds

            c.chkActivated setValue user.isActive
            c.txtFirstName setValue user.getFirstName
            c.txtLastName setValue user.getLastName
            c.txtLogin setValue user.getLoginName
            c.txtPassword setValue user.getPassword

            for {
              role <- roleMapper.getAllRoles
              roleId = role.getId
              if roleId != RoleId.USERS
            } {
              if (userRoleIds contains roleId) {
                c.tslRoles.addChosenItem(roleId, role.getName)
              } else {
                c.tslRoles.addAvailableItem(roleId, role.getName)
              }
            }

            let(Imcms.getServices.getLanguageMapper.getDefaultLanguage) { l =>
              c.sltUILanguage.addItem(l)
            }

            c.sltUILanguage.select(user.getLanguageIso639_2)

            dlg setOkHandler {
              user setActive c.chkActivated.booleanValue
              user setFirstName c.txtFirstName.value
              user setLastName c.txtLastName.value
              user setLoginName c.txtLogin.value
              user setPassword c.txtPassword.value
              user setRoleIds c.tslRoles.chosenItemIds.toArray
              user setLanguageIso639_2 c.sltUILanguage.value

              roleMapper.saveUser(user)
              userSelect.reload()
            }
          }
        }
      }
    }

    userSelect.selectionListeners += { ui.miEdit setEnabled _.isDefined }
    userSelect.notifyListeners()
  }
}


class UserManagerUI(val userSelectUI: UserSelectUI) extends VerticalLayout with Spacing {
  import com.imcode.imcms.vaadin.Theme.Icons._

  val mb = new MenuBar
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miHelp = mb.addItem("Help", Help16)

  addComponents(this, mb, userSelectUI)
}


/**
 * Add/Edit user dialog content.
 */
class UserEditorUI extends FormLayout {
  val txtLogin = new TextField("Username")
  val txtPassword = new TextField("4-16 characters") with Secret
  val txtVerifyPassword = new TextField("4-16 characters (retype)") with Secret
  val txtFirstName = new TextField("First")
  val txtLastName = new TextField("Last")
  val chkActivated = new CheckBox("Activated")
  val tslRoles = new TwinSelect[RoleId]("Roles")
  val sltUILanguage = new Select("Interface language") with ValueType[String] with NoNullSelection
  val txtEmail = new TextField("Email")

  val lytPassword = new HorizontalLayoutUI("Password") {
      addComponent(txtPassword)
      addComponent(txtVerifyPassword)
  }

  val lytName = new HorizontalLayoutUI("Name") {
      addComponent(txtFirstName)
      addComponent(txtLastName)
  }

  val lytLogin = new HorizontalLayoutUI("Login") {
    addComponents(this, txtLogin, chkActivated)
    setComponentAlignment(chkActivated, Alignment.BOTTOM_LEFT)
  }

  val btnContacts = new Button("Edit...") with LinkStyle with Disabled

  val lytContacts = new HorizontalLayout {
    setCaption("Contacts")
    addComponent(btnContacts)
  }

  forlet(txtLogin, txtPassword, txtVerifyPassword, txtEmail) { _ setRequired true }

  addComponents(this, lytLogin, lytPassword, lytName, txtEmail, sltUILanguage, tslRoles, lytContacts)
}