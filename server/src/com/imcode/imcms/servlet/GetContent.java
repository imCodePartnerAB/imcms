package com.imcode.imcms.servlet;

import imcode.util.Utility;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

public class GetContent extends HttpServlet {
    private static final String JSP__GET_CONTENT = "demo_content.jsp";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contents = null;
        try {
            contents = Utility.getContents("/WEB-INF/templates/text/" + JSP__GET_CONTENT, request, response);
        }
        catch(Exception ex) {
            ex.printStackTrace(response.getWriter());
        }
        if (contents != null) {
            response.getWriter().write(contents);
        }
    }
}
