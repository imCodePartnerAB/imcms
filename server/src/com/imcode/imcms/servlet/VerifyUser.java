package com.imcode.imcms.servlet;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.services.TwoFactorService;
import com.imcode.imcms.servlet.superadmin.AdminUser;
import com.imcode.imcms.servlet.superadmin.UserEditorPage;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.AuthenticationMethodConfiguration;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

import static com.imcode.imcms.services.TwoFactorService.PROPERTY_NAME_2FA;

public class VerifyUser extends HttpServlet {

    public static final String SESSION_ATTRIBUTE__NEXT_URL = "next_url";
    public static final String REQUEST_PARAMETER__NEXT_URL = SESSION_ATTRIBUTE__NEXT_URL;
    public static final String REQUEST_PARAMETER__NEXT_META = "next_meta";
    public static final String SESSION_ATTRIBUTE__NEXT_META = "next_meta";
    public static final String REQUEST_PARAMETER__EDIT_USER = "edit_user";
    public static final String REQUEST_PARAMETER__USERNAME = "name";
    public static final String REQUEST_PARAMETER__PASSWORD = "passwd";
    public static final String REQUEST_ATTRIBUTE__ERROR = "error";
    private static final Log log = LogFactory.getLog(VerifyUser.class);
    private static final String SESSION_ATTRIBUTE__LOGIN_TARGET = "login.target";
    private final static LocalizedMessage ERROR__LOGIN_FAILED = new LocalizedMessage("templates/login/access_denied.html/4");

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ContentManagementSystem cms = null;

        Utility.setDefaultHtmlContentType(res);
        final ImcmsServices services = Imcms.getServices();
        final Map<String, AuthenticationMethodConfiguration> loginConfiguration = services.getConfig().getAuthenticationConfiguration();

        String name = req.getParameter(REQUEST_PARAMETER__USERNAME);
        String passwd = req.getParameter(REQUEST_PARAMETER__PASSWORD);


        final ImcmsAuthenticatorAndUserAndRoleMapper userAndRoleMapper = services.getImcmsAuthenticatorAndUserAndRoleMapper();
        try {
            final boolean is2FA = loginConfiguration.containsKey(PROPERTY_NAME_2FA);
            if (is2FA) {
                cms = TwoFactorService.getInstance().initOrCheck(req, res, name, passwd);
            } else {
                cms = ContentManagementSystem.login(req, res, name, passwd);
            }
        } catch (UserIpIsNotAllowedException e) {
            userAndRoleMapper.forwardDeniedUserToMessagePage(e.getUser(), req, res);
            return;
        }

        if (null != cms) {
            final User currentUser = cms.getCurrentUser();
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
        req.getRequestDispatcher("/imcms/" + Utility.getLoggedOnUser(req).getLanguageIso639_2()
                + "/login/index.jsp").forward(req, res);
    }

    private void goToLoginSuccessfulPage(HttpServletRequest req,
                                         HttpServletResponse res) throws IOException, ServletException {

        new GoToLoginSuccessfulPageCommand().dispatch(req, res);
    }

    private void goToEditUserPage(User user, HttpServletResponse res, HttpServletRequest req) throws IOException, ServletException {
        final UserDomainObject internalUser = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getUser(user.getId());
        final DispatchCommand returnCommand = new GoToLoginSuccessfulPageCommand();
        final UserEditorPage userEditorPage = new UserEditorPage(internalUser, new AdminUser.SaveUserAndReturnCommand(internalUser, returnCommand), returnCommand);
        userEditorPage.forward(req, res);
    }

    public static class GoToLoginSuccessfulPageCommand implements DispatchCommand {
        public void dispatch(final HttpServletRequest request,
                             final HttpServletResponse response) throws IOException, ServletException {
            String nexturl = "/servlet/StartDoc";
            final HttpSession session = request.getSession(true);
            if (session.getAttribute(SESSION_ATTRIBUTE__NEXT_META) != null) {
                nexturl = "/servlet/GetDoc?meta_id=" + session.getAttribute(SESSION_ATTRIBUTE__NEXT_META);
                session.removeAttribute(SESSION_ATTRIBUTE__NEXT_META);
            } else if (session.getAttribute(SESSION_ATTRIBUTE__NEXT_URL) != null) {
                nexturl = (String) session.getAttribute(SESSION_ATTRIBUTE__NEXT_URL);
                session.removeAttribute(SESSION_ATTRIBUTE__NEXT_URL);
            } else if (request.getParameter(REQUEST_PARAMETER__NEXT_URL) != null) {
                nexturl = request.getParameter(REQUEST_PARAMETER__NEXT_URL);
            } else if (request.getParameter(REQUEST_PARAMETER__NEXT_META) != null) {
                nexturl = "/servlet/GetDoc?meta_id=" + request.getParameter(REQUEST_PARAMETER__NEXT_META);
            } else if (session.getAttribute(SESSION_ATTRIBUTE__LOGIN_TARGET) != null) {
                nexturl = (String) session.getAttribute(SESSION_ATTRIBUTE__LOGIN_TARGET);
                session.removeAttribute(SESSION_ATTRIBUTE__LOGIN_TARGET);
            }
            response.sendRedirect(nexturl.startsWith("/") ? request.getContextPath() + nexturl : nexturl);
        }
    }
}

