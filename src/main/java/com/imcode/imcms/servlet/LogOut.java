package com.imcode.imcms.servlet;

import imcode.server.ImcmsConstants;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogOut extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

	    req.setAttribute("userLanguage", Utility.getUserLanguageFromCookie(req.getCookies()).getCode());
        req.getRequestDispatcher(ImcmsConstants.LOGOUT_URL).forward(req, res);

        Utility.makeUserLoggedOut(req, res);
    }
}
