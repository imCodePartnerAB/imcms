import java.io.* ;
import javax.servlet.* ;
import javax.servlet.http.* ;
import imcode.util.* ;
import imcode.server.* ;

public class Restart extends HttpServlet {
	
	public void doGet ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		String imcserver 			= Utility.getDomainPref("adminserver",host) ;
		String start_url        	= Utility.getDomainPref( "start_url",host ) ;

		User user ;
		if ( (user = Check.userLoggedOn( req, res, start_url ))==null ) {
			return;
		}
		// Is user superadmin?

		String sqlStr  = "select role_id from users,user_roles_crossref\n" ;
		sqlStr += "where users.user_id = user_roles_crossref.user_id\n" ;
		sqlStr += "and user_roles_crossref.role_id = 0\n" ;
		sqlStr += "and users.user_id = " + user.getInt("user_id") ;
		
		if ( IMCServiceRMI.sqlQuery(imcserver,sqlStr).length == 0 ) {
			Utility.redirect(req,res,start_url) ;
			return ;
		}

		log ("Restarting...") ;
		Prefs.flush() ;
		log ("Flushed preferencescache") ;
		IMCServiceRMI.flush() ;
		log ("Flushed RMI-interfacecache") ;
		log ("Restart Complete.") ;
		res.getOutputStream().println("Restart complete.") ;
	}
}
