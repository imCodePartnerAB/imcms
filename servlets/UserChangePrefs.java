import java.io.* ;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.* ;

import imcode.external.diverse.* ;
import imcode.server.* ;
import imcode.util.* ;

public class UserChangePrefs extends Administrator {
    private final static String CVS_REV = "$Revision$" ;
    private final static String CVS_DATE = "$Date$" ;

    String LOGIN = "userprefs_login.htm" ;
    String LOGIN_FAILED = "userprefs_login_failed.htm"   ;
    String CHANGE_PREFS = "userprefs_change.htm" ;
    String DONE = "userprefs_done.htm" ;

    /**
       POST
    **/

    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;

	// Lets get an user object
	imcode.server.user.UserDomainObject user = super.getUserObj(req,res) ;
	String userId = null ;

	// Get the session
	HttpSession session = req.getSession(true) ;

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

	    // Lets update the session with a return back to variable which the phonenbr
	    // function will look for
	    if(session != null  ) {
		session.setAttribute("UserChangePrefs.goBack", "1" ) ;
	    }
	    res.sendRedirect("AdminUserPhones?user_id=" + user_id);
	}   // End of editphones


	// ********** SAVE A USER *************
	if( req.getParameter("SAVE_USER") != null ) {
	    // log("Ok, save user parameters") ;

	    userId = (String) session.getAttribute("AdminUser.user_id" ) ;
	    // Lets get the users information from db
	    Properties currUserInfo = getUserInfoDB(imcref, userId) ;

	    // Lets get the parameters from html page and validate them
	    Properties params = this.getParameters(req) ;

	    //log(currUserInfo.toString()) ;

	    // Lets check the password. if its empty, then it wont be updated, (we use
	    // the password from the db instead
	    if(params.getProperty("password1").equals("")) {
		params.setProperty("password1" , currUserInfo.getProperty("login_password")) ;
		params.setProperty("password2" , currUserInfo.getProperty("login_password")) ;
		//log("gick in") ;
	    }
	    //log(params.getProperty("password1")) ;
	    if( AdminUserProps.verifyPassword(params,req,res) == false) {
		return ;
	    }

	    if(checkParameters(params) == false) {
		String header = req.getServletPath() ;
		AdminError2 err = new AdminError2(req,res,header,52) ;
		log(header + err.getErrorMsg()) ;
		return ;
	    }

	    // Lets update the db object with the new info from http form
	    currUserInfo = this.copyProps(params ,currUserInfo) ;

	    currUserInfo.setProperty("password1", params.getProperty("password1") ) ;
	    currUserInfo.setProperty("title", params.getProperty("title")) ;
	    currUserInfo.setProperty("company", params.getProperty("company")) ;

	    // Lets build the users information into a string and add it to db
	    String[] procParams = AdminUserProps.extractUpdateUserSprocParametersFromProperties(currUserInfo) ;
	    imcref.sqlUpdateProcedure( "UpdateUser", procParams ) ;


	    // ****** Default option. Ok, Generate a login page to the user
	    VariableManager vm = new VariableManager() ;
	    vm.addProperty("SERVLET_URL", "" ) ;
	    this.sendHtml(req,res,vm, DONE ) ;
	    return ;
	}


	// ************* Verfiy username and password **************
	if(req.getParameter("authenticate") != null) {
	    String loginName = (req.getParameter("login_name")==null) ? "" : (req.getParameter("login_name")) ;
	    String password = (req.getParameter("password")==null) ? "" : (req.getParameter("password")) ;

	    // Validate loginparams against the DB
	    String sqlQ = "GetUserIdFromName '" + loginName + "', '" + password + "'" ;
	    userId = imcref.sqlProcedureStr( sqlQ ) ;
	    //	log("GetUserIdFromName ok") ;

	    // Lets check that we the found the user. Otherwise send unvailid username password
	    if( userId == null ) {
		String header = req.getServletPath() ;
		AdminError2 err = new AdminError2(req,res,header,50) ;
		log(header + err.getErrorMsg()) ;
		return ;
	    }

	    // Lets check if the users password are correct
	    String currPass = imcref.sqlProcedureStr("GetUserPassword " + userId ) ;
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
	} // end authenticate


	///*********** Lets get the users information ************
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

		String[] userInfo = imcref.sqlProcedure("UserPrefsChange " + userId ) ;
		String[] keys = {"USER_ID","LOGIN_NAME","PWD1","PWD2","FIRST_NAME",
				 "LAST_NAME","TITLE", "COMPANY", "ADDRESS","CITY","ZIP","COUNTRY","COUNTRY_COUNCIL",
				 "EMAIL", "LANG_ID" } ;

		for(int i = 0 ; i<keys.length; i++) {
		    vm.addProperty(keys[i], userInfo[i]);
		}

		// Lets fix all users phone numbers from DB
		String[] phonesArr = imcref.sqlProcedure("GetUserPhones " + userId) ;
		Vector phonesV  = new Vector(java.util.Arrays.asList(phonesArr)) ;
		Html htm = new Html() ;
		String phones = htm.createHtmlCode("ID_OPTION", "", phonesV ) ;
		vm.addProperty("PHONES_MENU", phones  ) ;
		vm.addProperty("CURR_USER_ID", userId  ) ;

		// Lets get the the users language id
		String[] langList = imcref.sqlProcedure("GetLanguageList") ;
		Vector selectedLangV = new Vector() ;
		selectedLangV.add(vm.getProperty("LANG_ID")) ;
		vm.addProperty("LANG_TYPES", htm.createHtmlCode("ID_OPTION",selectedLangV, new Vector(java.util.Arrays.asList(langList)))) ;

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



	// ****** Default option. Ok, Generate a login page to the user
	VariableManager vm = new VariableManager() ;
	vm.addProperty("SERVLET_URL", "" ) ;
	Html ht = new Html() ;
	this.sendHtml(req,res,vm, LOGIN ) ;
	return ;

    } // end HTTP POST

    /**
       Adds the userInformation to the htmlPage. if an empty vector is sent as argument
       then an empty one will be created
    **/
    public VariableManager addUserInfo(VariableManager vm, Vector v) {
	// Here is the order in the vector
	// [3, Rickard, tynne, Rickard, Larsson, Drakarve, Havdhem, 620 11, Sweden, Gotland,
	// rickard@imcode.com, 0, 1001, 0, 1]

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

    /**
       Collects the parameters from the request object
    **/

    public Properties getParameters( HttpServletRequest req) throws ServletException, IOException {

	Properties userInfo = new Properties() ;
	// Lets get the parameters we know we are supposed to get from the request object
	//String login_name = (req.getParameter("login_name")==null) ? "" : (req.getParameter("login_name")) ;
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

	userInfo.setProperty("password1", password1) ;
	userInfo.setProperty("password2", password2) ;
	userInfo.setProperty("first_name", first_name) ;
	userInfo.setProperty("last_name", last_name) ;
	userInfo.setProperty("address", address) ;
	userInfo.setProperty("city", city) ;
	userInfo.setProperty("zip", zip) ;
	userInfo.setProperty("title", title) ;
	userInfo.setProperty("company", company) ;
	userInfo.setProperty("country", country) ;
	userInfo.setProperty("country_council", country_council) ;
	userInfo.setProperty("email", email) ;
	userInfo.setProperty("lang_id", lang_id) ;

	return userInfo ;
    }





    /**
       Lets get the Userparameters
    */

    public Vector getUserParameters(HttpServletRequest req) throws ServletException, IOException {
	// Lets get the users from the multichoicebox
	String[] roles = (req.getParameterValues("AllUsers")==null) ? new String[0] : (req.getParameterValues("AllUsers"));
	return new Vector(java.util.Arrays.asList(roles)) ;
    }


    public void log( String str) {
	super.log(str) ;
	System.out.println("UserChangePrefs: " + str ) ;
    }


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

    /*
      Service. Sends all requests to doPost method
    */
    public void service (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	this.doPost(req,res) ;
    }

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


    /**
       Copies all properties from propertyOne to propertyTwo as long as the property is not "" or null
    **/
    public Properties copyProps(Properties p1, Properties p2) {
	Enumeration enumValues = p1.elements() ;
	Enumeration enumKeys = p1.keys() ;
	while((enumValues.hasMoreElements() && enumKeys.hasMoreElements())) {
	    String aKey = (String) (enumKeys.nextElement()) ;
	    String aValue = (String) (enumValues.nextElement()) ;
	    if( (aValue != null) && ( !aValue.equals("")) ){
		p2.setProperty(aKey, aValue) ;
	    }
	}
	return p2 ;
    } // checkparameters

   /**
    Collects all userparameters from the users table in the db
    Returns null if something goes wrong
    **/
   private static Properties getUserInfoDB( IMCServiceInterface imcref, String userId ) {

      // Get default props
      Properties p = doDefaultUser();
      Hashtable h = imcref.sqlQueryHash( "GetUserInfo " + userId );
      Enumeration keys = h.keys();
      while( keys.hasMoreElements() ) {
         Object key = keys.nextElement();
         String[] values = (String[])h.get( key );
         String aValue = values[0];
         p.setProperty( key.toString(), aValue );
      }

      return p;
   }

   /**
    Creates a properties with all the users properties from the
    users table. All keys are here, but not the values
    */
   private static Properties doDefaultUser() {

      Properties p = new Properties();

      p.setProperty( "user_id", "" );
      p.setProperty( "login_name", "" );
      p.setProperty( "login_password", "" );
      p.setProperty( "first_name", "" );
      p.setProperty( "last_name", "" );
      p.setProperty( "title", "" );
      p.setProperty( "company", "" );
      p.setProperty( "address", "" );
      p.setProperty( "city", "" );
      p.setProperty( "zip", "" );
      p.setProperty( "country", "" );
      p.setProperty( "country_council", "" );
      p.setProperty( "email", "" );

      p.setProperty( "external", "" );
      p.setProperty( "last_page", "" );
      p.setProperty( "archive_mode", "" );
      p.setProperty( "lang_id", "" );

      p.setProperty( "user_type", "" );
      p.setProperty( "active", "" );
      p.setProperty( "create_date", "" );
      return p;

   }  // End of


} // End of class
