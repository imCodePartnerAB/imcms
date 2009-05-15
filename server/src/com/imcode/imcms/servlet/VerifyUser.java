package com.imcode.imcms.servlet;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import com.imcode.imcms.flow.DispatchCommand;
import com.imcode.imcms.servlet.superadmin.AdminUser;
import com.imcode.imcms.servlet.superadmin.UserEditorPage;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class VerifyUser extends HttpServlet {

    private static final String SESSION_ATTRIBUTE__NEXT_URL = "next_url";
    public static final String REQUEST_PARAMETER__NEXT_URL = SESSION_ATTRIBUTE__NEXT_URL;
    public static final String REQUEST_PARAMETER__NEXT_META = "next_meta";
    private static final String SESSION_ATTRIBUTE__NEXT_META = "next_meta";
    private static final String SESSION_ATTRIBUTE__LOGIN_TARGET = "login.target";
    public static final String REQUEST_PARAMETER__EDIT_USER = "edit_user";
    public static final String REQUEST_PARAMETER__USERNAME = "name";
    public static final String REQUEST_PARAMETER__PASSWORD = "passwd";
    public static final String REQUEST_ATTRIBUTE__ERROR = "error";

    private final static LocalizedMessage ERROR__LOGIN_FAILED = new LocalizedMessage("templates/login/access_denied.html/4");
    
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Utility.setDefaultHtmlContentType(res);

        String name = req.getParameter(REQUEST_PARAMETER__USERNAME);
        String passwd = req.getParameter(REQUEST_PARAMETER__PASSWORD);

        ContentManagementSystem cms = ContentManagementSystem.login(req, res, name, passwd);

        if ( null != cms ) {
            User currentUser = cms.getCurrentUser();
            if ( req.getParameter(REQUEST_PARAMETER__EDIT_USER) != null && !currentUser.isDefaultUser() ) {
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
        UserDomainObject internalUser = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getUser(user.getId()) ;
        DispatchCommand returnCommand = new GoToLoginSuccessfulPageCommand();
        UserEditorPage userEditorPage = new UserEditorPage(internalUser, new AdminUser.SaveUserAndReturnCommand(internalUser, returnCommand), returnCommand);
        userEditorPage.forward(req, res);
    }

    private static class GoToLoginSuccessfulPageCommand implements DispatchCommand {
        public void dispatch(HttpServletRequest request,
                             HttpServletResponse response) throws IOException, ServletException {
            String nexturl = "StartDoc";
            HttpSession session = request.getSession(true);
            if ( session.getAttribute(SESSION_ATTRIBUTE__NEXT_META) != null ) {
                nexturl = "GetDoc?meta_id=" + session.getAttribute(SESSION_ATTRIBUTE__NEXT_META);
                session.removeAttribute(SESSION_ATTRIBUTE__NEXT_META);
            } else if ( session.getAttribute(SESSION_ATTRIBUTE__NEXT_URL) != null ) {
                nexturl = (String) session.getAttribute(SESSION_ATTRIBUTE__NEXT_URL);
                session.removeAttribute(SESSION_ATTRIBUTE__NEXT_URL);
            } else if ( request.getParameter(REQUEST_PARAMETER__NEXT_URL) != null ) {
                nexturl = request.getParameter(REQUEST_PARAMETER__NEXT_URL);
            } else if ( request.getParameter(REQUEST_PARAMETER__NEXT_META) != null ) {
                nexturl = "GetDoc?meta_id=" + request.getParameter(REQUEST_PARAMETER__NEXT_META);
            } else if ( session.getAttribute(SESSION_ATTRIBUTE__LOGIN_TARGET) != null ) {
                nexturl = (String) session.getAttribute(SESSION_ATTRIBUTE__LOGIN_TARGET);
                session.removeAttribute(SESSION_ATTRIBUTE__LOGIN_TARGET);
            }
            response.sendRedirect(nexturl);
        }
    }
}

