package com.imcode.imcms.servlet;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.IOException;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.WebAppGlobalConstants;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

public class LogOut extends HttpServlet {

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser( req );
        HttpSession session = req.getSession( true );
        session.removeAttribute( WebAppGlobalConstants.LOGGED_IN_USER );

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        Utility.setDefaultHtmlContentType( res );
        res.getOutputStream().print( imcref.getAdminTemplate( "logged_out.html", user, null ) );
    }
}
