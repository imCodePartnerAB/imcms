import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.util.* ;

/**
 * The class used to generate login pages, and administrate users page
 * <pre>
  TEMPLATES: The following html files and fragments are used by this servlet.

 	Conf_admin_user.htm : Used to generate a selection list of users
  Conf_admin_user_resp.htm : Used to administrate a user
 	Conf_Login.htm : Html file used to prompt the user for username / password (usermode)
 	Conf_Add_User.htm : Html file used to add a new user (adminmode)
 	Conf_Login_Error.htm : Html file used to generate a login failure. (adminmode)
 </pre>
 * @author  Rickard Larsson
 * @version 1.0
 * Date : 2000-06-16
 */

public class BillBoardLogin extends BillBoard {//ConfLogin

	private final static String USER_UNADMIN_LINK_TEMPLATE = "BillBoard_User_Unadmin_Link.htm";//Conf_User_Unadmin_Link.htm
	private static Vector test;
	
	String LOGIN_HTML = "BillBoard_Login.htm" ;	   // The login page Conf_Login.htm
	String CREATE_HTML = "BillBoard_Add_User.htm" ;   // The create new user page Conf_Add_User.htm
	String LOGIN_ERROR_HTML = "BillBoard_Login_Error.htm" ;  // The error Conf_Login_Error.htm
	// page used for specialized messages to user
	String ADMIN1_HTML = "BillBoard_admin_user.htm" ;//Conf_admin_user.htm
	String ADMIN2_HTML = "BillBoard_admin_user_resp.htm" ;//Conf_admin_user_resp.htm
	String ADD_USER_OK_HTML = "BillBoard_Login_add_ok.htm" ;//Conf_Login_add_ok.htm

	/**
	* <pre>
	Generates html pages used to handle users. Login, add new users, administrate users

	PARAMETERS:
		login_type : Flag used to generate a html page. Default is no value at all.
		If the value is missing, a login page will be generated. Case-insensitive. </LI>

	Expected values
		ADMIN_USER : Generates html page used to administrate users.
	ADD_USER : Generates html page used to add new users

	Example: /ConfLogin?login_type=ADMIN_USER

	TAGS:
		#USERS_MENU# : Inserts an optionlist with all the conference  users
	Example: /ConfLogin?login_type=ADMIN_USER

	* </pre>
	**/
	
	public void init(ServletConfig config)
	throws ServletException {
		super.init(config);
		test = new Vector();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException 
	{
			log("START BillBoardLogin doGet");

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard parameters and validate them
		Properties params = super.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false) return ;

		// Lets get the user object
		imcode.server.User user = super.getUserObj(req,res) ;
		if(user == null) return ;

		int testMetaId = Integer.parseInt( params.getProperty("META_ID") );
		if ( !isUserAuthorized( req, res, testMetaId, user ) ) {
			return;
		}

		String loginType = (req.getParameter("login_type")==null) ? "" : (req.getParameter("login_type")) ;
		//log("Logintype är nu: " + loginType) ;

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String ConfPoolServer = Utility.getDomainPref("billboard_server",host) ;
	
		String userId = user.getString("user_id");
		if(!super.prepareUserForBillBoard(req, res, params, userId) ) {
				log("Error in prepareUserFor Conf" ) ;
		}
		return ;


		// ********** LOGIN PAGE *********
		// Lets build the Responsepage to the loginpage
//		VariableManager vm = new VariableManager() ;

//		vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req)) ;
//		vm.addProperty( "#IMAGE_URL#", this.getExternalImageFolder( req ) );
//		sendHtml(req,res,vm, LOGIN_HTML) ;
//		return ;
	} // End doGet

