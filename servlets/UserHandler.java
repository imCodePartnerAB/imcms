import java.io.* ;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.* ;
import imcode.external.diverse.* ;
import imcode.util.* ;

public class UserHandler extends Administrator {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

//	 String LOGIN = "userprefs_login.htm" ;
//    String LOGIN_FAILED = "userprefs_login_failed.htm"   ;
//    String CHANGE_PREFS = "userprefs_change.htm" ;

/**
	Executes the sproc xxxx which will update the users values in the db
**/

public static boolean updateUserInfoDB(String imcServer, Properties p) /*throws IOException */ {
  try {
	// Lets build the users information into a string and add it to db
	String userStr = createUserInfoString(p) ;
   IMCServiceRMI.sqlUpdateProcedure(imcServer, "UpdateUser " + userStr) ;
   return true ;

  } catch(IOException e) {
		return false ;
  }

}

/**
	Executes the sproc xxxx which will add the users values in the db
**/

public static void addUserInfoDB(String imcServer, String userStr) throws IOException  {
//  try {
	// Lets build the users information into a string and add it to db
   IMCServiceRMI.sqlUpdateProcedure(imcServer, "AddNewUser " + userStr) ;
  // return true ;

//  } catch(IOException e) {
//  		return false ;
// }

}




/**
	Creates hea sql string string used to run sproc updateUser
**/

public static String createUserInfoString(Properties params) {

	String sqlStr = "" ;
	sqlStr += params.getProperty("user_id") + ", " ;
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
	sqlStr +=	"'" + params.getProperty("email") + "', "  ;

// Default values for admin_mode, last_page, archive_mode, lang_id
	sqlStr +=	"0" + ", ";
	sqlStr += "1001" + ", ";
	sqlStr +=	"0" + ", ";

	sqlStr += params.getProperty("lang_id") + ", "  ;   // lang_id
	//sqlStr +=	"1" + ", " ;     // 1 står för sverige
	sqlStr +=	params.getProperty("user_type") + ", " ;
	sqlStr +=	params.getProperty("active")  ;

//	this.log("Userinfostring:" + sqlStr) ;
	return sqlStr ;

} // End of createUserInfoString

/**
   Creates a properties with all the necessary props used to update
   a users prefs in the users table in the database
*/
public static Properties doUpdateDefaults() {

	Properties p = new Properties() ;

   p.setProperty("user_id", "") ;
   p.setProperty("login_name", "") ;
	p.setProperty("password1", "") ;
	p.setProperty("first_name", "") ;
	p.setProperty("last_name", "") ;
	p.setProperty("title", "") ;
	p.setProperty("company", "") ;
	p.setProperty("address", "") ;
	p.setProperty("city", "") ;
	p.setProperty("zip", "") ;
	p.setProperty("country", "") ;
	p.setProperty("country_council", "") ;
	p.setProperty("email", "") ;

 //  p.setProperty("admin_mode", "") ;
 //	p.setProperty("last_page", "") ;
 //	p.setProperty("archive_mode", "") ;
 //	p.setProperty("lang_id", "") ;

 //	p.setProperty("user_type", "") ;
 //	p.setProperty("active", "") ;
   return p ;

}  // End of

/**
	Returns a Properties, containing the user information from the html page. if Something
	failes, a error page will be generated and null will be returned.
*/




/**
	Collects all userparameters from the users table in the db
   Returns null if something goes wrong
**/

public static Properties getUserInfoDB(String imcServer, String userId) /*throws IOException */ {

  // Get default props
	Properties p = doDefaultUser() ;
  try {
	Hashtable h = IMCServiceRMI.sqlQueryHash(imcServer, "GetUserInfo " + userId) ;
	Enumeration keys = h.keys() ;
	while( keys.hasMoreElements() ) {
		Object key = keys.nextElement() ;
	  String[] values = (String[]) h.get(key) ;
	  String aValue = values[0] ;
	  p.setProperty(key.toString(), aValue ) ;
	}

	return p ;
  } catch(IOException e) {
		return null ;
  }

}

/**
   Creates a properties with all the users properties from the
   users table. All keys are here, but not the values
*/
public static Properties doDefaultUser() {

	Properties p = new Properties() ;

   p.setProperty("user_id", "") ;
   p.setProperty("login_name", "") ;
	p.setProperty("login_password", "") ;
	p.setProperty("first_name", "") ;
	p.setProperty("last_name", "") ;
	p.setProperty("title", "") ;
	p.setProperty("company", "") ;
	p.setProperty("address", "") ;
	p.setProperty("city", "") ;
	p.setProperty("zip", "") ;
	p.setProperty("country", "") ;
	p.setProperty("country_council", "") ;
	p.setProperty("email", "") ;

   p.setProperty("admin_mode", "") ;
	p.setProperty("last_page", "") ;
	p.setProperty("archive_mode", "") ;
	p.setProperty("lang_id", "") ;

	p.setProperty("user_type", "") ;
	p.setProperty("active", "") ;
	p.setProperty("create_date", "") ;
   return p ;

}  // End of


/**
   Compares 2 properties, checks if the first properties all keys is
   valid in the second, which means that they are NOT  ""
*/
public static boolean checkNecessaryParameters(Properties necessary, Properties all) {

	// Ok, lets check that the user has typed anything in all the fields
  //Enumeration enumValues = all.elements() ;
  Enumeration necKeys = necessary.keys() ;
  while( necKeys.hasMoreElements() ) {
	String aKey = (String) necKeys.nextElement() ;
	if( all.getProperty(aKey) == null || all.getProperty(aKey).equals("") ) ;
	 return false 	;
  }
  return true ;
} // checkparameters



/**
	Collects all userparameters from a request object.
   Does not handle roles. Collects the parameters  from the request parameters
   The name of the parameters are the same as those in the database.


public static Properties getUserParams( HttpServletRequest req) {

	Properties userInfo = new Properties() ;
// Lets get the parameters we know we are supposed to get from the request object
	String user_id = (req.getParameter("user_id")==null) ? "" : (req.getParameter("user_id")) ;
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

   String lang_id = (req.getParameter("lang_id")==null) ? "" : (req.getParameter("lang_id")) ;
	String user_type = (req.getParameter("user_type")==null) ? "" : (req.getParameter("user_type")) ;
	String active = (req.getParameter("active")==null) ? "" : (req.getParameter("active")) ;

	String create_date = (req.getParameter("create_date")==null) ? "" : (req.getParameter("create_date")) ;

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

	userInfo.setProperty("user_id", user_id) ;
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
   userInfo.setProperty("lang_id", lang_id) ;
	userInfo.setProperty("user_type", user_type) ;
	userInfo.setProperty("active", active) ;

   userInfo.setProperty("create_date", create_date) ;
//	userInfo.setProperty("admin_mode", admin_mode) ;
//	userInfo.setProperty("user_mode", user_mode) ;


//	this.log("UserInfo:" + userInfo.toString()) ;
	return userInfo ;
}
 */

/**
	Collects all userparameters from a request object.
   Does not handle roles. Collects the parameters  from the request parameters
   The name of the parameters are the same as those in the database.


public static Properties getUserParams( HttpServletRequest req) {

	Properties userInfo = new Properties() ;
// Lets get the parameters we know we are supposed to get from the request object
	String user_id =  req.getParameter("user_id") ;
	String login_name = req.getParameter("login_name") ;
	String password1 =  req.getParameter("password1") ;
	String password2 =  req.getParameter("password2") ;

	String first_name =  req.getParameter("first_name") ;
	String last_name = req.getParameter("last_name") ;
   String title = req.getParameter("title") ;
	String company =  req.getParameter("company") ;

	String address =  req.getParameter("address") ;
	String city =req.getParameter("city") ;
	String zip = req.getParameter("zip") ;
	String country = req.getParameter("country") ;
	String country_council =  req.getParameter("country_council") ;
	String email = req.getParameter("email") ;
   String lang_id = req.getParameter("lang_id") ;
	String user_type = req.getParameter("user_type") ;
	String active = req.getParameter("active") ;

	String create_date =req.getParameter("create_date") ;

	//String admin_mode = (req.getParameter("admin_mode")==null) ? "NOT_USED" : (req.getParameter("admin_mode")) ;
	//String user_mode = (req.getParameter("user_mode")==null) ? "NOT_USED" : (req.getParameter("user_mode")) ;

	// Lets fix those fiels which arent mandatory
/// 	if( address.trim().equals("")) address = "--" ;
// 	if( city.trim().equals("")) city = "--" ;
// 	if( zip.trim().equals("")) zip = "--" ;
 //	if( country.trim().equals("")) country = "--" ;
 //	if( country_council.trim().equals("")) country_council = "--" ;
// 	if( email.trim().equals("")) email = "--" ;

	userInfo.setProperty("user_id", user_id) ;
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
   userInfo.setProperty("lang_id", lang_id) ;
	userInfo.setProperty("user_type", user_type) ;
	userInfo.setProperty("active", active) ;

   userInfo.setProperty("create_date", create_date) ;
//	userInfo.setProperty("admin_mode", admin_mode) ;
//	userInfo.setProperty("user_mode", user_mode) ;


//	this.log("UserInfo:" + userInfo.toString()) ;
	return userInfo ;
}
*/

/**
	Creates a sql string used to save a users prefs

**/
/*
public String fixUpdateUserSql(Properties p, String newUserId) {

	StringBuffer sql = new StringBuffer(100) ;
   sql.append(newUserId + ", ") ;
   sql.append(newUserId + ", ") ;
   sql.append( "'" + p.getProperty("login_name") + "', " ) ;
	sql.append( "'" + p.getProperty("password1") + "', ";
	sqlStr +=	"'" + p.getProperty("first_name") + "', ";
	sqlStr +=	"'" + p.getProperty("last_name") + "', ";
	sqlStr +=	"'" + p.getProperty("first_name") + "', ";
	sqlStr +=	"'" + p.getProperty("last_name") + "', ";

	sqlStr +=	"'" + p.getProperty("address") + "', ";
	sqlStr +=	"'" + p.getProperty("city") + "', ";
	sqlStr +=	"'" + p.getProperty("zip") + "', ";
	sqlStr +=	"'" + p.getProperty("country") + "', " ;
	sqlStr +=	"'" + p.getProperty("country_council") + "', ";
	sqlStr +=	"'" + p.getProperty("email") + "', "  ;



	String sqlStr = "" ;
	sqlStr += newUserId + ", " ;
	sqlStr +=	"'" + p.getProperty("login_name") + "', "  ;
	sqlStr += "'" + p.getProperty("password1") + "', ";
	sqlStr +=	"'" + p.getProperty("first_name") + "', ";
	sqlStr +=	"'" + p.getProperty("last_name") + "', ";
	sqlStr +=	"'" + p.getProperty("first_name") + "', ";
	sqlStr +=	"'" + p.getProperty("last_name") + "', ";

	sqlStr +=	"'" + p.getProperty("address") + "', ";
	sqlStr +=	"'" + p.getProperty("city") + "', ";
	sqlStr +=	"'" + p.getProperty("zip") + "', ";
	sqlStr +=	"'" + p.getProperty("country") + "', " ;
	sqlStr +=	"'" + p.getProperty("country_council") + "', ";
	sqlStr +=	"'" + p.getProperty("email") + "', "  ;

// Default values for admin_mode, last_page, archive_mode, lang_id
	sqlStr +=	"0" + ", ";
	sqlStr += "1001" + ", ";
	sqlStr +=	"0" + ", ";
	sqlStr +=	"1" + ", " ;     // 1 står för sverige
	sqlStr +=	p.getProperty("user_type") + ", " ;
	sqlStr +=	p.getProperty("active")  ;

//	this.log("Userinfostring:" + sqlStr) ;
	return sqlStr ;

} // End of createUserInfoString

 */




/*
	public static void saveUserPrefs(P req, HttpServletResponse res)
		throws ServletException, IOException {
/*
	// Lets get the userId from the request Object.
	RmiLayer imc = new RmiLayer(user) ;
		String userId = this.getCurrentUserId(req,res) ;
		if (userId == null)	return ;

  // Lets get the parameters from html page and validate them
		Properties params = this.getParameters(req) ;

  // Lets check the password. if its empty, then it wont be updated. get the
  // old password from db and use that one instad
	String currPwd = imc.execSQlProcedureStr(server, "GetUserPassword " + userId ) ;
	if( currPwd.equals("-1") ) {
		String header = "Fel! Ett lösenord kund inte hittas" ;
		  String msg = "Lösenord kunde inte hittas"+ "<BR>" ;
			this.log(header + msg) ;
			AdminError err = new AdminError(req,res,header,msg) ;
			return ;

	}
	if(params.getProperty("password1").equals("")) {
		params.setProperty("password1", currPwd) ;
		params.setProperty("password2", currPwd) ;
	}
		params = this.validateParameters(params,req,res) ;
		if(params == null) return ;

// Lets get the roles from htmlpage
		Vector rolesV = this.getRolesParameters(req, res) ;
		if( rolesV == null) return ;

	// Lets check if the password contains something. If it doesnt
  // contain anything, then assume that the old one wont be updated
		if( this.verifyPassword(params,req,res) == false)	return ;

	// Lets build the users information into a string and add it to db
	String userStr = createUserInfoString(params, userId) ;
	imc.execSqlUpdateProcedure(server, "UpdateUser " + userStr) ;

	// Lets add the new users roles. but first, delete users current Roles
	// and then add the new ones
		imc.execSqlUpdateProcedure(server, "DelUserRoles " + userId ) ;

		for(int i = 0; i<rolesV.size(); i++){
			String aRole = rolesV.elementAt(i).toString() ;
		imc.execSqlUpdateProcedure(server, "AddUserRole " + userId + ", " + aRole) ;
		}

		this.goAdminUsers(req, res) ;
	this.log("SAVED_CHANGED_USER är klar") ;
	return ;
  }

*/



/**
		POST
	public void doPost(HttpServletRequest req, HttpServletResponse res)
	  throws ServletException, IOException {

	  String host 				= req.getHeader("Host") ;
	  String server 			= Utility.getDomainPref("adminserver",host) ;
	 String adminServer 	= Utility.getDomainPref("adminserver",host) ;

	// Lets validate the session
//	  if (super.checkSession(req,res) == false)	return ;

	// Lets get an user object
	 imcode.server.User user = super.getUserObj(req,res) ;
	  RmiConf rmi = new RmiConf(user) ;
	 String userId = null ;

   // Get the session
	 HttpSession session = req.getSession(true) ;

  // ************* Verfiy username and password **************

	 if(req.getParameter("verifyUser") != null) {
			String loginName = (req.getParameter("login_name")==null) ? "" : (req.getParameter("login_name")) ;
		   String password = (req.getParameter("password")==null) ? "" : (req.getParameter("password")) ;

		// Validate loginparams against the DB
			String sqlQ = "GetUserIdFromName '" + loginName + "', '" + password + "'" ;
			userId = rmi.execJanusSqlProcedureStr(server, sqlQ ) ;
		//	log("GetUserIdFromName ok") ;

		// Lets check that we the found the user. Otherwise send unvailid username password
			if( userId == null ) {
				String header = req.getServletPath() ;
				AdminError2 err = new AdminError2(req,res,header,50) ;
				log(header + err.getErrorMsg()) ;
				return ;
			}

		// Lets check if the users password are correct
		String currPass = rmi.execJanusSqlProcedureStr(server, "GetUserPassword " + userId ) ;
		if(!currPass.equals(password)) {
				String header = req.getServletPath() ;
				AdminError2 err = new AdminError2(req,res,header,51) ;
				log(header + err.getErrorMsg()) ;
				return ;
		}

	  // Ok, the user is verified, lets generate the users infopage
	  // Lets set the user id we are working on
		//	HttpSession session = req.getSession(true) ;
			if(session != null  ) {
			  session.setAttribute("AdminUser.user_id", userId ) ;
		   session.setAttribute("AdminUser.passedLogin", "1" ) ;
			}

	  // ok, redirect to myself
		 res.sendRedirect("UserChangePrefs?changeUser=on");
		 return ;
	  } // end verifyUser

	// Lets generate the users info
	 if(req.getParameter("changeUser") != null) {
	   session = req.getSession(false) ;
		 if(session == null ) {
			String header = req.getServletPath() ;
		AdminError2 err = new AdminError2(req,res,header,52) ;
		log(header + err.getErrorMsg()) ;
			return ;
		 }

		 if(session != null  ) {
		 userId = (String) session.getAttribute("AdminUser.user_id") ;
	   }

	   String showUserInfo = (String) session.getAttribute("AdminUser.passedLogin" ) ;
	   if(showUserInfo.equals("1")) {
		// Ok, we got the user. Lets get his settings.
		VariableManager vm = new VariableManager() ;
		String[] userInfo = rmi.execJanusSqlProcedure(server, "UserPrefsChange " + userId ) ;
		String[] keys = {"USER_ID","LOGIN_NAME","LOGIN_PASSWORD","FIRST_NAME",
			"LAST_NAME","TITLE", "COMPANY", "ADDRESS","CITY","ZIP","COUNTRY","COUNTRY_COUNCIL",
			"EMAIL" } ;
	  //log(hash.toString()) ;
	  //Vector userV = this.convert2Vector(userInfo) ;
		for(int i = 0 ; i<keys.length; i++) {
			vm.addProperty(keys[i], userInfo[i]);
		}

	  // Lets fix all users phone numbers from DB
			String[] phonesArr = rmi.execJanusSqlProcedure(server, "GetUserPhones " + userId) ;
			Vector phonesV  = super.convert2Vector(phonesArr) ;
		 Html htm = new Html() ;
			String phones = htm.createHtmlCode("ID_OPTION", "", phonesV ) ;
			vm.addProperty("PHONES_MENU", phones  ) ;
		 vm.addProperty("CURR_USER_ID", userId  ) ;

	// Lets set the user id we are working on
			// HttpSession session = req.getSession(true) ;
			String theUserId = null ;
			if(session != null  ) {
			  session.setAttribute("AdminUser.user_id", userId ) ;
		   session.setAttribute("AdminUser.passedLogin", "1" ) ;
			}

	 // Lets generete the change user page
			this.sendHtml(req,res,vm, CHANGE_PREFS ) ;
			return ;
	 }
	}

   // ********** SAVE A USER *************
	   if( req.getParameter("save_changes") != null ) {
			log("Ok, save user parameters") ;
		  res.sendRedirect("UserChangePrefs?changeUser=on");
		  return ;
	   }

  // Lets check if the user wants to edit his phonenumbers
  // ******* Edit phones fields **********
	if( req.getParameter("edit_phones") != null ) {
		log("Ok, edit phones") ;
	  String user_id = req.getParameter("CURR_USER_ID") ;
	  if(user_id == null) {
		String header = req.getServletPath() ;
		AdminError2 err = new AdminError2(req,res,header,51) ;
		log(header + err.getErrorMsg()) ;
		  return ;
	  }

	  res.sendRedirect("AdminUserPhones?user_id=" + user_id);
   }   // End of editphones

  // ****** Default option. Ok, Generate a login page to the user
	VariableManager vm = new VariableManager() ;
   vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req) ) ;
	Html ht = new Html() ;
   this.sendHtml(req,res,vm, LOGIN ) ;
   return ;

} // end HTTP POST
*/

/**
	Adds the userInformation to the htmlPage. if an empty vector is sent as argument
	then an empty one will be created
**/
public static VariableManager addUserInfo(VariableManager vm, Vector v) {
	// Here is the order in the vector
	// [3, Rickard, tynne, Rickard, Larsson, Drakarve, Havdhem, 620 11, Sweden, Gotland,
	// rickard@imcode.com, 0, 1001, 0, 1]
	//(v.get(1)==null) ? "" : (req.getParameter("password1")) ;

	if(v.size() == 0) {
		for(int i = 0; i < 20; i++)
			v.add(i, "") ;
	}

	vm.addProperty("LOGIN_NAME", v.get(1).toString()) ;
	vm.addProperty("PWD1", v.get(2).toString()) ;
	vm.addProperty("PWD2", v.get(2).toString()) ;
//	vm.addProperty("PWD1", "") ;
//	vm.addProperty("PWD2", "") ;
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
	vm.addProperty("ACTIVE", "13") ;

  return vm ;
}


