package com.imcode.imcms.servlet;

import java.io.* ;
import java.util.* ;
import javax.servlet.* ;
import javax.servlet.http.* ;

import imcode.util.* ;
import imcode.server.* ;
import imcode.server.user.UserDomainObject;

public class LogOut extends HttpServlet {
	public void doGet ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {

        UserDomainObject user = Utility.getLoggedOnUser( req );
        HttpSession session = req.getSession (true) ;
		session.invalidate() ;

        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;

        res.setContentType("text/html") ;
        ServletOutputStream out = res.getOutputStream () ;
        Vector vec = new Vector() ;
        vec.add("#start#") ;
        String start_url = imcref.getStartUrl() ;
        vec.add(start_url) ;
        vec.add("#login#") ;
        String login_url = imcref.getImcmsUrl() + user.getLanguageIso639_2() + "/login/";
        vec.add(login_url) ;
        String htmlStr = imcref.getAdminTemplate( "logged_out.html", user, vec ) ;
		out.print(htmlStr) ;
	}
}
