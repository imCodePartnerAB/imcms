package com.imcode.imcms.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static imcode.server.ImcmsConstants.API_VIEW_DOC_PATH;

public class StartDoc extends HttpServlet {

    private static final long serialVersionUID = -7352117429674871492L;

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // just forward to new controller
        request.getRequestDispatcher(API_VIEW_DOC_PATH).forward(request, response);
    }
}
