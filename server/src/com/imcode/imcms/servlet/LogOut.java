package com.imcode.imcms.servlet;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogOut extends HttpServlet {

    public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser( req );
        HttpSession session = req.getSession( true );
        session.invalidate();

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface();

        Utility.setDefaultHtmlContentType( res );
        res.getOutputStream().print( imcref.getAdminTemplate( "logged_out.html", user, null ) );
    }
}
