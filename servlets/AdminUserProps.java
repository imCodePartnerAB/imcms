import java.io.* ;
import javax.servlet.*;
import javax.servlet.http.*;
import java.awt.* ;
import java.util.* ;
import imcode.external.diverse.* ;
import imcode.util.* ;

public class AdminUserProps extends Administrator {
	String HTML_TEMPLATE = "AdminUserResp.htm";

/**
 * POST
**/

public void doPost(HttpServletRequest req, HttpServletResponse res)
  throws ServletException, IOException {

  String host 				= req.getHeader("Host") ;
  String server 			= Utility.getDomainPref("adminserver",host) ;

  // Lets validate the session
   if (super.checkSession(req,res) == false) return ;

  // Lets get an user object
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) {
	  String header = "Error in AdminCounter." ;
	  String msg = "Couldnt create an user object."+ "<BR>" ;
	  this.log(header + msg) ;
	  AdminError err = new AdminError(req,res,header,msg) ;
	  return ;
	}

	// Lets check if the user is an admin, otherwise throw him out.
	if (super.checkAdminRights(server, user) == false) {
		  String header = "Error in AdminCounter." ;
		  String msg = "The user is not an administrator."+ "<BR>" ;
		  this.log(header + msg) ;
		  AdminError err = new AdminError(req,res,header,msg) ;
		  return ;
	}

// Lets check which button was pushed
		String adminTask = req.getParameter("adminTask") ;
		this.log("Argument till server:" + adminTask) ;
		if(adminTask == null)	adminTask = "" ;

	// ******* SAVE NEW USER TO DB **********
  if( req.getParameter("SAVE_USER") != null && adminTask.equalsIgnoreCase("ADD_USER") ) {
		log("Lets add a new user to db") ;
  // Lets get the parameters from html page and validate them
		Properties params = this.getParameters(req) ;
		params = this.validateParameters(params,req,res) ;
		if(params == null) return ;

	// Lets get the roles from htmlpage
		Vector rolesV = this.getRolesParameters(req, res) ;
		if( rolesV == null) return ;

	// Lets validate the password
		if( UserHandler.verifyPassword(params,req,res) == false)	return ;

	// Lets check that the new username doesnt exists already in db
		RmiLayer imc = new RmiLayer(user) ;
		String userName = params.getProperty("login_name") ;
		String userNameExists[] = imc.execSqlProcedure(server, "FindUserName '" + userName + "'") ;

	// log("SQL fråga:" + "FindUserName '" + userName + "'") ;
		if(userNameExists != null ) {
			if(userNameExists.length > 0 ) {
				String header = "Error in AdminUserProps." ;
				String msg = "The username already exists, please change the username."+ "<BR>" ;
				this.log(header + msg) ;
				AdminError err = new AdminError(req,res,header,msg) ;
				return ;
			}
		}

	// Lets get the highest userId
		String newUserId = getNewUserID(imc,req,res) ;
		if( newUserId == null) return ;
	  log("NewUserId: " + newUserId) ;

	// Lets build the users information into a string and add it to db
	  params.setProperty("user_id", newUserId) ;
	  String userStr = UserHandler.createUserInfoString(params) ;
		//String userStr = createUserInfoString(params, newUserId) ;
	  log("AddNewUser " + userStr) ;
		imc.execSqlUpdateProcedure(server, "AddNewUser " + userStr) ;
	  // log("Lade till ny användare: " + newUserId) ;
	// Lets add the new users roles
			for(int i = 0; i<rolesV.size(); i++){
				String aRole = rolesV.elementAt(i).toString() ;
			imc.execSqlUpdateProcedure(server, "AddUserRole " + newUserId + ", " + aRole) ;
			}

		 this.goAdminUsers(req, res) ;
		 return ;
	}

	// ******** SAVE EXISTING USER TO DB ***************
	if( req.getParameter("SAVE_USER") != null && adminTask.equalsIgnoreCase("SAVE_CHANGED_USER")) {

	// Lets get the userId from the request Object.
	RmiLayer imc = new RmiLayer(user) ;
		String userId = this.getCurrentUserId(req,res) ;
	  log("UserID: " + userId) ;
		if (userId == null)	return ;

	// Lets get the parameters from html page and validate them
		Properties params = this.getParameters(req) ;
	  params.setProperty("user_id", userId) ;

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

	// Ok, Lets validate all fields
		params = this.validateParameters(params,req,res) ;
		if(params == null) return ;

	// Lets get the roles from htmlpage
		Vector rolesV = this.getRolesParameters(req, res) ;
		if( rolesV == null) return ;

	// Lets check if the password contains something. If it doesnt
	// contain anything, then assume that the old one wont be updated
		if( UserHandler.verifyPassword(params,req,res) == false)	return ;

	// Lets build the users information into a string and add it to db
	  String userStr = "UpdateUser " + UserHandler.createUserInfoString(params) ;
	  log("userSQL: " + userStr) ;
	  imc.execSqlUpdateProcedure(server, userStr) ;

	// Lets add the new users roles. but first, delete users current Roles
	// and then add the new ones
	  imc.execSqlUpdateProcedure(server, "DelUserRoles " + userId ) ;
	  for(int i = 0; i<rolesV.size(); i++){
		String aRole = rolesV.elementAt(i).toString() ;
		imc.execSqlUpdateProcedure(server, "AddUserRole " + userId + ", " + aRole) ;
	  }

	  this.goAdminUsers(req, res) ;
	  // this.log("SAVED_CHANGED_USER är klar") ;
	  return ;
  }

