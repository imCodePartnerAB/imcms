package com.imcode.imcms.servlet.superadmin;

import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.servlet.admin.UserFinder;
import com.imcode.imcms.util.l10n.ImcmsPrefsLocalizedMessageProvider;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Properties;

public class AdminUser extends HttpServlet {

    public final static String USER_LOGIN_NAME_PARAMETER_NAME = "loginname";
    private final static Logger log = Logger.getLogger(AdminUser.class.getName());
    private final static String CHANGE_EXTERNAL_USER_URL = "/jsp/changeexternaluser.jsp";
    private static final LocalizedMessage BUTTON_TEXT__EDIT_USER = new LocalizedMessage("templates/sv/AdminChangeUser.htm/2006");
    private static final LocalizedMessage HEADLINE__EDIT_USER = new LocalizedMessage("templates/sv/AdminChangeUser.htm/4/1");
    private static final long serialVersionUID = -2629630588609905055L;

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        final UserDomainObject user = Utility.getLoggedOnUser(req);
        Utility.setDefaultHtmlContentType(res);

        // Lets verify that the user is an admin, otherwise throw him out.
        if (!user.isSuperAdmin() && !user.isUserAdminAndCanEditAtLeastOneRole()) {
            String header = "Error in AdminUser.";
            Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
            String msg = langproperties.getProperty("error/servlet/global/no_administrator") + "<br>";
            log.debug(header + "- user is not an administrator");
            AdminRoles.printErrorMessage(req, res, header, msg);
            return;
        }

        UserFinder userFinder = new UserFinder();
        userFinder.setUsersAddable(true);
        userFinder.setHeadline(HEADLINE__EDIT_USER);
        userFinder.setSelectButtonText(BUTTON_TEXT__EDIT_USER);
        userFinder.setSelectUserCommand(
                (selectedUser, request, response) -> gotoChangeUser(request, response, user, selectedUser)
        );
        userFinder.setCancelCommand(
                (request, response) -> request.getRequestDispatcher("AdminManager").forward(request, response)
        );
        userFinder.forward(req, res);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("AdminManager");
    }

    private void gotoChangeUser(HttpServletRequest req, HttpServletResponse res, UserDomainObject user,
                                UserDomainObject userToChange) throws IOException, ServletException {
        if (!userToChange.isImcmsExternal()) {
            redirectChangeUser(req, res, user, userToChange);
        } else {
            String queryString = "?"
                    + URLEncoder.encode(USER_LOGIN_NAME_PARAMETER_NAME, "UTF-8")
                    + "="
                    + URLEncoder.encode(userToChange.getLoginName(), "UTF-8");
            RequestDispatcher rd = req.getRequestDispatcher("/imcms/" + user.getLanguageIso639_2()
                    + CHANGE_EXTERNAL_USER_URL
                    + queryString);
            rd.forward(req, res);
        }
    }

    private void redirectChangeUser(HttpServletRequest req, HttpServletResponse res, UserDomainObject user,
                                    final UserDomainObject userToChange) throws IOException, ServletException {

        if (!user.isSuperAdmin() && !user.isUserAdminAndCanEditAtLeastOneRole() && !userToChange.equals(user)) {
            String header = "Error in AdminUser, change user.";
            Properties langproperties = ImcmsPrefsLocalizedMessageProvider.getLanguageProperties(user);
            String msg = langproperties.getProperty("error/servlet/AdminUser/user_have_no_permission") + "<br>";
            log.debug(header + "- user have no permission to edit user values");
            AdminRoles.printErrorMessage(req, res, header, msg);
        } else {
            final DispatchCommand returnCommand = this::doGet;
            DispatchCommand saveAndReturnCommand = new SaveUserAndReturnCommand(userToChange, returnCommand);
            UserEditorPage userEditorPage = new UserEditorPage(userToChange, saveAndReturnCommand, returnCommand);
            userEditorPage.forward(req, res);
        }
    }

    public static class SaveUserAndReturnCommand implements DispatchCommand {
        private static final long serialVersionUID = 7199727572954498276L;
        private final UserDomainObject userToChange;
        private final DispatchCommand returnCommand;

        public SaveUserAndReturnCommand(UserDomainObject userToChange, DispatchCommand returnCommand) {
            this.userToChange = userToChange;
            this.returnCommand = returnCommand;
        }

        public void dispatch(HttpServletRequest request,
                             HttpServletResponse response) throws IOException, ServletException {
            Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().saveUser(userToChange);
            returnCommand.dispatch(request, response);
        }
    }
}
