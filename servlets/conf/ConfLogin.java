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

public class ConfLogin extends Conference {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	private final static String USER_UNADMIN_LINK_TEMPLATE = "Conf_User_Unadmin_Link.htm";

	String LOGIN_HTML = "Conf_Login.htm" ;	   // The login page
	String CREATE_HTML = "Conf_Add_user.htm" ;   // The create new user page
	String LOGIN_ERROR_HTML = "Conf_Login_Error.htm" ;  // The error
	// page used for specialized messages to user
	String ADMIN1_HTML = "Conf_Admin_user.htm" ;
	String ADMIN2_HTML = "Conf_admin_user_resp.htm" ;
	String ADD_USER_OK_HTML = "Conf_Login_add_ok.htm" ;

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

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

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
		String ConfPoolServer = Utility.getDomainPref("conference_server",host) ;


		// ******** ADD USER PAGE *********
		// Lets generate the adduser page
		if(loginType.equalsIgnoreCase("ADD_USER")) {
			RmiConf rmi = new RmiConf(user) ;
			String htmlPage = rmi.getLoginPage(host) ;

			// Lets build the Responsepage to the loginpage
			VariableManager vm = new VariableManager() ;
			Vector userInfoV = new Vector(20) ; // a vector, bigger than the amount fields
			vm = this.addUserInfo(vm,userInfoV) ;
			vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req)) ;
			sendHtml(req,res,vm, CREATE_HTML) ;
			return ;
		}

		// ******** ADMINISTRATE USERS PAGE *********
		// Lets generate the adduser page
		if(loginType.equalsIgnoreCase("ADMIN_USER")) {
			// Lets check that the user is an administrator
			if( super.getAdminRights(imcServer, params.getProperty("META_ID"), user) == false ) {
				String header = "ConfAdmin servlet. " ;
				String msg = params.toString() ;
				ConfError err = new ConfError(req,res,header,6) ;
				return ;
			}

			// Lets get all users in this conference from db
			RmiConf rmi = new RmiConf(user) ;
			String[] usersArr = rmi.execSqlQuery(ConfPoolServer, "A_GetAllConfUsersInList " + params.get("META_ID")) ;
			Vector usersV  = super.convert2Vector(usersArr) ;
			VariableManager vm = new VariableManager() ;
			Html ht = new Html() ;
			String usersOption = ht.createHtmlCode("ID_OPTION", "", usersV ) ;
			vm.addProperty("USERS_MENU", usersOption  ) ;
			vm.addProperty("UNADMIN_LINK_HTML", this.USER_UNADMIN_LINK_TEMPLATE );
			this.sendHtml(req,res,vm, ADMIN1_HTML) ;
			return ;
		}

		// ********** LOGIN PAGE *********
		// Lets build the Responsepage to the loginpage
		VariableManager vm = new VariableManager() ;

		vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req)) ;
		vm.addProperty( "#IMAGE_URL#", this.getExternalImageFolder( req ) );
		sendHtml(req,res,vm, LOGIN_HTML) ;
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
	throws ServletException, IOException {

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
		String confPoolServer = Utility.getDomainPref("conference_server",host) ;

		// ************* VERIFY LOGIN TO CONFERENCE **************
		// Ok, the user wants to login
		if(loginType.equalsIgnoreCase("login") /* && req.getParameter("submit") != null */) {
			//	log("Ok, nu försöker vi verifiera logga in!") ;
			Properties lparams = this.getLoginParams(req ,res) ;

			// Ok, Lets check what the user has sent us. Lets verify the fields the user
			// have had to write freetext in to verify that the sql questions wont go mad.
			if (this.verifyLoginParams(req, res, lparams) == false) return ;
			lparams = super.verifyForSql(lparams) ;
			String userName = lparams.getProperty("LOGIN_NAME") ;
			String password = lparams.getProperty("PASSWORD") ;

			// Validate loginparams against Janus DB
			RmiConf rmi = new RmiConf(user) ;
			String sqlQ = "GetUserIdFromName '" + userName + "', '" + password + "'" ;
			String userId = rmi.execJanusSqlProcedureStr(imcServer, sqlQ ) ;
			//log("Användarens id var: " + userId) ;

			// Lets check that we the found the user. Otherwise send unvailid username password
			if( userId == null ) {
				String header = "ConfLogin servlet." ;
				ConfError err = new ConfError(req,res,header,50, LOGIN_ERROR_HTML ) ;
				log(header + err.getErrorMsg()) ;
				return ;
			}

			// Ok, we found the user, lets verify that the user is a member of this conference
			// MemberInConf	@meta_id int,	@user_id int
			String checkUserSql = "A_MemberInConf " + params.getProperty("META_ID") + ", "+ userId ;
			log("CheckUserSql: " + checkUserSql) ;
			String foundUserInConf = rmi.execSqlProcedureStr(confPoolServer, checkUserSql) ;

			// Ok, The user is not a user in this conference, lets check if he has
			// the right to be a member.
			boolean okToLogIn = false ;
			if(foundUserInConf == null) {
				log("Ok, the user is not a member here, lets find out if he could be") ;
				okToLogIn = super.checkDocRights(imcServer, params.getProperty("META_ID"), user) ;
				log("Ok, let the user in and let him be a member: " + okToLogIn) ;
			}

			if( foundUserInConf == null && okToLogIn == false) {
				String header = "ConfLogin servlet." ;
				ConfError err = new ConfError(req,res,header,50, LOGIN_ERROR_HTML ) ;
				log(header + err.getErrorMsg() + "\n the user exists, but is not a member in this conference") ;
				return ;
			}
			else {
				// Ok, The user is here for the first time, and he has the rights to go in
				// Lets update his user object
				//log("Lets update the users userObject") ;
				String firstName = rmi.execJanusSqlProcedureStr(imcServer,  "GetUserNames " + userId + ", 1" );
				String lastName = rmi.execJanusSqlProcedureStr(imcServer,  "GetUserNames " + userId + ", 2" ) ;
				if( firstName == null || lastName == null ) {
					String header = "ConfLogin servlet." ;
					ConfError err = new ConfError(req,res,header,62, LOGIN_ERROR_HTML ) ;
					log(header + err.getErrorMsg() + "\n the user exists, but is not a member in this conference") ;
					return ;
				}
				user.setField("user_id", userId) ;
				user.setField("first_name", firstName) ;
				user.setField("last_name", lastName) ;

				//log("Uppdaterad objectID: " + user.getString("user_id") ) ;
				//log("Uppdaterad first_name: " + user.getString("first_name") ) ;
				//log("Uppdaterad last_name: " + user.getString("last_name") ) ;
			}

			//  Lets update the users sessionobject with a a ok login to the conference
			//	Send him to the manager with the ability to get in
			log("Ok, nu förbereder vi användaren på att logga in") ;
			if(!super.prepareUserForConf(req, res, params, userId) ) {
				log("Error in prepareUserFor Conf" ) ;
			}
			return ;
		}

		// ************* ADD USER TO CONFERENCE **************
		if(loginType.equalsIgnoreCase("ADD_USER") ) {
			// log("Now runs add_user") ;

			// Lets get the parameters from html page and validate them
			Properties userParams = this.getNewUserParameters(req) ;
			// Properties userParams = this.getNewUserParameters2(req) ;
			if( this.checkUserParameters(userParams) == false) {
				String header = "ConfLogin servlet." ;
				ConfError err = new ConfError(req,res,header,51, LOGIN_ERROR_HTML ) ;
				log(header + err.getErrorMsg()) ;
				return ;
			}

			// Lets validate the passwords. Error message will be generated in method
			if( UserHandler.verifyPassword(userParams,req,res) == false) return ;

			// Lets validate the phonenbr. Error message will be generated in method
			if( UserHandler.verifyPhoneNumber(userParams,req,res) == false) return ;

			// Lets validate the userparameters before the sql
			userParams = super.verifyForSql(userParams) ;
			String userName = userParams.getProperty("login_name") ;
			RmiConf rmi = new RmiConf(user) ;

			// Lets check that the new username doesnt exists already in db
			String userNameExists[] = rmi.execJanusSqlProcedure(imcServer, "FindUserName '" + userName + "'") ;
			//log("SQL fråga:" + "FindUserName '" + userName + "'") ;
			if(userNameExists != null ) {
				if(userNameExists.length > 0 ) {
					String header = "ConfLogin servlet." ;
					ConfError err = new ConfError(req,res,header,56, LOGIN_ERROR_HTML ) ;
					return ;
				}
			}

			// Lets get the new UserId for the new user
			String newUserId = rmi.execJanusSqlProcedureStr(imcServer, "GetHighestUserId" ) ;
			if(newUserId == null) {
				String header = "ConfLogin servlet." ;
				ConfError err = new ConfError(req,res,header,61, LOGIN_ERROR_HTML ) ;
				return ;
			}

			// Lets build the users information into a string and add it to db
			//String userStr = createUserInfoString(userParams, newUserId) ;

			// Lets get the language id the user will have, or set the lang_id to 1
			// as default
			if( userParams.getProperty("lang_id") == null )
			  userParams.setProperty("lang_id", "1") ;

			userParams.setProperty("user_id", newUserId) ;
			//log("Userparams: " + userParams.toString());
			String userStr =  UserHandler.createUserInfoString(userParams) ;
			//log("UserDBStr: " + userStr) ;

			// log("AddNewUser " + userStr) ;
			//rmi.execJanusSqlUpdateProcedure(imcServer,  userStr) ;
			UserHandler.addUserInfoDB(imcServer, userStr) ;

			// Lets add a new phone number
			StringBuffer phoneStr = new StringBuffer() ;
			phoneStr.append("phoneNbrAdd " + newUserId) ;
			phoneStr.append(", '" + userParams.getProperty("country_code") ) ;
			phoneStr.append("', '" + userParams.getProperty("area_code") ) ;
			phoneStr.append("', '" + userParams.getProperty("local_code") + "'" ) ;
			log("PhoneNbrAddSQL: " + phoneStr.toString()) ;
			rmi.execJanusSqlUpdateProcedure(imcServer, phoneStr.toString()) ;

			// Ok, lets get the roles the user will get when he is selfregistering  and
			// add those roles to the user
			String sqlSproc = "A_SelfRegRoles_GetAll2 " + params.getProperty("META_ID") ;
			String sqlAnswer[] = rmi.execSqlProcedure(confPoolServer, sqlSproc ) ;

			// First, get the langprefix
			String lang_id = user.getString("lang_id") ;
			String langsql = "GetLangPrefixFromId " + lang_id ;
			//log("LangPrefixSql: " + langsql) ;
			String langPrefix = rmi.execJanusSqlProcedureStr(imcServer, langsql ) ;
			//log( "LangPrefix: " + langPrefix) ;

			if(sqlAnswer != null ) {
				for(int i = 0; i<sqlAnswer.length; i+=2) {
					String aRoleId = sqlAnswer[i].toString() ;
					// Lets check that the role id is still valid to use against
					// the host system

					String sqlCheckSproc = "RoleCheckConferenceAllowed '" + langPrefix +"', "+ aRoleId ;
					// log("RoleCheckSQL: " + sqlCheckSproc) ;
					String found = rmi.execJanusSqlProcedureStr(imcServer, sqlCheckSproc) ;
					//log("FoundRoleCheckSQL: " + found) ;
					if (found != null ) {
						String addRoleSql = "AddUserRole " + newUserId + ", " + aRoleId ;
						//log("AddRoleSQL: " + addRoleSql) ;
						rmi.execJanusSqlUpdateProcedure(imcServer, addRoleSql) ;
					}
				}
			}

			// Ok, Lets add the users roles into db, first get the role his in the system with
			Properties currUserParams = super.getUserParameters(user) ;
			String userId = currUserParams.getProperty("USER_ID") ;

			String usersRoles[] = rmi.execJanusSqlProcedure(imcServer, "GetUserRolesIDs " + userId ) ;
			// log("SQL fråga:" + "GetUserRolesIDs " + userId) ;
			if(usersRoles != null ) {
				for(int i = 0; i<usersRoles.length; i+=2) {
					// Late change, fix so the superadminrole wont be copied to the new user
					if(!usersRoles[i].toString().equals("1")) {
						String addRoleSql = "AddUserRole " + newUserId + ", " + usersRoles[i].toString() ;
						rmi.execJanusSqlUpdateProcedure(imcServer, addRoleSql) ;
					}
					else {
						// log("Superadmin rollen kom inte med!") ;
					}
				}
			}
			else {  // nothing came back from getUserRoles
				String header = "ConfLogin servlet." ;
				ConfError err = new ConfError(req,res,header,58 ) ;
				log(header + err.getErrorMsg()) ;
				return ;
			}

			// Lets add the new user in the conference db as well
			// ConfUsersAdd	@user_id int,	@conf_id int,	@aFirstName char(25),	@aLastName char(30)
			String metaId = params.getProperty("META_ID") ;
			String fName = userParams.getProperty("first_name") ;
			String lName = userParams.getProperty("last_name") ;
			String confUsersAddSql = "A_ConfUsersAdd "+ newUserId +", "+ metaId +", '"+ fName + "', '";
			confUsersAddSql += lName + "'";
			// log("ConfUsersAddSql: " + confUsersAddSql) ;
			rmi.execSqlUpdateProcedure(confPoolServer, confUsersAddSql) ;

			//	log("OK, nu slog vi in en ny användare") ;
			String header = "ConfLogin servlet." ;
			ConfError err = new ConfError(req,res,header,55, ADD_USER_OK_HTML ) ;
			log(header + err.getErrorMsg()) ;
			return ;
		}

		// ******* CHANGE_USER **********
		// if(loginType.equalsIgnoreCase("CHANGE_USER") ) {
		if( req.getParameter("CHANGE_USER") != null ) {
			// log("Ok, Lets change a conference user") ;
			// Lets check that the user is an administrator
			if( super.getAdminRights(imcServer, params.getProperty("META_ID"), user) == false ) {
				String header = "ConfAdmin servlet. " ;
				String msg = params.toString() ;
				ConfError err = new ConfError(req,res,header,6) ;
				return ;
			}

			// Lets get the user which should be changed
			String userId = this.getCurrentUserId(req,res) ;
			if (userId == null)	return ;
			RmiConf rmi = new RmiConf(user) ;

			// Lets get all Userinformation from Janus db
			String userInfo[] = rmi.execJanusSqlProcedure(imcServer, "GetUserInfo " + userId) ;

			// Lets get the selected users userlevel
			String level = rmi.execSqlProcedureStr(confPoolServer, "A_ConfUsersGetUserLevel " + params.getProperty("META_ID") +", " + userId) ;
			String levelStatus = "" ;
			if(level.equalsIgnoreCase("1") ) levelStatus = "checked" ;
			level = "EXPERT" ;

				Vector userV = this.convert2Vector(userInfo) ;
			VariableManager vm = new VariableManager() ;
			Html htm = new Html() ;
			vm = this.addUserInfo(vm,userV) ;

			// Lets create the HTML page
			vm.addProperty("USER_LEVEL", level) ;
			vm.addProperty("USER_LEVEL_STATUS", levelStatus) ;
			vm.addProperty("CURR_USER_ID", userId) ;
			this.sendHtml(req, res, vm, ADMIN2_HTML) ;
			return ;
		}

		//******* SAVE AN EXISTING USERS SETTINGS ***********
		if( req.getParameter("SAVE_USER") != null) {
			// log("Ok , lets save an existing user") ;
			// Lets check that the user is an administrator
			if( super.getAdminRights(imcServer, params.getProperty("META_ID"), user) == false ) {
				String header = "ConfAdmin servlet. " ;
				String msg = params.toString() ;
				ConfError err = new ConfError(req,res,header,6) ;
				return ;
			}

			// Lets get the parameters from html page and validate them

			String userLevel = req.getParameter("user_level") ;
			if(userLevel == null) userLevel = "NORMAL" ;
			if(userLevel.equalsIgnoreCase("NORMAL"))
				userLevel = "0" ;
			else
				userLevel = "1" ;

			//	log("UserLevel: " + userLevel) ;

			// Lets get the userId were changing properties on from the request Object.
			String userId = this.getCurrentUserId(req,res) ;
			if (userId == null)	return ;

			// Lets add the new information into the conf user db
			RmiConf rmi = new RmiConf(user) ;
			String sqlQ = "A_ConfUsersSetUserLevel " + params.getProperty("META_ID") + ", " ;
			sqlQ += userId + ", " + userLevel ;
			//	log("SqlQ: " + sqlQ) ;
			rmi.execSqlUpdateProcedure(confPoolServer, sqlQ) ;

			String url = "ConfLogin?login_type=admin_user" ;
			res.sendRedirect(url) ;
			return ;
		}

		// ***** RETURN TO ADMIN MANAGER *****
		if( loginType.equalsIgnoreCase("GoBack")) {
			String url = "ConfLogin?login_type=admin_user" ;
			res.sendRedirect(url) ;
			return ;
		}
	} // end HTTP POST

	/**
	Collects the parameters from the request object


	public Properties getParameters( HttpServletRequest req) throws ServletException, IOException {

	Properties userInfo = new Properties() ;
	// Lets get the parameters we know we are supposed to get from the request object
	String login_name = (req.getParameter("login_name")==null) ? "" : (req.getParameter("login_name")) ;
	String password1 = (req.getParameter("password1")==null) ? "" : (req.getParameter("password1")) ;
	String password2 = (req.getParameter("password2")==null) ? "" : (req.getParameter("password2")) ;

	String first_name = (req.getParameter("first_name")==null) ? "" : (req.getParameter("first_name")) ;
	String last_name = (req.getParameter("last_name")==null) ? "" : (req.getParameter("last_name")) ;
	String title = (req.getParameter("title")==null) ? "" : (req.getParameter("title")) ;
	String company = (req.getParameter("company")==null) ? "" : (req.getParameter("company")) ;

	String address = (req.getParameter("address")==null) ? "" : (req.getParameter("address")) ;
	String city = (req.getParameter("city")==null) ? "" : (req.getParameter("city")) ;
	String zip = (req.getParameter("zip")==null) ? "" : (req.getParameter("zip")) ;
	String country = (req.getParameter("country")==null) ? "" : (req.getParameter("country")) ;
	String country_council = (req.getParameter("country_council")==null) ? "" : (req.getParameter("country_council")) ;
	String email = (req.getParameter("email")==null) ? "" : (req.getParameter("email")) ;

	String user_type = (req.getParameter("user_type")==null) ? "" : (req.getParameter("user_type")) ;
	String active = (req.getParameter("active")==null) ? "0" : (req.getParameter("active")) ;

	//String admin_mode = (req.getParameter("admin_mode")==null) ? "NOT_USED" : (req.getParameter("admin_mode")) ;
	//String user_mode = (req.getParameter("user_mode")==null) ? "NOT_USED" : (req.getParameter("user_mode")) ;

	// Lets fix those fiels which arent mandatory
		if( title.trim().equals("")) title = "--" ;
		if( company.trim().equals("")) company = "--" ;
		if( address.trim().equals("")) address = "--" ;
		if( city.trim().equals("")) city = "--" ;
		if( zip.trim().equals("")) zip = "--" ;
		if( country.trim().equals("")) country = "--" ;
		if( country_council.trim().equals("")) country_council = "--" ;
		if( email.trim().equals("")) email = "--" ;
	if( user_type.trim().equals("")) user_type = "--" ;
		if( active.trim().equals("")) active = "--" ;

	userInfo.setProperty("login_name", login_name) ;
	userInfo.setProperty("password1", password1) ;
	userInfo.setProperty("password2", password2) ;
	userInfo.setProperty("first_name", first_name) ;
	userInfo.setProperty("last_name", last_name) ;
	userInfo.setProperty("title", title) ;
	userInfo.setProperty("company", company) ;

	userInfo.setProperty("address", address) ;
	userInfo.setProperty("city", city) ;
	userInfo.setProperty("zip", zip) ;
	userInfo.setProperty("country", country) ;
	userInfo.setProperty("country_council", country_council) ;
	userInfo.setProperty("email", email) ;
	userInfo.setProperty("user_type", user_type) ;
	userInfo.setProperty("active", active) ;

	//	userInfo.setProperty("admin_mode", admin_mode) ;
	//	userInfo.setProperty("user_mode", user_mode) ;


	//	this.log("UserInfo:" + userInfo.toString()) ;
	return userInfo ;
	}
	**/



	/**
	Collects the parameters from the request object
	**/
	/*
	private Properties getNewUserParameters( HttpServletRequest req) throws ServletException, IOException {

	Properties userInfo = new Properties() ;
	// Lets get the parameters we know we are supposed to get from the request object
	String login_name = (req.getParameter("login_name")==null) ? "" : (req.getParameter("login_name")) ;
	String password1 = (req.getParameter("password1")==null) ? "" : (req.getParameter("password1")) ;
	String password2 = (req.getParameter("password2")==null) ? "" : (req.getParameter("password2")) ;

	String first_name = (req.getParameter("first_name")==null) ? "" : (req.getParameter("first_name")) ;
	String last_name = (req.getParameter("last_name")==null) ? "" : (req.getParameter("last_name")) ;
	String address = (req.getParameter("address")==null) ? "" : (req.getParameter("address")) ;
	String city = (req.getParameter("city")==null) ? "" : (req.getParameter("city")) ;
	String zip = (req.getParameter("zip")==null) ? "" : (req.getParameter("zip")) ;
	String country = (req.getParameter("country")==null) ? "" : (req.getParameter("country")) ;
	String country_council = (req.getParameter("country_council")==null) ? "" : (req.getParameter("country_council")) ;
	String email = (req.getParameter("email")==null) ? "" : (req.getParameter("email")) ;

	// Lets fix those fiels which arent mandatory
	if( country.trim().equals("")) country = "--" ;
	if( country_council.trim().equals("")) country_council = "--" ;
	if( email.trim().equals("")) email = "--" ;

	userInfo.setProperty("login_name", login_name.trim()) ;
	userInfo.setProperty("password1", password1.trim()) ;
	userInfo.setProperty("password2", password2.trim()) ;
	userInfo.setProperty("first_name", first_name.trim()) ;
	userInfo.setProperty("last_name", last_name.trim()) ;
	userInfo.setProperty("address", address.trim()) ;
	userInfo.setProperty("city", city.trim()) ;
	userInfo.setProperty("zip", zip.trim()) ;
	userInfo.setProperty("country", country.trim()) ;
	userInfo.setProperty("country_council", country_council.trim()) ;
	userInfo.setProperty("email", email.trim()) ;
	//	this.log("UserInfo:" + userInfo.toString()) ;
	return userInfo ;
	}
	*/

	/**
	Collects the parameters from the request object
	*/

	private Properties getNewUserParameters( HttpServletRequest req) throws ServletException, IOException {

		Properties userInfo = new Properties() ;
		// Lets get the parameters we know we are supposed to get from the request object
		String login_name = (req.getParameter("login_name")==null) ? "" : (req.getParameter("login_name")) ;
		String password1 = (req.getParameter("password1")==null) ? "" : (req.getParameter("password1")) ;
		String password2 = (req.getParameter("password2")==null) ? "" : (req.getParameter("password2")) ;

		String first_name = (req.getParameter("first_name")==null) ? "" : (req.getParameter("first_name")) ;
		String last_name = (req.getParameter("last_name")==null) ? "" : (req.getParameter("last_name")) ;
		String title = (req.getParameter("title")==null) ? "" : (req.getParameter("title")) ;
		String company = (req.getParameter("company")==null) ? "" : (req.getParameter("company")) ;

		String address = (req.getParameter("address")==null) ? "" : (req.getParameter("address")) ;
		String city = (req.getParameter("city")==null) ? "" : (req.getParameter("city")) ;
		String zip = (req.getParameter("zip")==null) ? "" : (req.getParameter("zip")) ;
		String country = (req.getParameter("country")==null) ? "" : (req.getParameter("country")) ;
		String country_council = (req.getParameter("country_council")==null) ? "" : (req.getParameter("country_council")) ;
		String email = (req.getParameter("email")==null) ? "" : (req.getParameter("email")) ;

		String cCode = (req.getParameter("country_code")==null) ? "" : (req.getParameter("country_code")) ;
		String aCode = (req.getParameter("area_code")==null) ? "" : (req.getParameter("area_code")) ;
		String lCode = (req.getParameter("local_code")==null) ? "" : (req.getParameter("local_code")) ;

		String user_type = (req.getParameter("user_type")==null) ? "2" : (req.getParameter("user_type")) ;
		String active = (req.getParameter("active")==null) ? "1" : (req.getParameter("active")) ;

		// Lets fix those fiels which arent mandatory
		// Lets fix those fiels which arent mandatory
		if( title.trim().equals("")) title = "--" ;
		if( company.trim().equals("")) company = "--" ;
		if( address.trim().equals("")) address = "--" ;
		if( city.trim().equals("")) city = "--" ;
		if( zip.trim().equals("")) zip = "--" ;
		if( country.trim().equals("")) country = "--" ;
		if( country_council.trim().equals("")) country_council = "--" ;
		if( email.trim().equals("")) email = "--" ;

		if( cCode.trim().equals("")) cCode = "00" ;
		if( aCode.trim().equals("")) aCode = "00" ;
		if( lCode.trim().equals("")) lCode = "00" ;

		userInfo.setProperty("login_name", login_name.trim()) ;
		userInfo.setProperty("password1", password1.trim()) ;
		userInfo.setProperty("password2", password2.trim()) ;
		userInfo.setProperty("first_name", first_name.trim()) ;
		userInfo.setProperty("last_name", last_name.trim()) ;
		userInfo.setProperty("title", title.trim()) ;
		userInfo.setProperty("company", company.trim()) ;

		userInfo.setProperty("address", address.trim()) ;
		userInfo.setProperty("city", city.trim()) ;
		userInfo.setProperty("zip", zip.trim()) ;
		userInfo.setProperty("country", country.trim()) ;
		userInfo.setProperty("country_council", country_council.trim()) ;
		userInfo.setProperty("email", email.trim()) ;

		userInfo.setProperty("country_code", cCode.trim()) ;
		userInfo.setProperty("area_code", aCode.trim()) ;
		userInfo.setProperty("local_code", lCode.trim()) ;

		userInfo.setProperty("user_type", user_type.trim()) ;
		userInfo.setProperty("active", active.trim()) ;

		// this.log("UserInfo:" + userInfo.toString()) ;
		return userInfo ;
	}


	/**
	CheckUserparameters. Loops through the parameters and checks that they have
	been set to something
	*/
	private boolean checkUserParameters(Properties aPropObj) {
		// Ok, lets check that the user has typed anything in all the fields
		Enumeration enumValues = aPropObj.elements() ;
		Enumeration enumKeys = aPropObj.keys() ;
		while((enumValues.hasMoreElements() && enumKeys.hasMoreElements())) {
			Object oKeys = (enumKeys.nextElement()) ;
			Object oValue = (enumValues.nextElement()) ;
			String theVal = oValue.toString() ;
			if(theVal.equals(""))
				return false ;
		}

		return true ;
	} // checkUserParameters


	/**
	Validates the password. Password must contain at least 4 characters
	Generates an errorpage and returns false if something goes wrong


	private boolean verifyPassword(Properties prop, HttpServletRequest req,
	  HttpServletResponse res) throws ServletException, IOException {

	String pwd1 = prop.getProperty("password1") ;
	String pwd2 = prop.getProperty("password2") ;
	String header = "ConfLogin servlet" ;
	String msg = "" ;

	if( ! pwd1.equals(pwd2) ) {
	ConfError err = new ConfError(req,res,header,52, LOGIN_ERROR_HTML ) ;
				log(header + err.getErrorMsg()) ;
		return false ;
	}

	  if( pwd1.length() < 4) {
	ConfError err = new ConfError(req,res,header,53, LOGIN_ERROR_HTML ) ;
				log(header + err.getErrorMsg()) ;
		return false ;
			}

	return true ;

	} // End verifyPassword
	*/


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
			String header = "ConfLogin servlet. " ;
			ConfError err = new ConfError(req,res,header,50) ;
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
		System.out.println("ConfLogin: " + str ) ;
	}



	/**
	Adds the userInformation to the htmlPage. if an empty vector is sent as argument
	then an empty one will be created
	*/
	private VariableManager addUserInfo(VariableManager vm, Vector v) {
		// Here is the order in the vector
		// [3, Rickard, tynne, Rickard, Larsson, programmerare, imcode,  Drakarve, Havdhem, 620 11, Sweden, Gotland,
		// rickard@imcode.com, 0, 1001, 0, 1]
		//(v.get(1)==null) ? "" : (req.getParameter("password1")) ;

		if(v.size() == 0 || v.size() < 14 )
			for(int i = v.size(); i < 13; i++)
				v.add(i, "") ;


			vm.addProperty("LOGIN_NAME", v.get(1).toString()) ;
			vm.addProperty("PWD1", v.get(2).toString()) ;
			vm.addProperty("PWD2", v.get(2).toString()) ;
			vm.addProperty("FIRST_NAME", v.get(3).toString()) ;
			vm.addProperty("LAST_NAME", v.get(4).toString()) ;
			vm.addProperty("TITLE", v.get(5).toString()) ;
			vm.addProperty("COMPANY", v.get(6).toString()) ;

			vm.addProperty("ADDRESS", v.get(7).toString()) ;
			vm.addProperty("CITY", v.get(8).toString()) ;
			vm.addProperty("ZIP", v.get(9).toString()) ;
			vm.addProperty("COUNTRY", v.get(10).toString()) ;
			vm.addProperty("COUNTRY_COUNCIL", v.get(11).toString()) ;
			vm.addProperty("EMAIL", v.get(12).toString()) ;
			return vm ;
	}

	/**
	Creates a add new user sql string from the users information.
	**/
	private String createUserInfoString(Properties params, String newUserId) {

		String sqlStr = "" ;
		sqlStr += newUserId + ", " ;
		sqlStr +=	"'" + params.getProperty("login_name") + "', "  ;
		sqlStr += "'" + params.getProperty("password1") + "', ";
		sqlStr +=	"'" + params.getProperty("first_name") + "', ";
		sqlStr +=	"'" + params.getProperty("last_name") + "', ";
		sqlStr +=	"'" + params.getProperty("title") + "', ";
		sqlStr +=	"'" + params.getProperty("company") + "', ";
		sqlStr +=	"'" + params.getProperty("address") + "', ";
		sqlStr +=	"'" + params.getProperty("city") + "', ";
		sqlStr +=	"'" + params.getProperty("zip") + "', ";
		sqlStr +=	"'" + params.getProperty("country") + "', " ;
		sqlStr +=	"'" + params.getProperty("country_council") + "', ";
		sqlStr +=	"'" + params.getProperty("email") + "', ";

		// Default values for admin_mode, last_page, archive_mode, lang_id
		sqlStr +=	"0" + ", ";
		sqlStr += "1001" + ", ";
		sqlStr +=	"0" + ", ";
		sqlStr +=	"1" + ", " ;     // 1 stands for sweden
		sqlStr +=	"2" + ", " ;      // usertype. 0=special, 1=default, 2=conferenceuser
		sqlStr += "1" ;            // 1 stands for active
		// this.log("Userinfostring:" + sqlStr) ;
		return sqlStr ;

	} // End of createUserInfoString



	/**
	Returns a String, containing the userID in the request object.If something failes,
	a error page will be generated and null will be returned.
	*/

	private String getCurrentUserId(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		// Lets get the userId from the request Object.
		String userId = req.getParameter("user_id") ;
		if (userId == null ) {
			String header = "ConfLogin servlet." ;
			ConfError err = new ConfError(req,res,header,59, LOGIN_ERROR_HTML ) ;
			this.log(err.getErrorString()) ;
			return null;
		}
		else
			return userId ;

	} // End getCurrentUserId

} // End class