	public void log( String str) {
		super.log(str) ;
	  System.out.println("UserChangePrefs: " + str ) ;
	}


/**
		The getLoginParams method gets the login params from the requstobject
**/

	public static Properties getLoginParams(HttpServletRequest req, HttpServletResponse res)
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
		Validates the password. Password must contain at least 4 characters
		Generates an errorpage and returns false if something goes wrong
	*/

	public static boolean verifyPassword(Properties prop, HttpServletRequest req,
		  HttpServletResponse res) throws ServletException, IOException {

			String pwd1 = prop.getProperty("password1") ;
			String pwd2 = prop.getProperty("password2") ;
			String header = "Verify password error" ;
			String msg = "" ;

			if( ! pwd1.equals(pwd2) ) {
			header = req.getServletPath() ;
		   AdminError2 err = new AdminError2(req,res,header,52) ;
		   //log(header + err.getErrorMsg()) ;
			   return false ;
		 }

		   if( pwd1.length() < 4) {
			  header = req.getServletPath() ;
		  AdminError2 err = new AdminError2(req,res,header,53) ;
		  //log(header + err.getErrorMsg()) ;
			  return false ;
		   }

			return true ;

	} // End verifyPassword

	/**
		Validates the phonenumber. Password must contain at least 4 characters
		Generates an errorpage and returns false if something goes wrong
	*/

