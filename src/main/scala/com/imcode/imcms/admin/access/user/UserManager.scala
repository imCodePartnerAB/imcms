package com.imcode
package imcms
package admin.access.user

import _root_.imcode.server.user._

import com.imcode.imcms.vaadin.Current
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.component.dialog._
import com.imcode.imcms.vaadin.server._

import com.vaadin.ui.Component
import com.imcode.imcms.admin.access.user.projection.UsersProjection

// todo: add security check
// fixme: search for user w/o roles
// todo: superadmin: disable roles editing || disallow superadmin role removal
// fixme: interface language: I18n
class UserManager extends ImcmsServicesSupport {

  private val usersProjection = new UsersProjection

  val view: Component = new UserManagerView(usersProjection.view) |>> { v =>
    v.miNew.setCommandHandler { _ =>
      editUser(new UserDomainObject)
    }

    v.miEdit.setCommandHandler { _ =>
      whenSingleton(usersProjection.selection) { user =>
        editUser(user)
      }
    }

    usersProjection.listen { selection => v.miEdit.setEnabled(selection.size == 1) }
    usersProjection.notifyListeners()
  }

  /**
   * Edit and save new or existing user
   * @param user user to edit
   */
  private def editUser(user: UserDomainObject) {
    val editor = new UserEditor(user)
    val dialogTitle = if (user.isNew) "user_dlg.new.caption".i else "user_dlg.edit.caption".f(user.getLoginName)
    val dialog = new OkCancelDialog(dialogTitle) with OKCaptionIsSave

    EditorDialog.bind(dialog, editor) { editedUser =>
      val roleMapper = imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper
      if (user.isNew) roleMapper.addUser(editedUser) else roleMapper.saveUser(editedUser)
      usersProjection.reset()
      Current.page.showInfoNotification("User has been saved")
    }

    dialog.show()
  }
}



/*
  userFinder.isNullSelectable()???


        // Security check
        // Lets verify that the user is an admin, otherwise throw him out.
        if ( !user.isSuperAdmin() && !user.isUserAdminAndCanEditAtLeastOneRole() ) {
            String header = "Error in AdminUser.";
            Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            log.debug(header + "- user is not an administrator");
            AdminRoles.printErrorMessage(req, res, header, msg);
            return;
        }
----------------

          UserFinder userFinder = (UserFinder)HttpSessionUtils.getSessionAttributeWithNameInRequest( request, REQUEST_ATTRIBUTE_PARAMETER__USER_BROWSE );
        if ( null == userFinder ) {
            Utility.redirectToStartDocument(request, response);
        } else if ( null != request.getParameter( REQUEST_PARAMETER__SHOW_USERS_BUTTON ) ) {
            listUsers( request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__SELECT_USER_BUTTON ) ) {
            UserDomainObject selectedUser = getSelectedUserFromRequest( request );
            if ( null == selectedUser && !userFinder.isNullSelectable() ) {
                listUsers( request, response );
            } else {
                userFinder.selectUser( selectedUser, request, response );
            }
        } else if ( null != request.getParameter( REQUEST_PARAMETER__CANCEL_BUTTON ) ) {
            userFinder.cancel( request, response );
        } else if ( null != request.getParameter( REQUEST_PARAMETER__ADD_USER ) && userFinder.isUsersAddable() ) {
            goToCreateUserPage(userFinder, request, response);
        }
*/