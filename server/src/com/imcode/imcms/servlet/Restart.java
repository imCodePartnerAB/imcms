package com.imcode.imcms.servlet;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Prefs;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Restart extends HttpServlet {

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser( req );

        // Is user superadmin?
        if ( !user.isSuperAdmin() ) {
            Utility.redirectToStartDocument( req, res) ;
            return;
        }

        log( "Restarting..." );
        Prefs.flush();
        log( "Flushed preferencescache" );
        log( "Restart Complete." );
        res.getOutputStream().println( "Restart complete." );
    }
}
