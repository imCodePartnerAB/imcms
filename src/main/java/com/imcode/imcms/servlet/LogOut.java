package com.imcode.imcms.servlet;

import imcode.server.ImcmsConstants;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogOut extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        Utility.removeRememberCdCookie(req, res);
	    req.setAttribute("userLanguage", Utility.getUserLanguage(req.getCookies()));
        Cookie cookie = new Cookie(ImcmsSetupFilter.USER_LOGGED_IN_COOKIE_NAME, Boolean.toString(false));
        cookie.setMaxAge(0);
        cookie.setPath("/");
        res.addCookie(cookie);
        req.getRequestDispatcher(ImcmsConstants.LOGOUT_URL).forward(req, res);

        Utility.makeUserLoggedOut(req, res);
    }
}