package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Restart extends HttpServlet {

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser( req );

        if ( !user.isSuperAdmin() ) {
            res.sendError( HttpServletResponse.SC_FORBIDDEN );
            return;
        }

        Imcms.restartCms() ;
        res.getOutputStream().println( "Restart complete." );
    }
}
