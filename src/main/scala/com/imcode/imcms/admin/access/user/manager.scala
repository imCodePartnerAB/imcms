package com.imcode
package imcms.admin.access.user

import imcode.server.user._
import imcode.server.{Imcms}
import com.imcode.imcms.vaadin._
import com.vaadin.ui._

// todo add security check, add editAndSave, add external UI
class UserManager(app: ImcmsApplication) {
  private val search = new UserSearch(multiSelect = true)

  val ui = letret(new UserManagerUI(search.ui)) { ui =>
    val roleMapper = Imcms.getServices.getImcmsAuthenticatorAndUserAndRoleMapper

    ui.miNew setCommandHandler {
      app.initAndShow(new OkCancelDialog("New user")) { dlg =>
        dlg.mainUI = letret(new UserEditorUI) { c =>
          for (role <- roleMapper.getAllRoles if role.getId != RoleId.USERS) {
            c.tslRoles.addAvailableItem(role.getId, role.getName)
          }

          let(Imcms.getServices.getLanguageMapper.getDefaultLanguage) { l =>
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
              u setRoleIds c.tslRoles.chosenItemIds.toArray
              u setLanguageIso639_2 c.sltUILanguage.value

              roleMapper.addUser(u)
              search.reload()
            }
          }
        }
      }
    }

    ui.miEdit setCommandHandler {
      whenSingle(search.selection) { user =>
        app.initAndShow(new OkCancelDialog("Edit user")) { dlg =>
          dlg.mainUI = letret(new UserEditorUI) { c =>
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

            dlg wrapOkHandler {
              user setActive c.chkActivated.booleanValue
              user setFirstName c.txtFirstName.value
              user setLastName c.txtLastName.value
              user setLoginName c.txtLogin.value
              user setPassword c.txtPassword.value
              user setRoleIds c.tslRoles.chosenItemIds.toArray
              user setLanguageIso639_2 c.sltUILanguage.value

              roleMapper.saveUser(user)
              search.reload()
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
  val miNew = mb.addItem("Add new", New16)
  val miEdit = mb.addItem("Edit", Edit16)
  val miHelp = mb.addItem("Help", Help16)

  addComponents(this, mb, searchUI)
}


/**
 * Add/Edit user dialog content.
 */
class UserEditorUI extends FormLayout with UndefinedSize {
  val txtLogin = new TextField("Username")
  val txtPassword = new PasswordField("4-16 characters")
  val txtVerifyPassword = new PasswordField("4-16 characters (retype)")
  val txtFirstName = new TextField("First")
  val txtLastName = new TextField("Last")
  val chkActivated = new CheckBox("Activated")
  val tslRoles = new TwinSelect[RoleId]("Roles")
  val sltUILanguage = new Select("Interface language") with ValueType[String] with NoNullSelection
  val txtEmail = new TextField("Email")

  val lytPassword = new HorizontalLayoutUI("Password") with UndefinedSize {
      addComponent(txtPassword)
      addComponent(txtVerifyPassword)
  }

  val lytName = new HorizontalLayoutUI("Name") with UndefinedSize {
      addComponent(txtFirstName)
      addComponent(txtLastName)
  }

  val lytLogin = new HorizontalLayoutUI("Login") with UndefinedSize {
    addComponents(this, txtLogin, chkActivated)
    setComponentAlignment(chkActivated, Alignment.BOTTOM_LEFT)
  }

  val btnContacts = new Button("Edit...") with LinkStyle with Disabled

  val lytContacts = new HorizontalLayout with UndefinedSize {
    setCaption("Contacts")
    addComponent(btnContacts)
  }

  forlet(txtLogin, txtPassword, txtVerifyPassword, txtEmail) { _ setRequired true }

  addComponents(this, lytLogin, lytPassword, lytName, txtEmail, sltUILanguage, tslRoles, lytContacts)
}