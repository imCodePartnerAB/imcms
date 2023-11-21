package com.imcode.imcms.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StartDoc extends HttpServlet {

    private static final long serialVersionUID = -7352117429674871492L;

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // just redirect to new controller
        response.sendRedirect(request.getContextPath() + "/");
    }
}
