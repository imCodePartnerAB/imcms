import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.* ;

import imcode.server.* ;
import imcode.util.* ;
/**
   Verify a user.
*/
public class VerifyUser extends HttpServlet {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    /**
       init()
    */
    public void init( ServletConfig config ) throws ServletException {
	super.init( config ) ;
    }

    /**
       doPost()
    */
    public void doPost( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
	String host 				= req.getHeader("Host") ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterfaceByHost(host) ;
	String start_url        	= imcref.getStartUrl() ;
	String servlet_url       	= Utility.getDomainPref( "servlet_url",host ) ;
	String admin_url       		= Utility.getDomainPref( "admin_url",host ) ;
	String access_denied_url   	= Utility.getDomainPref( "access_denied_url",host ) ;

	imcode.server.User user ;
	res.setContentType( "text/html" );
	PrintWriter out = res.getWriter( );
	String test = "" ; 
	String type = "";
	String version = "" ;
	String plattform = "" ;

	String scheme = req.getScheme( );
	String serverName = req.getServerName( );
	int p = req.getServerPort( );
	String port = (p == 80 || p == 443) ? "" : ":" + p;

	// Get the user's name and password
	String name = req.getParameter( "name" );
	String passwd = req.getParameter( "passwd" );
	String value = req.getHeader( "User-Agent" ) ; 

	// Check the name and password for validity
	user = allowUser( name, passwd, host ) ; 

	// add browser info to user

	if( user == null ) {
	    res.sendRedirect(access_denied_url) ;              
	    return ;
	} else {

	    // Valid login.  Make a note in the session object.
	    HttpSession session = req.getSession( true );
	    session.setAttribute( "logon.isDone", user );  // just a marker object
	    session.setAttribute("browser_id",value) ;

	    // Try redirecting the client to the page he first tried to access
	    String target = (String) session.getAttribute("login.target");
	    if (target != null) {
		session.removeAttribute("login.target") ;
		res.sendRedirect(target);
		return ;
	    }
	    //return;

	    StartDoc.incrementSessionCounter(imcref,user,req) ;

	    user.setLoginType("verify") ;

	    // Couldn't redirect to the target.  Redirect to the site's home page.
	    res.sendRedirect( scheme + "://" + serverName + port + servlet_url + "StartDoc" );

	}
    }

    /**
       Test if user exist in the database
    */
    protected imcode.server.User allowUser( String user_name, String passwd, String host ) throws IOException { 
	imcode.server.User user = new imcode.server.User( ) ;
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterfaceByHost(host) ;

	// user information
	// FIXME: Replace this SQL-abomination.
	String fieldNames[] = {"user_id","login_name","login_password","first_name",
			       "last_name","title", "company", "address","city","zip","country",
			       "county_council","email","admin_mode","last_page","archive_mode","lang_id", "user_type", "active", "create_date" } ;
	user = imcref.verifyUser( new imcode.server.LoginUser( user_name,passwd ),fieldNames );
	return user ; //  user ;
    }
}
