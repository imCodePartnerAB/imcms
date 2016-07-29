package com.imcode.imcms.servlet;

import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogOut extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String language = Utility.getLoggedOnUser(req).getLanguageIso639_2();

        Utility.removeRememberCdCookie(req, res);
        req.setAttribute("language", language);
        Cookie cookie = new Cookie("userLoggedIn", "true");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        res.addCookie(cookie);
        req.getRequestDispatcher("/login/logged_out.jsp").forward(req, res);

        Utility.makeUserLoggedOut(req);
    }
}