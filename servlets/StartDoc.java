import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.rmi.* ;

import imcode.util.* ;
import imcode.server.* ;

/**
  Start servlet in the system.
*/
public class StartDoc extends HttpServlet {
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
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("userserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;
		String servlet_url       	= Utility.getDomainPref( "servlet_url",host ) ;

		long time = System.currentTimeMillis() ;
		imcode.server.User user ;
		String htmlStr = "" ;
		int meta_id ;
		String test = "" ;
		String type = "";
		String version = "" ;
		String plattform = "" ;
		boolean incCounter = false ;

		res.setContentType( "text/html" );
		ServletOutputStream out = res.getOutputStream( );
		// Get the session
		HttpSession session = req.getSession( true );

		// Does the session indicate this user already logged in?
		Object done = session.getValue( "logon.isDone" );  // marker object
		user = (imcode.server.User)done ;
//		String domain = req.getParameter("domain") ;

		if( done == null ) {

			incCounter = true ;
			// Check the name and password for validity
			String ip = req.getRemoteAddr( ) ;
			user = ipAssignUser( ip, host ) ;

			// Valid login.  Make a note in the session object.
//			session = req.getSession( true );
			session.putValue( "logon.isDone", user );  // just a marker object

			// get type of browser
			String value = req.getHeader( "User-Agent" ) ;

			if ( value == null ) {
				value = "" ;
			}
			session.putValue("browser_id",value) ;

			if( user == null ) {
				// No logon.isDone means he hasn't logged in.
				// Save the request URL as the true target and redirect to the login page.

				session.putValue( "login.target", HttpUtils.getRequestURL( req ).toString( ) );
				String scheme = req.getScheme( );
				String serverName = req.getServerName( );
				int p = req.getServerPort( );
				String port = (p == 80 || p == 443) ? "" : ":" + p;
				res.sendRedirect( scheme + "://" + serverName + port + start_url ) ;
				return ;
			}
//			user.setBrowserInfo( type,version,plattform ) ;
		}

		if( incCounter ) {
			IMCServiceRMI.incCounter(imcserver) ;
			IMCServiceRMI.sqlUpdateProcedure( imcserver, "IncSessionCounter" ) ;
		}


		meta_id = IMCServiceRMI.getDefaultHomePage(imcserver) ;

		String scheme = req.getScheme( );
		String serverName = req.getServerName( );
		int p = req.getServerPort( );
		String port = (p == 80 || p == 443) ? "" : ":" + p;
		Utility.redirect(req,res,"GetDoc?meta_id="+meta_id) ;

		return ;
	}

	/**
	Check if user exist in database
	*/
	static protected imcode.server.User allowUser( String user_name, String passwd, String host ) throws IOException {
		String imcserver 			= Utility.getDomainPref("userserver",host) ;

		// user information
		String fieldNames[] = {"user_id","login_name","login_password","first_name",
						  "last_name","title","user","address","city","zip","country",
						  "county_council","email","admin_mode","last_page","archive_mode" ,"lang_id" ,"user_type","active", "create_date"} ;
		return IMCServiceRMI.verifyUser( imcserver, new imcode.server.LoginUser( user_name,passwd ),fieldNames ) ;
	}

	/**
	Ip login  - check if user exist in ip-table
	*/
	static protected imcode.server.User ipAssignUser( String remote_ip , String host) throws IOException {
		String imcserver 			= Utility.getDomainPref("userserver",host) ;
		imcode.server.User user = new imcode.server.User( ) ;

		long ip = Utility.ipStringToLong(remote_ip) ;

		String sqlStr = "" ;
		sqlStr  = "select distinct login_name,login_password,ip_access_id from users,user_roles_crossref,ip_accesses\n" ;
		sqlStr += "where user_roles_crossref.user_id = ip_accesses.user_id\n" ;
		sqlStr += "and users.user_id = user_roles_crossref.user_id\n" ;
		sqlStr += "and ip_accesses.ip_start <= " + ip + "\n" ;
		sqlStr += "and ip_accesses.ip_end >= " + ip + "\n" ;
		sqlStr += "order by ip_access_id desc" ;

		String user_data[] = IMCServiceRMI.sqlQuery( imcserver,sqlStr );

		if( user_data.length > 0 )  {
			user = allowUser( user_data[0],user_data[1],host ) ;
			user.setLoginType("ip_access") ;
		}
		else {
			user = allowUser( "User","user", host ) ;
			user.setLoginType("extern") ;
		}

		return user ;
	}
}



