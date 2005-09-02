package com.imcode.imcms.servlet;

import com.imcode.imcms.api.ContentManagementSystem;
import com.imcode.imcms.api.User;
import imcode.util.Utility;

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
                                         HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(true);
        String nexturl = "StartDoc";

        if ( session.getAttribute(SESSION_ATTRIBUTE__NEXT_META) != null ) {
            nexturl = "GetDoc?meta_id=" + session.getAttribute(SESSION_ATTRIBUTE__NEXT_META);
            session.removeAttribute(SESSION_ATTRIBUTE__NEXT_META);
        } else if ( session.getAttribute(SESSION_ATTRIBUTE__NEXT_URL) != null ) {
            nexturl = (String) session.getAttribute(SESSION_ATTRIBUTE__NEXT_URL);
            session.removeAttribute(SESSION_ATTRIBUTE__NEXT_URL);
        } else if ( req.getParameter(REQUEST_PARAMETER__NEXT_URL) != null ) {
            nexturl = req.getParameter(REQUEST_PARAMETER__NEXT_URL);
        } else if ( req.getParameter(REQUEST_PARAMETER__NEXT_META) != null ) {
            nexturl = "GetDoc?meta_id=" + req.getParameter(REQUEST_PARAMETER__NEXT_META);
        } else if ( session.getAttribute(SESSION_ATTRIBUTE__LOGIN_TARGET) != null ) {
            nexturl = (String) session.getAttribute(SESSION_ATTRIBUTE__LOGIN_TARGET);
            session.removeAttribute(SESSION_ATTRIBUTE__LOGIN_TARGET);
        }

        res.sendRedirect(nexturl);
    }

    private void goToEditUserPage(User user, HttpServletResponse res, String accessDeniedUrl,
                                  HttpServletRequest req) throws IOException {
        if ( user.isDefaultUser() ) {
            res.sendRedirect(accessDeniedUrl);
        } else {
            String nexturl = "StartDoc" ;
            if ( req.getParameter(REQUEST_PARAMETER__NEXT_URL) != null ) {
                nexturl = req.getParameter(REQUEST_PARAMETER__NEXT_URL);
            } else if ( req.getParameter(REQUEST_PARAMETER__NEXT_META) != null ) {
                nexturl = "GetDoc?meta_id=" + req.getParameter(REQUEST_PARAMETER__NEXT_META);
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("userToChange", "" + user.getId());
            session.setAttribute(SESSION_ATTRIBUTE__NEXT_URL, nexturl);

            res.sendRedirect("AdminUserProps?CHANGE_USER=true");
        }
    }
}

