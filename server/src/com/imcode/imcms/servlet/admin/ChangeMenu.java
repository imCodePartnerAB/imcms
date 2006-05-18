package com.imcode.imcms.servlet.admin;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import java.io.IOException;

public class ChangeMenu extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher ;
        if (gotParameter(request, "copy") 
            || gotParameter(request, "delete")
            || gotParameter(request, "archive")
            || gotParameter(request, "sort") ) {
            dispatcher = request.getRequestDispatcher("/servlet/SaveSort") ;
        } else {
            dispatcher = request.getRequestDispatcher("/servlet/AddDoc") ;
        }
        
        dispatcher.forward(request, response);
    }

    private boolean gotParameter(HttpServletRequest request, String parameterName) {
        return null != request.getParameter(parameterName);
    }

}
