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
		doGet()
	*/

	public void doGet( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		doPost(req, res);
		return;
	} 
	/** end of doGet() */
	

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

		String name = req.getParameter( "name" );
		String passwd = req.getParameter( "passwd" );
		String value = req.getHeader( "User-Agent" ) ; 
		
		
		// Check the name and password for validity
		user = imcref.verifyUser( name, passwd );
		
		// Get session 
		HttpSession session = req.getSession( true );

		if( user == null ) {
		    if ( req.getParameter("next_meta") != null ){
				session.setAttribute( "next_meta", req.getParameter("next_meta") );
			}
			res.sendRedirect(access_denied_url) ;
		    return ;
		} else {
				
		    // Valid login.  Make a note in the session object.
		    session.setAttribute( "logon.isDone", user );  // just a marker object
		    session.setAttribute("browser_id",value) ;
			
			StartDoc.incrementSessionCounter(imcref,user,req) ;

		   	user.setLoginType("verify") ;
			
			
			String nexturl = "StartDoc";  // default value
			
			if (req.getParameter("Logga in")!=null){
				String target="";
				// if we have got a next_meta lets redirect to that meta_id
				if ( session.getAttribute( "next_meta" ) !=null ){
					target = "GetDoc?meta_id=" +(String)session.getAttribute( "next_meta" );
					session.removeAttribute("next_meta") ;
					res.sendRedirect(target);
					return;
				}
				else {
					// Try redirecting the client to the page he first tried to access
					target = (String) session.getAttribute("login.target");
		    		if (target != null) {
						session.removeAttribute("login.target") ;
						res.sendRedirect(target);
						return ;
		    		}
		    	}
				
			}else if (req.getParameter("Ändra")!=null){
				// if user has been pushed button "change" from login page 
				session.setAttribute("userToChange", ""+user.getUserId() );
				session.setAttribute("next_url", "StartDoc");
				
				res.sendRedirect("AdminUserProps?CHANGE_USER=true");
				return;
					
			
			}else if ( req.getParameter("next_url") !=null ){
				//if user was redirected here from an user template and next_meta was passed
				nexturl = req.getParameter("next_url") ;
				res.sendRedirect(nexturl);
				return;
				
			}
			else if ( req.getParameter("next_meta") !=null ){
				//if user was redirected here from an user template and next_url was passed
				nexturl = "GetDoc?meta_id=" + req.getParameter("next_meta") ;
				res.sendRedirect(nexturl);
				return;
			}	
				
						

			// Couldn't redirect to the target.  Redirect to the site's home page.
		    res.sendRedirect( scheme + "://" + serverName + port + servlet_url + "StartDoc" );

		} 
		
    } /** end of doPost()  */                
}

