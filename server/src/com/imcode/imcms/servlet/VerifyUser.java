package com.imcode.imcms.servlet;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import com.imcode.imcms.servlet.superadmin.UserEditorPage;
import com.imcode.imcms.servlet.superadmin.AdminUser;
import com.imcode.imcms.flow.DispatchCommand;
import imcode.util.Utility;
import imcode.server.user.UserDomainObject;
import imcode.server.Imcms;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class VerifyUser extends HttpServlet {

    private static final String SESSION_ATTRIBUTE__NEXT_URL = "next_url";
    public static final String REQUEST_PARAMETER__NEXT_URL = SESSION_ATTRIBUTE__NEXT_URL;
    private static final String REQUEST_PARAMETER__NEXT_META = "next_meta";
    private static final String SESSION_ATTRIBUTE__NEXT_META = "next_meta";
    private static final String REQUEST_PARAMETER__LOGIN_FAILED_URL = "access_denied_url";
    private static final String SESSION_ATTRIBUTE__LOGIN_TARGET = "login.target";

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Utility.setDefaultHtmlContentType(res);

        String name = req.getParameter("name");
        String passwd = req.getParameter("passwd");
        String accessDeniedUrl = req.getContextPath() + "/imcms/" + Utility.getLoggedOnUser(req).getLanguageIso639_2()
                                 + "/login/access_denied.jsp" ;

        ContentManagementSystem cms = ContentManagementSystem.login(req, name, passwd);

        if ( null != cms ) {
            if ( req.getParameter("Ändra") != null ) {
                goToEditUserPage(cms.getCurrentUser(), res, accessDeniedUrl, req);
            } else {
                goToLoginSuccessfulPage(req, res);
            }
        } else {
            goToLoginFailedPage(req, accessDeniedUrl, res);
        }
    }

    private void goToLoginFailedPage(HttpServletRequest req, String accessDeniedUrl,
                                      HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(true);

        String nexturl = accessDeniedUrl ;

        if ( req.getParameter(REQUEST_PARAMETER__NEXT_META) != null ) {
            session.setAttribute(SESSION_ATTRIBUTE__NEXT_META, req.getParameter(REQUEST_PARAMETER__NEXT_META));
        } else if ( req.getParameter(REQUEST_PARAMETER__NEXT_URL) != null ) {
            session.setAttribute(SESSION_ATTRIBUTE__NEXT_URL, req.getParameter(REQUEST_PARAMETER__NEXT_URL));
        }
        if ( req.getParameter(REQUEST_PARAMETER__LOGIN_FAILED_URL) != null ) {
            nexturl = req.getParameter(REQUEST_PARAMETER__LOGIN_FAILED_URL);
        }
        res.sendRedirect(nexturl);
    }

    private void goToLoginSuccessfulPage(HttpServletRequest req,
                                         HttpServletResponse res) throws IOException, ServletException {

        new GoToLoginSuccessfulPageCommand().dispatch(req, res);
    }

    private void goToEditUserPage(User user, HttpServletResponse res, String accessDeniedUrl,
                                  HttpServletRequest req) throws IOException, ServletException {
        if ( user.isDefaultUser() ) {
            res.sendRedirect(accessDeniedUrl);
        } else {
            UserDomainObject internalUser = Imcms.getServices().getImcmsAuthenticatorAndUserAndRoleMapper().getUser(user.getId()) ;
            DispatchCommand returnCommand = new GoToLoginSuccessfulPageCommand();
            UserEditorPage userEditorPage = new UserEditorPage(internalUser, new AdminUser.SaveUserAndReturnCommand(internalUser, returnCommand), returnCommand);
            userEditorPage.forward(req, res);
        }
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

