import imcode.server.* ;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.util.* ;

/**
 * The class used to generate login pages, and administrate users page
 * 
 * TEMPLATES: The following html files and fragments are used by this servlet.
 *	BillBoard_Login_Error.htm 
 *	
 * @version 1.2 20 Aug 2001
 * @author Rickard Larsson REBUILD TO BillBoardLogin BY Peter Östergren
 *
 */

public class BillBoardLogin extends BillBoard {//ConfLogin
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	private final static String USER_UNADMIN_LINK_TEMPLATE = "BillBoard_User_Unadmin_Link.htm";//Conf_User_Unadmin_Link.htm
    //private static Vector test;
	

	//String CREATE_HTML = "BillBoard_Add_User.htm" ;   // The create new user page Conf_Add_User.htm
	String LOGIN_ERROR_HTML = "BillBoard_Login_Error.htm" ;  // The error Conf_Login_Error.htm
	// page used for specialized messages to user
	//String ADMIN1_HTML = "BillBoard_admin_user.htm" ;//Conf_admin_user.htm
	//String ADMIN2_HTML = "BillBoard_admin_user_resp.htm" ;//Conf_admin_user_resp.htm
	//String ADD_USER_OK_HTML = "BillBoard_Login_add_ok.htm" ;//Conf_Login_add_ok.htm


	
	public void init(ServletConfig config)
	throws ServletException {
		super.init(config);
	//	test = new Vector();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
			//log("START BillBoardLogin doGet");

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard parameters and validate them
		Properties params = super.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false) return ;

		// Lets get the user object
		imcode.server.user.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		int testMetaId = Integer.parseInt( params.getProperty("META_ID") );
		if ( !isUserAuthorized( req, res, testMetaId, user ) ) {
			return;
		}

		String loginType = (req.getParameter("login_type")==null) ? "" : (req.getParameter("login_type")) ;
		//log("Logintype är nu: " + loginType) ;

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
		IMCPoolInterface billref = IMCServiceRMI.getBillboardIMCPoolInterface(req) ;

		String userId = ""+user.getUserId();
		if(!super.prepareUserForBillBoard(req, res, params, userId) ) {
				log("Error in prepareUserFor Conf" ) ;
		}
		return ;
	} // End doGet

	/**
	<PRE>
			Parameter	Händelse	parameter värde
	login_type	Utförs om login_type OCH submit har skickats. Verifierar inloggning i konferensen.	LOGIN
	login_type	Adderar en användare in i Janus user db och till konferensens db	ADD_USER
	login_type	Sparar en användares användarnivå till konferens db	SAVE_USER
	Reacts on the actions sent.

	PARAMETERS:
		login_type : Flag used to detect selected acion. Case insensitive

	Expected values
		LOGIN : Verifies a user login to the conference
	ADD_USER : Adds a new user in the db
	SAVE_USER	: Saves a users level to the db

	</PRE>
	**/

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
		//log("START BillBoardLogin doPost");

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard parameters and validate them
		Properties params = super.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false) return ;

		// Lets get the user object
		imcode.server.user.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		int testMetaId = Integer.parseInt( params.getProperty("META_ID") );
		if ( !isUserAuthorized( req, res, testMetaId, user ) ) {
			return;
		}

		// Lets get the loginType
		String loginType = (req.getParameter("login_type")==null) ? "" : (req.getParameter("login_type")) ;
		String tmp = req.getParameter("SAVE_USER") ;
		//log("post logintype: " + loginType ) ;
		//log("tmp: " + tmp) ;

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
		IMCPoolInterface billref = IMCServiceRMI.getBillboardIMCPoolInterface(req) ;

		// ************* VERIFY LOGIN TO CONFERENCE **************
		// Ok, the user wants to login
		if(loginType.equalsIgnoreCase("login") /* && req.getParameter("submit") != null */) {
			//log("Ok, nu försöker vi verifiera logga in!") ;
			String userId = ""+user.getUserId();

			//  Lets update the users sessionobject with a a ok login to the conference
			//	Send him to the manager with the ability to get in
			//log("Ok, nu förbereder vi användaren på att logga in") ;
			if(!super.prepareUserForBillBoard(req, res, params, userId) ) {
				log("Error in prepareUserFor Conf" ) ;
			}
			return ;
		}

		// ***** RETURN TO ADMIN MANAGER *****
		if( loginType.equalsIgnoreCase("GoBack")) {
			res.sendRedirect("BillBoardLogin?login_type=admin_user") ;
			return ;
		}
	} // end HTTP POST


	/**
	The getLoginParams method gets the login params from the requstobject
	**/

	private Properties getLoginParams(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		Properties login = new Properties() ;
		// Lets get the parameters we know we are supposed to get from the request object
		String login_name = (req.getParameter("login_name")==null) ? "" : (req.getParameter("login_name")) ;
		String password1 = (req.getParameter("password")==null) ? "" : (req.getParameter("password")) ;
		login.setProperty("LOGIN_NAME", login_name.trim()) ;
		login.setProperty("PASSWORD", password1.trim()) ;
		return login ;
	}

	/**
	The verifyLogin method verifies the login params from the requestobject
	**/

	private boolean verifyLoginParams(HttpServletRequest req, HttpServletResponse res,
		Properties loginP) throws ServletException, IOException {

		// Ok, lets check the parameters
		if( MetaInfo.checkParameters(loginP) == false) {
			String header = "BillBoardLogin servlet. " ;
			BillBoardError err = new BillBoardError(req,res,header,50) ;
			log(header + err.getErrorMsg()) ;
			return false ;
		}
		return true ;

	} // verifyLoginParams


	/**
	Log function, will work for both servletexec and Apache
	**/

	public void log( String msg) {
		super.log("BillBoardLogin: " + msg) ;
		
	}



	



	/**
	Detects paths and filenames.


	public void init(ServletConfig config) throws ServletException {
	super.init(config);
	ADMIN1_HTML = "Conf_admin_user.htm" ;
	ADMIN2_HTML = "Conf_admin_user_resp.htm" ;
	CREATE_HTML = "Conf_Add_User.htm" ;
	LOGIN_ERROR_HTML = "Conf_Login_Error.htm" ;
	ADD_USER_OK_HTML = "Conf_Login_add_ok.htm" ;
	}
	*/
	/**
	Returns a String, containing the userID in the request object.If something failes,
	a error page will be generated and null will be returned.
	*/

	private String getCurrentUserId(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		// Lets get the userId from the request Object.
		String userId = req.getParameter("user_id") ;
		if (userId == null ) {
			String header = "BillBoardLogin servlet." ;
			BillBoardError err = new BillBoardError(req,res,header,59, LOGIN_ERROR_HTML ) ;
			this.log(err.getErrorString()) ;
			return null;
		}
		else
			return userId ;

	} // End getCurrentUserId

} // End class