	/**
	<PRE>
			Parameter	Händelse	parameter värde
	login_type	Utförs om login_type OCH submit har skickats. Verifierar inloggning i konferensen.	LOGIN
	login_type	Adderar en användare in i Janus user db och till konferensens db 	ADD_USER
	login_type	Sparar en användares användarnivå till konferens db 	SAVE_USER
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
		log("START BillBoardLogin doPost");

		// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;

		// Lets get the standard parameters and validate them
		Properties params = super.getSessionParameters(req) ;
		if (super.checkParameters(req, res, params) == false) return ;

		// Lets get the user object
		imcode.server.User user = super.getUserObj(req,res) ;
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
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("billboard_server",host) ;

		// ************* VERIFY LOGIN TO CONFERENCE **************
		// Ok, the user wants to login
		if(loginType.equalsIgnoreCase("login") /* && req.getParameter("submit") != null */) {
			log("Ok, nu försöker vi verifiera logga in!") ;
			String userId = user.getString("user_id");
			
//remove ???			Properties lparams = this.getLoginParams(req ,res) ;

			// Ok, Lets check what the user has sent us. Lets verify the fields the user
			// have had to write freetext in to verify that the sql questions wont go mad.
//remove ???			if (this.verifyLoginParams(req, res, lparams) == false) return ;
//remove ???			lparams = super.verifyForSql(lparams) ;
//remove ???			String userName = lparams.getProperty("LOGIN_NAME") ;
//remove ???			String password = lparams.getProperty("PASSWORD") ;

			// Validate loginparams against Janus DB
//remove ???			RmiConf rmi = new RmiConf(user) ;
//remove ???			String sqlQ = "GetBillBoardUserIdFromName '" + userName + "', '" + password + "'" ;//GetUserIdFromName
//remove ???			String userId = rmi.execJanusSqlProcedureStr(imcServer, sqlQ ) ;
			//log("Användarens id var: " + userId) ;

			// Lets check that we the found the user. Otherwise send unvailid username password
//remove ???			if( userId == null ) {
//remove ???				String header = "BillBoardLogin servlet." ;
//remove ???				BillBoardError err = new BillBoardError(req,res,header,50, LOGIN_ERROR_HTML ) ;
//remove ???				log(header + err.getErrorMsg()) ;
//remove ???				return ;
//remove ???			}

			// Ok, we found the user, lets verify that the user is a member of this conference
			// MemberInConf	@meta_id int,	@user_id int
//remove ???			String checkUserSql = "BillBoardMemberInConf " + params.getProperty("META_ID") + ", "+ userId ;//MemberInConf
//remove ???			log("CheckUserSql: " + checkUserSql) ;
//remove ???			String foundUserInConf = rmi.execSqlProcedureStr(confPoolServer, checkUserSql) ;

			// Ok, The user is not a user in this conference, lets check if he has
			// the right to be a member.
//remove ???			boolean okToLogIn = false ;
//remove ???			if(foundUserInConf == null) {
//remove ???				log("Ok, the user is not a member here, lets find out if he could be") ;
//remove ???				okToLogIn = super.checkDocRights(imcServer, params.getProperty("META_ID"), user) ;
//remove ???				log("Ok, let the user in and let him be a member: " + okToLogIn) ;
//remove ???			}

//remove ???			if( foundUserInConf == null && okToLogIn == false) {
//remove ???				String header = "BillBoardLogin servlet." ;
//remove ???				BillBoardError err = new BillBoardError(req,res,header,50, LOGIN_ERROR_HTML ) ;
//remove ???				log(header + err.getErrorMsg() + "\n the user exists, but is not a member in this conference") ;
//remove ???				return ;
//remove ???			}
//remove ???			else {
//remove ???				// Ok, The user is here for the first time, and he has the rights to go in
				// Lets update his user object
				//log("Lets update the users userObject") ;
//remove ???				String firstName = rmi.execJanusSqlProcedureStr(imcServer,  "GetUserNames " + userId + ", 1" );
//remove ???				String lastName = rmi.execJanusSqlProcedureStr(imcServer,  "GetUserNames " + userId + ", 2" ) ;
//remove ???				if( firstName == null || lastName == null ) {
//remove ???					String header = "BillBoardLogin servlet." ;
//remove ???					BillBoardError err = new BillBoardError(req,res,header,62, LOGIN_ERROR_HTML ) ;
//remove ???					log(header + err.getErrorMsg() + "\n the user exists, but is not a member in this conference") ;
//remove ???					return ;
//remove ???				}
//remove ???				user.setField("user_id", userId) ;
//remove ???				user.setField("first_name", firstName) ;
//remove ???				user.setField("last_name", lastName) ;

				//log("Uppdaterad objectID: " + user.getString("user_id") ) ;
				//log("Uppdaterad first_name: " + user.getString("first_name") ) ;
				//log("Uppdaterad last_name: " + user.getString("last_name") ) ;
//remove ???			}

			//  Lets update the users sessionobject with a a ok login to the conference
			//	Send him to the manager with the ability to get in
			log("Ok, nu förbereder vi användaren på att logga in") ;
			if(!super.prepareUserForBillBoard(req, res, params, userId) ) {
				log("Error in prepareUserFor Conf" ) ;
			}
			return ;
		}

	
		// ***** RETURN TO ADMIN MANAGER *****
		if( loginType.equalsIgnoreCase("GoBack")) {
			String url = MetaInfo.getServletPath(req) ;
			url += "BillBoardLogin?login_type=admin_user" ;
			res.sendRedirect(url) ;
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
		MetaInfo mInfo = new MetaInfo() ;
		if( mInfo.checkParameters(loginP) == false) {
			// if(super.checkParameters(req, res, loginP) == false) {
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

	public void log( String str) {
		super.log(str) ;
		System.out.println("BillBoardLogin: " + str ) ;
	}



	



	/**
	Detects paths and filenames.


	public void init(ServletConfig config) throws ServletException {
	super.init(config);
	ADMIN1_HTML = "Conf_admin_user.htm" ;
	ADMIN2_HTML = "Conf_admin_user_resp.htm" ;
	LOGIN_HTML = "Conf_Login.htm" ;
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



