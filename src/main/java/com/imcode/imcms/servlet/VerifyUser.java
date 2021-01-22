package com.imcode.imcms.servlet;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.servlet.superadmin.AdminUser;
import com.imcode.imcms.servlet.superadmin.UserEditorPage;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static imcode.server.ImcmsConstants.API_PREFIX;
import static imcode.server.ImcmsConstants.LOGIN_URL;

public class VerifyUser extends HttpServlet {

    public static final String REQUEST_PARAMETER__NEXT_META = "next_meta";
    public static final String REQUEST_PARAMETER__EDIT_USER = "edit_user";
    public static final String REQUEST_PARAMETER__USERNAME = "name";
    public static final String REQUEST_PARAMETER__PASSWORD = "passwd";
    public static final String REQUEST_ATTRIBUTE__ERROR = "error";
    /**
     * Too many sessions message key.
     */
    private final static LocalizedMessage LOGIN_MSG__TOO_MANY_SESSIONS
            = new LocalizedMessage("templates/login/TooManySessions");
    private static final String SESSION_ATTRIBUTE__NEXT_URL = "next_url";
    public static final String REQUEST_PARAMETER__NEXT_URL = SESSION_ATTRIBUTE__NEXT_URL;
    private static final String SESSION_ATTRIBUTE__NEXT_META = REQUEST_PARAMETER__NEXT_META;
    private static final String SESSION_ATTRIBUTE__LOGIN_TARGET = "login.target";
    private final static LocalizedMessage ERROR__LOGIN_FAILED = new LocalizedMessage("templates/login/access_denied.html/4");

    public static void forwardToLogin(HttpServletRequest req, HttpServletResponse res, LocalizedMessage errorMsg) throws IOException, ServletException {
        req.getSession().invalidate();
        req.setAttribute(REQUEST_ATTRIBUTE__ERROR, errorMsg);
        req.getRequestDispatcher(API_PREFIX.concat(LOGIN_URL)).forward(req, res);
    }

    public static void forwardToLoginPageTooManySessions(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        forwardToLogin(req, res, LOGIN_MSG__TOO_MANY_SESSIONS);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Utility.setDefaultHtmlContentType(res);

        String name = req.getParameter(REQUEST_PARAMETER__USERNAME);
        String passwd = req.getParameter(REQUEST_PARAMETER__PASSWORD);

        if ((name == null) || (passwd == null)) {
            goToLoginFailedPage(req, res);
            return;
        }

        ImcmsAuthenticatorAndUserAndRoleMapper userAndRoleMapper = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        ContentManagementSystem cms = null;
        UserDomainObject userToCheck = Imcms.getServices().verifyUser(name, passwd);
        boolean isAllowed = userAndRoleMapper.isAllowedToAccess(req.getRemoteAddr(), userToCheck);

        if (isAllowed) {
            cms = ContentManagementSystem.login(req, userToCheck);
        }

        if (null != cms) {
            User currentUser = cms.getCurrentUser();
            if (req.getParameter(REQUEST_PARAMETER__EDIT_USER) != null && !currentUser.isDefaultUser()) {
                goToEditUserPage(currentUser, res, req);
            } else {
                goToLoginSuccessfulPage(req, res);
            }
        } else {
            goToLoginFailedPage(req, res);
        }
    }

    private void goToLoginFailedPage(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        req.setAttribute(REQUEST_ATTRIBUTE__ERROR, ERROR__LOGIN_FAILED);
        req.getRequestDispatcher(API_PREFIX.concat(LOGIN_URL)).forward(req, res);
    }


    private void goToLoginSuccessfulPage(HttpServletRequest req,
                                         HttpServletResponse res) throws IOException {

        new GoToLoginSuccessfulPageCommand().dispatch(req, res);
    }

    private void goToEditUserPage(User user, HttpServletResponse response, HttpServletRequest request) throws IOException, ServletException {
        UserDomainObject internalUser = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getUser(user.getId());
        DispatchCommand returnCommand = new GoToLoginSuccessfulPageCommand();
        UserEditorPage userEditorPage = new UserEditorPage(internalUser, new AdminUser.SaveUserAndReturnCommand(internalUser, returnCommand), returnCommand);
        userEditorPage.forward(request, response);
    }

    private static class GoToLoginSuccessfulPageCommand implements DispatchCommand {
        public void dispatch(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
            String nexturl = "/";
            HttpSession session = request.getSession(true);
            if (session.getAttribute(SESSION_ATTRIBUTE__NEXT_META) != null) {
                nexturl = "GetDoc?meta_id=" + session.getAttribute(SESSION_ATTRIBUTE__NEXT_META);
                session.removeAttribute(SESSION_ATTRIBUTE__NEXT_META);
            } else if (session.getAttribute(SESSION_ATTRIBUTE__NEXT_URL) != null) {
                nexturl = (String) session.getAttribute(SESSION_ATTRIBUTE__NEXT_URL);
                session.removeAttribute(SESSION_ATTRIBUTE__NEXT_URL);
            } else if (request.getParameter(REQUEST_PARAMETER__NEXT_URL) != null) {
                nexturl = request.getParameter(REQUEST_PARAMETER__NEXT_URL);
            } else if (request.getParameter(REQUEST_PARAMETER__NEXT_META) != null) {
                nexturl = "GetDoc?meta_id=" + request.getParameter(REQUEST_PARAMETER__NEXT_META);
            } else if (session.getAttribute(SESSION_ATTRIBUTE__LOGIN_TARGET) != null) {
                nexturl = (String) session.getAttribute(SESSION_ATTRIBUTE__LOGIN_TARGET);
                session.removeAttribute(SESSION_ATTRIBUTE__LOGIN_TARGET);
            }
            response.sendRedirect(nexturl);
        }
    }
}

