package com.imcode.imcms.servlet.superadmin;

import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Feature allows to define IP white list per user role.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 20.11.17.
 */
public class AdminIpWhiteList extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String language = Utility.getLoggedOnUser(request).getLanguageIso639_2();
        final String templatePath = "/WEB-INF/templates/" + language + "/admin/AdminIpWhiteList.jsp";

        response.setContentType("text/html");

        request.setAttribute("contextPath", request.getContextPath());
        request.setAttribute("language", language);

        request.getRequestDispatcher(templatePath).forward(request, response);
    }
}
