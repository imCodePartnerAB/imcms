import java.io.* ;
import javax.servlet.* ;
import javax.servlet.http.* ;
import imcode.util.* ;
import imcode.server.* ;
import imcode.server.user.UserDomainObject;

public class Restart extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	public void doGet ( HttpServletRequest req, HttpServletResponse res ) throws ServletException, IOException {
		String host 				= req.getHeader("Host") ;
		IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
		String start_url        	= imcref.getStartUrl() ;

		UserDomainObject user ;
		if ( (user = Check.userLoggedOn( req, res, start_url ))==null ) {
			return;
		}
		// Is user superadmin?

		String sqlStr  = "select role_id from users,user_roles_crossref\n" ;
		sqlStr += "where users.user_id = user_roles_crossref.user_id\n" ;
		sqlStr += "and user_roles_crossref.role_id = 0\n" ;
		sqlStr += "and users.user_id = " + user.getUserId() ;

		if ( imcref.sqlQuery(sqlStr).length == 0 ) {
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
