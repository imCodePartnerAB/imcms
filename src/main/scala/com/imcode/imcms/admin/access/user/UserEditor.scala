package com.imcode
package imcms
package admin
package access.user

import _root_.java.lang.String
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.component.dialog.OkCancelDialog
import com.imcode.imcms.vaadin.{Current, Editor}
import com.vaadin.server.UserError
import scala.collection.JavaConverters._

import imcode.server.user.{RoleId, UserDomainObject}
import imcode.util.Utility

// todo: contacts editor dialog - i18n, phone
// todo: check login - required, unique

/*
 * When saving an existing user then password fields are not taken into account unless modified.
 */
class UserEditor(user: UserDomainObject) extends Editor with ImcmsServicesSupport {

  override type Data = UserDomainObject

  private val contactsEditor = new UserContactsEditor(user)

  override val view = new UserEditorView |>> { v =>
    v.sltUILanguage.addItem("eng", "English", Theme.Icon.Language.flag("eng"))
    v.sltUILanguage.addItem("swe", "Svenska", Theme.Icon.Language.flag("swe"))

    v.btnEditContacts.addClickHandler { _ =>
      val contactsEditorDialog = new OkCancelDialog("Contacts".i)

      contactsEditorDialog.mainComponent = contactsEditor.view
      contactsEditorDialog.setOkButtonHandler {
        contactsEditorDialog.close()
      }

      contactsEditorDialog.show()
    }
  }

  resetValues()

  override def resetValues() {
    view.chkEnabled.setValue(user.isActive)
    view.txtFirstName.setValue(user.getFirstName)

    view.txtLastName.setValue(user.getLastName)
    view.txtLogin.setValue(user.getLoginName)
    view.txtEmail.setValue(user.getEmailAddress)

    if (!user.isNew) {
      // for decoration only - show that password exists.
      view.txtPassword.setInputPrompt("***************")
      view.txtPasswordCheck.setInputPrompt("***************")
    }

    view.tcsRoles.removeAllItems()
    for (role <- imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getAllRolesExceptUsersRole) {
      view.tcsRoles.addItem(role.getId, role.getName)
    }

    view.tcsRoles.value = user.getRoleIds.filterNot(_ == RoleId.USERS).toSeq.asJava
    view.sltUILanguage.select(
      Set("eng", "swe").find(_ == user.getLanguageIso639_2).getOrElse(Current.imcmsUser.getLanguageIso639_2)
    )

    contactsEditor.resetValues()
  }

  override def collectValues(): UserEditor#ErrorsOrData = {
    val loginValidationErrorOpt = validateLogin(view.txtLogin.trimmedValue)
    val emailValidationErrorOpt = validateEmail(view.txtEmail.trimmedValue)
    val passwordValidationErrorOpt = validatePassword(view.txtPassword.value, view.txtPasswordCheck.value)

    Seq(view.lytLogin, view.txtEmail, view.lytPassword).foreach(_.setComponentError(null))

    if (loginValidationErrorOpt.isDefined) {
      view.lytLogin.setComponentError(new UserError(loginValidationErrorOpt.get))
    }

    if (emailValidationErrorOpt.isDefined) {
      view.txtEmail.setComponentError(new UserError(emailValidationErrorOpt.get))
    }

    if (passwordValidationErrorOpt.isDefined) {
      view.lytPassword.setComponentError(new UserError(passwordValidationErrorOpt.get))
    }

    Seq(loginValidationErrorOpt, emailValidationErrorOpt, passwordValidationErrorOpt).flatten match {
      case errors if errors.nonEmpty => Left(errors)
      case _ =>
        val u = user.clone()

        if (user.isNew || view.txtPassword.value.length > 0) {
          u.setPassword(view.txtPassword.value)
        }
        u.setActive(view.chkEnabled.checked)
        u.setFirstName(view.txtFirstName.trimmedValue)
        u.setLastName(view.txtLastName.trimmedValue)
        u.setLoginName(view.txtLogin.trimmedValue)

        u.setRoleIds(view.tcsRoles.value.asScala.toArray)
        u.setLanguageIso639_2(view.sltUILanguage.selection)
        u.setEmailAddress(view.txtEmail.trimmedValue)
        u.setRoleIds(view.tcsRoles.selection.toArray)

        Right(u)
    }
  }

  private def validateEmail(email: String): Option[String] = {
    email match {
      case "" => Some("Email must not be empty")
      case _ if !Utility.isValidEmail(email) => Some("Not a valid email address")
      case _ => imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUserByEmail(email).asOption match {
        case Some(existingUser) if existingUser.getId != user.getId =>
          Some(s"The email is already assigned to user ${existingUser.getLoginName}")
        case _ =>
          None
      }
    }
  }

  private def validatePassword(password: String, passwordCheck: String): Option[String] = {
    (password.length, passwordCheck.length) match {
      case (0, 0) if !user.isNew => None
      case (0, _) => Some("Password must not be empty")
      case (m, _) if m < 4 => Some("Password is too short - password length must be 4 to 15 symbols")
      case (m, _) if m > 15 => Some("Password is too long - password length must be 4 to 15 symbols")
      case (m, n) if m != n => Some("Password do not match")
      case _ => None
    }
  }

  private def validateLogin(login: String): Option[String] = {
    login match {
      case "" => Some("Login must not be empty")
      case _ => imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(login).asOption match {
        case Some(existingUser) if existingUser.getId != user.getId =>
          Some(s"The login is already in use")
        case _=>
          None
      }
    }
  }
}