  // Phones
  // ******* Edit phones fields **********
   if( req.getParameter("edit_phones") != null ) {
	 log("Ok, edit phones") ;
		String userId = this.getCurrentUserId(req,res) ;
	  if (userId == null)	{
		 log("No user_id was found") ;
		 return ;
	  }
	// Lets update the sessions DISC_ID
	  HttpSession session = req.getSession(false) ;
	  if(session != null  ) {
		  session.putValue("AdminUser.user_id", userId ) ;
		  //userId = (String) session.getValue("AdminUser.user_id") ;
	  }
		  res.sendRedirect("AdminUserPhones?user_id=" + userId );
	  return ;
   }


  // ******** CANCEL_PHONE ***************
		if( req.getParameter("CANCEL_PHONE") != null ) {
	  //this.goAdminUsers(req, res) ;
			 String url = MetaInfo.getServletPath(req) ;
			 url += "AdminUser" ;
		 res.sendRedirect(url) ;
		return ;
		}

	// ******** GO_BACK TO THE MENY ***************
		if( req.getParameter("GO_BACK") != null ) {
			String url = MetaInfo.getServletPath(req) ;
			url += "AdminUser" ;
		res.sendRedirect(url) ;
		return ;
		}

	// ******** UNIDENTIFIED ARGUMENT TO SERVER ********
		this.log("Unidentified argument was sent!") ;
	doGet(req,res) ;
	return ;

} // end HTTP POST

/**
	Returns a String, containing the newUserID. if something failes, a error page
	will be generated and null will be returned.
*/

public String getNewUserID(RmiLayer imc, HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	 String host 				= req.getHeader("Host") ;
	 String server 			= Utility.getDomainPref("adminserver",host) ;
	log("host: " + host) ;
	log("server: " + server) ;

	String newUserId = imc.execSQlProcedureStr(server, "GetHighestUserId" ) ;
  // log("va fan") ;
		if ( newUserId.equals("") ) {
			String aHeader = "AddNewUser" ;
			String msg = "SP: GetHighestUserId misslyckades" ;
			this.log("msg") ;
			AdminError err = new AdminError(req,res,aHeader, msg) ;
			return null;
		}
		return newUserId ;

} // End GetNewUserID


/**
	Returns a Vector, containing the choosed roles from the html page. if Something
	failes, a error page will be generated and null will be returned.
*/

public Vector getRolesParameters(HttpServletRequest req, HttpServletResponse res)
	  throws ServletException, IOException {

// Lets get the roles
	  // Vector rolesV = this.getRolesParameters(req) ;
	  String[] roles = (req.getParameterValues("roles")==null) ? new String[0] : (req.getParameterValues("roles"));
		Vector rolesV = convert2Vector(roles) ;
		if(rolesV.size() == 0) {
			String header = "Roles error" ;
	  String msg = "Ingen roll var vald." + "<BR>";
		  this.log("Error in checking roles") ;
	  AdminError err = new AdminError(req,res,header, msg) ;
		  return null;
		}
	//this.log("Roles:"+ rolesV.toString()) ;
	return rolesV ;

} // End getRolesParameters



/**
	Adds the userInformation to the htmlPage. if an empty vector is sent as argument
	then an empty one will be created

public VariableManager addUserInfo(VariableManager vm, Vector v) {
	// Here is the order in the vector
	// [3, Rickard, tynne, Rickard, Larsson, Drakarve, Havdhem, 620 11, Sweden, Gotland,
	// rickard@imcode.com, 0, 1001, 0, 1]
	//(v.get(1)==null) ? "" : (req.getParameter("password1")) ;

	if(v.size() == 0)
		for(int i = 0; i < 11; i++)
			v.add(i, "") ;


	vm.addProperty("LOGIN_NAME", v.get(1).toString()) ;
	vm.addProperty("PWD1", v.get(2).toString()) ;
	vm.addProperty("PWD2", v.get(2).toString()) ;
	vm.addProperty("FIRST_NAME", v.get(3).toString()) ;
	vm.addProperty("LAST_NAME", v.get(4).toString()) ;
   vm.addProperty("TITLE", v.get(5).toString()) ;
	vm.addProperty("COMPANY", v.get(6).toString()) ;

	vm.addProperty("ADDRESS", v.get(5).toString()) ;
	vm.addProperty("CITY", v.get(6).toString()) ;
	vm.addProperty("ZIP", v.get(7).toString()) ;
	vm.addProperty("COUNTRY", v.get(8).toString()) ;
	vm.addProperty("COUNTRY_COUNCIL", v.get(9).toString()) ;
	vm.addProperty("EMAIL", v.get(10).toString()) ;
	vm.addProperty("USER_TYPE", v.get(11).toString()) ;
	return vm ;
}
 **/

