package com.imcode.imcms.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReDirecter extends HttpServlet {

    private final static int METAID_OFFSET = 2;

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws IOException, ServletException {

        String[] pathElements = req.getPathInfo().split("/") ;

        if ( pathElements.length > METAID_OFFSET ) {
            res.sendRedirect( req.getContextPath() + "/servlet/GetDoc?meta_id=" + pathElements[METAID_OFFSET] );
        } else {
            res.sendRedirect( req.getContextPath() + "/servlet/StartDoc" );
        }
    }

}