	public static boolean verifyPhoneNumber(Properties prop, HttpServletRequest req,
		  HttpServletResponse res) throws ServletException, IOException {
	 try {
	   String[] arr = {
		prop.getProperty("country_code"),
		prop.getProperty("area_code"),
		 prop.getProperty("local_code")
	   } ;

		for(int i = 0 ; i< arr.length ; i++ ) {
		  Integer.parseInt(arr[i]) ;
		}

	 } catch(NumberFormatException e) {
			// log(e.getMessage()) ;
		 AdminError2 err = new AdminError2(req,res,"",63 ) ;
		 return false ;
	 } catch(NullPointerException e) {
			// log(e.getMessage()) ;
		 AdminError2 err = new AdminError2(req,res,"",63 ) ;
		 return false ;
	 }
	  return true ;

	} // End phonenumber

/**
		Validates the username. Returns true if the login_name doesnt exists.
	  Returns false if the username exists
*/
 public static boolean checkExistingUserName(String imcServer, Properties prop) {
  try {
		String userName = prop.getProperty("login_name") ;
		String userNameExists[] = IMCServiceRMI.sqlProcedure(imcServer, "FindUserName '" + userName + "'") ;
		if(userNameExists != null ) {
			if(userNameExists.length > 0 ) {
			  //	String header = "Error in AdminUserProps." ;
			  //  String msg = "The username already exists, please change the username."+ "<BR>" ;
		  //  this.log(header + msg) ;
			  // AdminError err = new AdminError(req,res,header,msg) ;
			  return false;
			}
		}
	return true ;

  } catch(IOException e) {
		return false ;
  }
 } // CheckExistingUserName

} // End of class