/**
	Creates a sql string string from the html page


public String createUserInfoString(Properties params, String newUserId) {

	String sqlStr = "" ;
	sqlStr += newUserId + ", " ;
	sqlStr +=	"'" + params.getProperty("login_name") + "', "  ;
	sqlStr += "'" + params.getProperty("password1") + "', ";
	sqlStr +=	"'" + params.getProperty("first_name") + "', ";
	sqlStr +=	"'" + params.getProperty("last_name") + "', ";
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
	sqlStr +=	"1" + ", " ;     // 1 står för sverige
	sqlStr +=	params.getProperty("user_type") + ", " ;
	sqlStr +=	params.getProperty("active")  ;

//	this.log("Userinfostring:" + sqlStr) ;
	return sqlStr ;

} // End of createUserInfoString
  **/

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
   String title = req.getParameter("title") ;
	String company =  req.getParameter("company") ;

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

	userInfo.setProperty("login_name", login_name) ;
	userInfo.setProperty("password1", password1) ;
	userInfo.setProperty("password2", password2) ;
	userInfo.setProperty("first_name", first_name) ;
	userInfo.setProperty("last_name", last_name) ;
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
		Validates the password. Password must contain at least 4 characters
		Generates an errorpage and returns false if something goes wrong


	private boolean verifyPassword(Properties prop, HttpServletRequest req,
		  HttpServletResponse res) throws ServletException, IOException {

			String pwd1 = prop.getProperty("password1") ;
			String pwd2 = prop.getProperty("password2") ;
			String header = "Verify password error" ;
			String msg = "" ;

			if( ! pwd1.equals(pwd2) )
				msg = "Lösenorden var felaktigt verifierade. Ange nytt lösenord!" ;
		  if( pwd1.length() < 4)
			msg = "Ett lösenord måste vara minst fyra tecken långt. Ange nytt lösenord!" ;

		  if( ! msg.equalsIgnoreCase("") ) {
			this.log("Verifieringen av lösenordet misslyckades.") ;
		AdminError err = new AdminError(req,res,header, msg) ;
			return false ;
			}
			return true ;

	} // End verifyPassword
  */

	public void log( String str) {
			super.log(str) ;
		  System.out.println("AddNewUser: " + str ) ;
	}

	/**
		Returns to the adminUsers meny
	*/

	public void goAdminUsers(HttpServletRequest req, HttpServletResponse res)
		 throws ServletException, IOException {
		String servletPath = MetaInfo.getServletPath(req) ;
		res.sendRedirect(servletPath + "AdminUser") ;
	}

  /**
	Returns a String, containing the userID in the request object.If something failes,
	a error page will be generated and null will be returned.
*/

public String getCurrentUserId(HttpServletRequest req, HttpServletResponse res)
	  throws ServletException, IOException {

// Lets get the userId from the request Object.
		String userId = req.getParameter("user_Id") ;
			if (userId == null)
				userId = req.getParameter("CURR_USER_ID") ;

			if (userId == null ) {
				String header = "AdminUserProps error. " ;
		String msg = "No user_id was available." + "<BR>";
			this.log(header + msg) ;
		AdminError err = new AdminError(req,res,header, msg) ;
			return null;
			} else
				this.log("AnvändarId=" + userId) ;
				return userId ;

	  } // End getCurrentUserId


  /**
	Service method. Sends the user to the post method
**/

public void service (HttpServletRequest req, HttpServletResponse res)
	  throws ServletException, IOException {

		String action = req.getMethod() ;
		// log("Action:" + action) ;
			  this.doPost(req,res) ;
 }


  /**
	Collects the parameters from the request object
**/

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
	String language = (req.getParameter("lang_id")==null) ? "1" : (req.getParameter("lang_id")) ;

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
	userInfo.setProperty("lang_id", language) ;

//	userInfo.setProperty("admin_mode", admin_mode) ;
//	userInfo.setProperty("user_mode", user_mode) ;


//	this.log("UserInfo:" + userInfo.toString()) ;
	return userInfo ;
}

/**
	Returns a Properties, containing the user information from the html page. if Something
	failes, a error page will be generated and null will be returned.
*/

public Properties validateParameters(Properties aPropObj, HttpServletRequest req,
	HttpServletResponse res) throws ServletException, IOException {

	  //	Properties params = this.getParameters(req) ;
		if(checkParameters(aPropObj) == false) {
			String header = "Checkparameters error" ;
			String msg = "Samtliga fält var inte korrekt ifyllda." + "<BR>";
			this.log("Error in checkingparameters") ;
			AdminError err = new AdminError(req,res,header, msg) ;
			return null;
		}
		return aPropObj ;

} // end checkParameters


}