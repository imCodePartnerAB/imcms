package com.imcode
package imcms
package admin
package access.user

import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.component.dialog.OkCancelDialog
import com.imcode.imcms.vaadin.{Current, Editor}
import scala.collection.JavaConverters._

import imcode.server.user.{RoleId, UserDomainObject}

// todo: interface language
// todo: password modified flag
// todo: contacts editor dialog - buttons close & reset
// fixme: validate fields

class UserEditor(user: UserDomainObject) extends Editor with ImcmsServicesSupport {

  override type Data = UserDomainObject

  private val contactsEditor = new UserContactsEditor(user)

  override val view = new UserEditorView |>> { v =>
    v.sltUILanguage.addItem("eng", "English")
    v.sltUILanguage.addItem("swe", "Svenska")

    v.btnEditContacts.addClickHandler { _ =>
      val contactsEditorDialog = new OkCancelDialog("Contacts".i)

      contactsEditorDialog.mainComponent = contactsEditor.view
      contactsEditorDialog.setOkButtonHandler {
        contactsEditorDialog.close()
      }

      Current.ui.addWindow(contactsEditorDialog)
    }
  }

  resetValues()

  override def resetValues() {
    view.chkEnabled.setValue(user.isActive)
    view.txtFirstName.setValue(user.getFirstName)
    view.txtLastName.setValue(user.getLastName)
    view.txtLogin.setValue(user.getLoginName)
    view.txtPassword.setValue("")
    view.txtEmail.setValue(user.getEmailAddress)

    view.tcsRoles.removeAllItems()
    for (role <- imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getAllRoles if role.getId != RoleId.USERS) {
      view.tcsRoles.addItem(role.getId, role.getName)
    }

    view.tcsRoles.value = user.getRoleIds.filterNot(_ == RoleId.USERS).toSeq.asJava
    view.sltUILanguage.select(user.getLanguageIso639_2)

    contactsEditor.resetValues()
  }

  override def collectValues(): UserEditor#ErrorsOrData = {
    val u = user.clone()

    u.setActive(view.chkEnabled.checked)
    u.setFirstName(view.txtFirstName.trimmedValue)
    u.setLastName(view.txtLastName.trimmedValue)
    u.setLoginName(view.txtLogin.trimmedValue)
    u.setPassword(view.txtPassword.value)
    u.setRoleIds(view.tcsRoles.value.asScala.toArray)
    u.setLanguageIso639_2(view.sltUILanguage.selection)
    u.setEmailAddress(view.txtEmail.trimmedValue)
    u.setRoleIds(view.tcsRoles.selection.toArray)

    Right(u)
  }
}
