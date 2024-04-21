package com.imcode.imcms.servlet;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import com.imcode.imcms.domain.component.UserLockValidator;
import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.servlet.superadmin.AdminUser;
import com.imcode.imcms.servlet.superadmin.UserEditorPage;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static imcode.server.ImcmsConstants.API_PREFIX;
import static imcode.server.ImcmsConstants.LOGIN_URL;

public class VerifyUser extends HttpServlet {

    public static final String REQUEST_PARAMETER__NEXT_META = "next_meta";
    public static final String REQUEST_PARAMETER__EDIT_USER = "edit_user";
    public static final String REQUEST_PARAMETER__USERNAME = "name";
    public static final String REQUEST_PARAMETER__PASSWORD = "passwd";
    public static final String REQUEST_PARAMETER__OTP = "oneTimePassword";
    public static final String REQUEST_ATTRIBUTE__ERROR = "error";
    public static final String REQUEST_ATTRIBUTE__WAIT_TIME = "time_error";
    public static final String REQUEST_ATTRIBUTE__INFO_LEFT_ATTEMPTS = "left_attempts_info";
    public static final String DEFAULT_START_PAGE_URL = "/servlet/StartDoc";
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
    private final static LocalizedMessage ERROR__ATTEMPTS_EXHAUSTED = new LocalizedMessage("templates/login/access_denied.html/5");
    private final static LocalizedMessage LEFT__ATTEMPTS_INFO = new LocalizedMessage("templates/login/access_denied.html/6");

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

        String name = StringUtils.defaultString(req.getParameter(REQUEST_PARAMETER__USERNAME), (String) req.getSession().getAttribute(REQUEST_PARAMETER__USERNAME));
        String passwd = Utility.unescapeValue(StringUtils.defaultString(
                req.getParameter(REQUEST_PARAMETER__PASSWORD),
                (String) req.getSession().getAttribute(REQUEST_PARAMETER__PASSWORD)
        ));

	    if (StringUtils.isAnyBlank(name, passwd)) {
            goToLoginFailedPage(req, res, null);
            return;
        }

	    Imcms.getServices().getMultiFactorAuthenticationService().cleanSession(req.getSession());
	    ImcmsAuthenticatorAndUserAndRoleMapper userAndRoleMapper = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper();
        ContentManagementSystem cms = null;
        UserDomainObject userToCheck = Imcms.getServices().verifyUser(name, passwd);
        boolean isAllowed = userAndRoleMapper.isAllowedToAccess(req.getRemoteAddr(), userToCheck);
        final UserDomainObject userByLogin = userAndRoleMapper.getUserByLoginIgnoreCase(name);

        if (isAllowed) {
            cms = ContentManagementSystem.login(req, res, userToCheck);
        }

        if (null != cms) {
            User currentUser = cms.getCurrentUser();
	        if ((req.getParameter(REQUEST_PARAMETER__EDIT_USER) != null
			        || req.getSession().getAttribute(REQUEST_PARAMETER__EDIT_USER) != null)
			        && !currentUser.isDefaultUser()) {
                goToEditUserPage(currentUser, res, req);
            } else {
                goToLoginSuccessfulPage(req, res);
            }
        } else if (Imcms.getServices().getMultiFactorAuthenticationService().isInProgress(req)) {
	        goToMFAPage(req, res);
        } else {
            goToLoginFailedPage(req, res, userByLogin);
        }
    }

	private void goToMFAPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(API_PREFIX.concat("/mfa")).forward(request, response);
	}

    private void goToLoginFailedPage(HttpServletRequest req, HttpServletResponse res, UserDomainObject user) throws IOException, ServletException {
        final UserLockValidator userLockValidator = Imcms.getServices().getUserLockValidator();
        if (userLockValidator.isUserBlocked(user)) {
            req.setAttribute(REQUEST_ATTRIBUTE__ERROR, ERROR__ATTEMPTS_EXHAUSTED);
            req.setAttribute(REQUEST_ATTRIBUTE__WAIT_TIME, userLockValidator.getRemainingWaitTime(user));
        } else if (req.getAttribute(REQUEST_ATTRIBUTE__ERROR) == null) {
            req.setAttribute(REQUEST_ATTRIBUTE__ERROR, ERROR__LOGIN_FAILED);
        }

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

        private final Logger log = LogManager.getLogger(GoToLoginSuccessfulPageCommand.class);

        public void dispatch(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
            String nexturl = DEFAULT_START_PAGE_URL;
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

            try {
                if (nexturl.startsWith("/") && !nexturl.startsWith("//")) {
                    nexturl = request.getContextPath() + nexturl;
                } else {
                    final URI uri = new URI(request.getRequestURL().toString());
                    final String host = uri.getHost();
                    if (!nexturl.contains(host)) {
                        Utility.redirectToStartDocument(request, response);
                        return;
                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
                log.error("GoToLoginSuccessfulPage URI not correct as " + request.getRequestURL().toString());
                Utility.redirectToStartDocument(request, response);
                return;
            }

            response.sendRedirect(nexturl);
        }
    }
}

