package com.imcode.imcms.servlet;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
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
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        UserDomainObject user = Utility.getLoggedOnUser( req );

        // Is user superadmin?
        if ( !user.isSuperAdmin() ) {
            String start_url = imcref.getStartUrl();
            Utility.redirect( req, res, start_url );
            return;
        }

        log( "Restarting..." );
        Prefs.flush();
        log( "Flushed preferencescache" );
        log( "Restart Complete." );
        res.getOutputStream().println( "Restart complete." );
    }
}
