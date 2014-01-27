package com.imcode
package imcms
package admin
package access.user

import _root_.java.lang.String
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
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

  override val view = new UserEditorView |>> { v =>
    v.account.sltUILanguage.addItem("eng", "English", Theme.Icon.Language.flag("eng"))
    v.account.sltUILanguage.addItem("swe", "Svenska", Theme.Icon.Language.flag("swe"))
  }

  resetValues()

  override def resetValues() {
    view.account.chkEnabled.setValue(user.isActive)
    view.account.txtFirstName.setValue(user.getFirstName)

    view.account.txtLastName.setValue(user.getLastName)
    view.account.txtLoginName.setValue(user.getLoginName)
    view.account.txtEmail.setValue(user.getEmailAddress)

    if (!user.isNew) {
      // for decoration only - show that password exists.
      view.account.txtPassword.setInputPrompt("***************")
      view.account.txtPasswordCheck.setInputPrompt("***************")
    }

    view.account.tcsRoles.removeAllItems()
    for (role <- imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getAllRolesExceptUsersRole) {
      view.account.tcsRoles.addItem(role.getId, role.getName)
    }

    view.account.tcsRoles.selection = user.getRoleIds.filterNot(_ == RoleId.USERS).to[Seq]
    view.account.sltUILanguage.select(
      Set("eng", "swe").find(_ == user.getLanguageIso639_2).getOrElse(Current.imcmsUser.getLanguageIso639_2)
    )
  }

  override def collectValues(): UserEditor#ErrorsOrData = {
    val loginValidationErrorOpt = validateLogin(view.account.txtLoginName.trimmedValue)
    val emailValidationErrorOpt = validateEmail(view.account.txtEmail.trimmedValue)
    val passwordValidationErrorOpt = validatePassword(view.account.txtPassword.value, view.account.txtPasswordCheck.value)

    Seq(view.account.lytLogin, view.account.txtEmail, view.account.lytPassword).foreach(_.setComponentError(null))
    view.getTab(0).setComponentError(null)

    if (loginValidationErrorOpt.isDefined) {
      view.account.lytLogin.setComponentError(new UserError(loginValidationErrorOpt.get))
    }

    if (emailValidationErrorOpt.isDefined) {
      view.account.txtEmail.setComponentError(new UserError(emailValidationErrorOpt.get))
    }

    if (passwordValidationErrorOpt.isDefined) {
      view.account.lytPassword.setComponentError(new UserError(passwordValidationErrorOpt.get))
    }

    Seq(loginValidationErrorOpt, emailValidationErrorOpt, passwordValidationErrorOpt).flatten match {
      case errors if errors.nonEmpty =>
        view.getTab(0).setComponentError(new UserError(errors.mkString(", ")))
        Left(errors)
      case _ =>
        val u = user.clone()

        if (user.isNew || view.account.txtPassword.value.length > 0) {
          u.setPassword(view.account.txtPassword.value)
        }
        u.setActive(view.account.chkEnabled.checked)
        u.setFirstName(view.account.txtFirstName.trimmedValue)
        u.setLastName(view.account.txtLastName.trimmedValue)
        u.setLoginName(view.account.txtLoginName.trimmedValue)

        u.setRoleIds(view.account.tcsRoles.selection.toArray)
        u.setLanguageIso639_2(view.account.sltUILanguage.firstSelected)
        u.setEmailAddress(view.account.txtEmail.trimmedValue)
        u.setRoleIds(view.account.tcsRoles.selection.toArray)

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
