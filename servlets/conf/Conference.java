/*
 *
 * @(#)Conference.java
 *
 *
 *
 * Copyright (c)
 *
*/

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import imcode.util.*;
import imcode.util.IMCServiceRMI;
import imcode.util.Parser;

/**
 * superclas for conference servlets.
 *
 * Html template in use:
 * Conf_Admin_Button.htm
 *
 * Html parstags in use:
 * #ADMIN_TYPE#
 * #TARGET#
 *
 * stored procedures in use:
 * -
 *
 * @version 1.5 20 Nov 2000
 * @author Rickard Larsson, Jerker Drottenmyr
 *
*/


public class Conference extends HttpServlet {

	private final static String ADMIN_BUTTON_TEMPLATE = "Conf_Admin_Button.htm";
	private final static String UNADMIN_BUTTON_TEMPLATE = "Conf_Unadmin_Button.htm";

	/**
	Returns the metaId from a request object, if not found, we will
	get the one from our session object. If still not found then null is returned.
	*/

	public String getMetaId (HttpServletRequest req)
	throws ServletException, IOException {

		String metaId = req.getParameter("meta_id") ;
		if( metaId == null ) {
			HttpSession session = req.getSession(false) ;
			if (session != null) {
				metaId =	(String) session.getValue("Conference.meta_id") ;
			}
		}
		if( metaId == null) {
			log("No meta_id could be found! Error in Conference.class") ;
			return null ;
		}
		return metaId ;
	}

	/**
	Returns the ForumId from a request object, if not found, we will
	get the one from our session object. If still not found then null is returned.
	*/

	public String getForumId (HttpServletRequest req)
	throws ServletException, IOException {

		String forumId = req.getParameter("forum_id") ;
		if( forumId == null ) {
			HttpSession session = req.getSession(false) ;
			if (session != null) {
				forumId =	(String) session.getValue("Conference.forum_id") ;
			}
		}
		if( forumId == null) {
			log("No forum_id could be found! Error in Conference.class") ;
			return null ;
		}
		return forumId ;
	}

	/**
	Collects all information from the user object. To get information from
	Janus's userobject.
	* userObject.getString(String theKey)
	* userObject.getInt(String theKey)
	* userObject.getBoolean(String theKey)

	**/

	public Properties getUserParameters(imcode.server.User user) {
		Properties userParams= new Properties() ;
		userParams.setProperty("USER_ID", user.getString("user_id")) ;
		userParams.setProperty("LOGIN_NAME", user.getString("login_name")) ;
		userParams.setProperty("LOGIN_PASSWORD", user.getString("login_password")) ;
		userParams.setProperty("FIRST_NAME", user.getString("first_name")) ;
		userParams.setProperty("LAST_NAME", user.getString("last_name")) ;
		userParams.setProperty("ADDRESS", user.getString("address")) ;
		userParams.setProperty("CITY", user.getString("city")) ;
		userParams.setProperty("ZIP", user.getString("zip")) ;
		userParams.setProperty("COUNTRY", user.getString("country")) ;
		userParams.setProperty("COUNTY_COUNCIL", user.getString("county_council")) ;
		userParams.setProperty("EMAIL", user.getString("email")) ;
		userParams.setProperty("ADMIN_MODE", user.getString("admin_mode")) ;
		userParams.setProperty("LAST_PAGE", user.getString("last_page")) ;
		userParams.setProperty("ARCHIVE_MODE", user.getString("archive_mode")) ;
		userParams.setProperty("USER_TYPE", user.getString("user_type")) ;
		userParams.setProperty("LOGIN_TYPE", user.getLoginType()) ;

		//userParams.setProperty("LANG_ID", user.getString("lang_id")) ;

		// log("GetUserParameters: " + userParams.toString()) ;
		return userParams ;
	}


	/**
	Returns an user object. If an error occurs, an errorpage will be generated.
	*/

	protected imcode.server.User getUserObj(HttpServletRequest req,
		HttpServletResponse res) throws ServletException, IOException {

		if(checkSession(req,res) == true) {

			// Get the session
			HttpSession session = req.getSession(true);
			// Does the session indicate this user already logged in?
			Object done = session.getValue("logon.isDone");  // marker object
			imcode.server.User user = (imcode.server.User) done ;

			return user ;
		}
		else {
			String header = "Conference servlet." ;
			ConfError err = new ConfError(req,res,header, 2) ;
			log(err.getErrorMsg()) ;
			return null ;
		}
	}

	// *************** LETS HANDLE THE SESSION META PARAMETERS *********************


	/**
	Collects the standard parameters from the session object
	**/

	public Properties getSessionParameters( HttpServletRequest req)
	throws ServletException, IOException {


		// Get the session
		HttpSession session = req.getSession(true);
		String metaId = (	(String) session.getValue("Conference.meta_id")==null) ? "" : ((String) session.getValue("Conference.meta_id")) ;
		String parentId = (	(String) session.getValue("Conference.parent_meta_id")==null) ? "" : ((String) session.getValue("Conference.parent_meta_id")) ;
		String cookieId = (	(String) session.getValue("Conference.cookie_id")==null) ? "" : ((String) session.getValue("Conference.cookie_id")) ;

		Properties reqParams= new Properties() ;
		reqParams.setProperty("META_ID", metaId) ;
		reqParams.setProperty("PARENT_META_ID", parentId) ;
		reqParams.setProperty("COOKIE_ID", cookieId) ;

		return reqParams ;
	}


	/**
	Collects the EXTENDED parameters from the session object. As extended paramters are we
	counting:

	Conference.forum_id
	Conference.discussion_id

	@Parameter: Properties params, if a properties object is passed, we will fill the
	object with the extended paramters, otherwise we will create one.
	**/

	public Properties getExtSessionParameters( HttpServletRequest req, Properties params)
	throws ServletException, IOException {

		// Get the session
		HttpSession session = req.getSession(true);
		String forumId = (	(String) session.getValue("Conference.forum_id")==null) ? "" : ((String) session.getValue("Conference.forum_id")) ;
		String discId = (	(String) session.getValue("Conference.disc_id")==null) ? "" : ((String) session.getValue("Conference.disc_id")) ;

		if( params == null)
			params = new Properties() ;
		params.setProperty("FORUM_ID", forumId) ;
		params.setProperty("DISC_ID", discId) ;
		return params ;
	}



	/**
	Verifies that the user has logged in. If he hasnt, he will be redirected to
	an url which we get from a init file name conference.
	*/

	protected boolean checkSession(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		// Get the session
		HttpSession session = req.getSession(true);
		// Does the session indicate this user already logged in?
		Object done = session.getValue("logon.isDone");  // marker object
		imcode.server.User user = (imcode.server.User) done ;

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String ConfPoolServer = Utility.getDomainPref("conference_server",host) ;

		if (done == null) {
			// No logon.isDone means he hasn't logged in.
			// Save the request URL as the true target and redirect to the login page.
			session.putValue("login.target", HttpUtils.getRequestURL(req).toString());
			String serverName = MetaInfo.getServerName(req) ;
			String startUrl = RmiConf.getLoginUrl(host) ;
			res.sendRedirect(serverName + startUrl) ;

			//this.log("Server: " + serverName) ;
			//this.log("startUrl: " + startUrl) ;
			//this.log("A user had not logged in. He was sent to " + serverName + startUrl) ;
			return false;
		}
		return true ;
	}

	// *************** LETS HANDLE THE STANDARD META PARAMETERS *********************


	/**
	Collects the parameters from the request object
	**/

	public Properties getParameters( HttpServletRequest req)
	throws ServletException, IOException {

		MetaInfo mInfo = new MetaInfo() ;
		return mInfo.getParameters(req) ;
	}

	/**
	check the meta Parameters
	*/

	public boolean checkParameters(HttpServletRequest req,HttpServletResponse res)
	throws ServletException, IOException {
		MetaInfo mInfo = new MetaInfo() ;
		Properties params = mInfo.getParameters(req) ;
		if( mInfo.checkParameters(params) == false) {
			String header = "Conference servlet." ;
			String msg = params.toString() ;
			ConfError err = new ConfError(req, res, header, msg, 1) ;
			return false;
		}
		return true ;
	}

	public boolean checkParameters(HttpServletRequest req,HttpServletResponse res,
		Properties params) throws ServletException, IOException {

		MetaInfo mInfo = new MetaInfo() ;
		if( mInfo.checkParameters(params) == false) {
			String header = "Conference servlet." ;
			String msg = params.toString() ;
			ConfError err = new ConfError(req, res, header, msg, 1) ;
			log(err.getErrorString()) ;
			return false;
		}
		return true ;
	}

	// *************************** END OF META PARAMETER FUNCTIONS *****************


	// *************************** ADMIN RIGHTS FUNCTIONS **************************

	protected boolean getAdminRights(String server, String metaId, imcode.server.User user) {

		/* Rickards old style
		try {
			// Lets verify if the user has admin rights for the metaid
			RmiConf rmi = new RmiConf(user) ;
			return rmi.checkAdminRights( server, metaId , user ) ;
		} catch	(Exception e) {
			log("GetAdminRights failed!!!") ;
			return false ;

		}
		*/
		try {
			return userHasAdminRights( server, Integer.parseInt( metaId ), user );
		} catch ( IOException e ) {
			log("GetAdminRights failed!!!") ;
			return false ;
		}

	} // End GetAdminRights

	/**
	CheckAdminRights, returns true if the user is an superadmin. Only an superadmin
	is allowed to create new users
	False if the user isn't an administrator.
	1 = administrator
	0 = superadministrator
	*/

	protected boolean checkAdminRights(String server, imcode.server.User user) {


		try {

			// Lets verify that the user who tries to add a new user is an SUPERADMIN
			RmiLayer imc = new RmiLayer(user) ;
			int currUser_id = user.getInt("user_id") ;
			String checkAdminSql = "CheckAdminRights " + currUser_id ;
			String[] roles = imc.execSqlProcedure(server, checkAdminSql) ;

			for(int i = 0 ; i< roles.length; i++ ) {
				String aRole = roles[i] ;
				if(aRole.equalsIgnoreCase("0") )
					return true ;
			}
			return false ;
		} catch (ServletException e) {
			this.log("An error occured in CheckAdminRights") ;
			this.log(e.getMessage() ) ;
		}
		return false ;

	} // checkAdminRights

	/**
	CheckAdminRights, returns true if the user is an admin.
	False if the user isn't an administrator
	*/

	protected boolean checkAdminRights(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String ConfPoolServer = Utility.getDomainPref("conference_server",host) ;


		imcode.server.User user = getUserObj(req,res) ;
		if(user == null) {
			this.log("CheckadminRights: an error occured, getUserObj") ;
			return false ;
		}
		else
			return checkAdminRights(imcServer, user) ;
	}

	/**
	CheckAdminRights, returns true if the user is an admin.
	False if the user isn't an administrator
	*/

	protected boolean checkDocRights(String server, String meta_id, imcode.server.User user)
	throws ServletException, IOException {
		return RmiConf.checkDocRights(server, meta_id, user) ;
	}

	// *********************** GETEXTERNAL TEMPLATE FUNCTIONS *********************

	/**
	Gives the folder to the root external folder,Example /templates/se/102/
	*/

	public String getExternalTemplateRootFolder (HttpServletRequest req)
	throws ServletException, IOException {

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String ConfPoolServer = Utility.getDomainPref("conference_server",host) ;

		String externalTemplateLib = "" ;
		String metaId = this.getMetaId(req) ;
		if( metaId == null) {
			log("No meta_id could be found! Error in Conference.class") ;
			return "No meta_id could be found!" ;
		}
		externalTemplateLib = MetaInfo.getExternalTemplateFolder(imcServer, metaId) ;
		return externalTemplateLib ;
	}


	/**
	Gives the folder where All the html templates for a language are located.
	This method will call its helper method getTemplateLibName to get the
	name of the folder which contains the templates for a certain meta id
	*/

	public String getExternalTemplateFolder (HttpServletRequest req)
	throws ServletException, IOException {

		String externalTemplateLib = "" ;
		String metaId = this.getMetaId(req) ;
		if( metaId == null) {
			log("No meta_id could be found! Error in Conference.class") ;
			return "No meta_id could be found!" ;
		}
		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("conference_server",host) ;
		String extFolder = this.getExternalTemplateFolder(imcServer, metaId) ;
		return extFolder += this.getTemplateLibName(confPoolServer, metaId) ;
		// return this.getExternalTemplateFolder(imcServer, metaId) ;
	}

	/**
	Gives the folder where All the html templates for a language are located.
	This method will call its helper method getTemplateLibName to get the
	name of the folder which contains the templates for a certain meta id
	*/

	public String getExternalTemplateFolder (String server, String metaId )
	throws ServletException, IOException {

		String externalTemplateLib = "" ;
		if( metaId == null) {
			log("No meta_id could be found! Error in Conference.class") ;
			return "No meta_id could be found!" ;
		}
		externalTemplateLib = MetaInfo.getExternalTemplateFolder(server, metaId) ;
		if(externalTemplateLib == null)
			log("Error!: getExternalTemplateFolder: " + externalTemplateLib) ;
		//externalTemplateLib += this.getTemplateLibName(server, metaId) ;
		// log("ExternalTemplateLib: " + externalTemplateLib) ;
		//log("*** GetExternalTemplateFolder WAS CALLED" ) ;
		return externalTemplateLib ;
	}


	/**
	Returns the foldername where the templates are situated for a certain metaid.
	**/
	protected String getTemplateLibName(String server, String meta_id)
	throws ServletException, IOException {
		// RmiConf aRmiObj = new RmiConf() ;
		String sqlQ = "GetTemplateLib " + meta_id ;
		String libName = RmiConf.execSqlProcedureStr(server, sqlQ) ;
		if( libName == null) {
			libName = "Original" ;
			//log(sqlQ + ": fungerar inte!") ;
		}
		libName += "/" ;
		return libName  ;

	} // End of getTemplateLibName




	/**
	Collects the parameters from the request object. This function will get all the possible
	parameters this servlet will be able to get. If a parameter wont be found, the session
	parameter will be used instead, or if no such parameter exist in the session object,
	a key with no value = "" will be used instead.
	Since this method is used. it means
	that this servlet will take more arguments than the standard ones.
	**/

	public Properties getRequestParameters( HttpServletRequest req)
	throws ServletException, IOException {

		Properties reqParams = new Properties() ;

		// Lets get our own variables. We will first look for the discussion_id
		//	 in the request object, if not found, we will get the one from our session object
		String confForumId = req.getParameter("forum_id") ;
		// log("Nytt ForumId är: " + confForumId) ;
		if( confForumId == null ) {
			HttpSession session = req.getSession(false) ;
			if (session != null) {
				confForumId =	(String) session.getValue("Conference.forum_id") ;
			}
		}
		reqParams.setProperty("FORUM_ID", confForumId) ;
		return reqParams ;
	}



	//************************ END GETEXTERNAL TEMPLATE FUNCTIONS ***************

	/**
	SendHtml. Generates the html page to the browser. Uses the templatefolder
	by taking the metaid from the request object to determind the templatefolder.
	Will by default handle maximum 3 servletadresses.
	*/

	public void sendHtml (HttpServletRequest req, HttpServletResponse res,
		VariableManager vm, String htmlFile) throws ServletException, IOException {

		imcode.server.User user = getUserObj(req,res) ;
		// RmiConf rmi = new RmiConf(user) ;
		String metaId = this.getMetaId(req) ;
		if (metaId == null) {
			log("NO metaid could be found in the passed request object") ;
			String header = "Conference servlet. " ;
			ConfError err = new ConfError(req,res,header,5) ;
			return ;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("conference_server",host) ;

		// This is the old version, which was ok before the double server installation
		// Lets get the TemplateFolder  and the foldername used for this certain metaid
		//	String templateLib = this.getExternalTemplateFolder(imcServer, metaId) ;
		// log("TemplateLib: " + templateLib) ;
		// Lets get the path to the imagefolder.
		//	String imagePath = servletPath + this.getExternalImageFolder(imcServer, metaId) ;

		// Lets get the TemplateFolder  and the foldername used for this certain metaid
		String templateLib = this.getExternalTemplateFolder(req) ;
		//String templateLib = this.getExternalTemplateFolder(imcServer, metaId) ;
		//log("TemplateLib: " + templateLib) ;

		// Lets add 3 server hostadresses
		String servletPath = MetaInfo.getServletPath(req) ;

		// Lets get the path to the imagefolder.
		String imagePath = this.getExternalImageFolder(req) ;
		//log("ImagePath: " + imagePath) ;

		VariableManager adminButtonVM = new VariableManager();
		adminButtonVM.addProperty( "IMAGE_URL", imagePath);
		adminButtonVM.addProperty( "SERVLET_URL", servletPath);
		adminButtonVM.addProperty( "ADMIN_LINK_HTML", vm.getProperty( "ADMIN_LINK_HTML" ) );

                //log("vm: " + vm.toString()) ;
                VariableManager unAdminButtonVM = new VariableManager();
		unAdminButtonVM.addProperty( "IMAGE_URL", imagePath);
		unAdminButtonVM.addProperty( "SERVLET_URL", servletPath);
		unAdminButtonVM.addProperty( "UNADMIN_LINK_HTML", vm.getProperty("UNADMIN_LINK_HTML"));

		vm.addProperty("IMAGE_URL", imagePath);
		vm.addProperty("SERVLET_URL", servletPath);

		//String adminBtn = this.getAdminButtonLink( req, user, adminButtonVM ) ;
		String adminBtn = this.getAdminButtonLink( req, user, adminButtonVM ) ;
		vm.addProperty("CONF_ADMIN_LINK", adminBtn);

		// log("before UNadminBUttonlink: " + imagePath) ;
		String unAdminBtn = this.getUnAdminButtonLink(req, user, unAdminButtonVM) ;
		vm.addProperty("CONF_UNADMIN_LINK", unAdminBtn);

		// log("Before HTmlgenerator: ") ;
		HtmlGenerator htmlObj = new HtmlGenerator(templateLib, htmlFile) ;
		String html = htmlObj.createHtmlString(vm,req) ;
		//log("Before sendToBrowser: ") ;

		htmlObj.sendToBrowser(req,res,html) ;
		//log("after sendToBrowser: ") ;

	}

	/**
	Log function. Logs the message to the log file and console
	*/

	public void log(String msg) {
		super.log(msg) ;
		System.out.println("Conference: " + msg) ;

	}

	/**
	Date function. Returns the current date and time in the swedish style
	*/


	public static String getDateToday() {
		java.util.Calendar cal = java.util.Calendar.getInstance() ;

		String year  = Integer.toString(cal.get(Calendar.YEAR)) ;
		int month = Integer.parseInt(Integer.toString(cal.get(Calendar.MONTH))) + 1;
		int day   = Integer.parseInt(Integer.toString(cal.get(Calendar.DAY_OF_MONTH))) ;
		int hour  = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR_OF_DAY))) ;
		int min   = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;

		String dateToDay  = year ;
		dateToDay += "-" ;
		dateToDay += month < 10 ? "0" + Integer.toString(month) : Integer.toString(month) ;
		dateToDay += "-" ;
		dateToDay += day < 10 ? "0" + Integer.toString(day) : Integer.toString(day) ;

		return dateToDay ;
	}

	/**
	Date function. Returns the current time in the swedish style
	*/

	public static String getTimeNow() {
		java.util.Calendar cal = java.util.Calendar.getInstance() ;

		int hour  = Integer.parseInt(Integer.toString(cal.get(Calendar.HOUR_OF_DAY))) ;
		int min   = Integer.parseInt(Integer.toString(cal.get(Calendar.MINUTE))) ;
		int sec   = Integer.parseInt(Integer.toString(cal.get(Calendar.SECOND))) ;

		String timeNow  = "" ;
		timeNow += hour < 10 ? "0" + Integer.toString(hour) : Integer.toString(hour) ;
		timeNow += ":" ;
		timeNow += min < 10 ? "0" + Integer.toString(min) : Integer.toString(min) ;
		timeNow += ":" ;
		timeNow += sec < 10 ? "0" + Integer.toString(sec) : Integer.toString(sec) ;
		// timeNow += ".000" ;

		return timeNow ;
	}


	/**
	Converts array to vector
	*/

	public Vector convert2Vector(String[] arr) {
		Vector rolesV  = new Vector() ;
		for(int i = 0; i<arr.length; i++)
			rolesV.add(arr[i]) ;
		return rolesV ;
	}

	/**
	Creates Sql characters. Encapsulates a string with ' ' signs. And surrounding
	Space.
	Example. this.sqlChar("myString")  --> " 'myString' "
	*/

	public static String sqlChar(String s) {
		return " '" + s + "' " ;
	}

	/**
	Creates Sql characters. Encapsulates a string with ' ' signs. + an comma.
	And surrounding space.
	Example. this.sqlP("myString")  --> " 'myString', "
	*/

	public static String sqlPDelim(String s) {
		return " '" + s + "', " ;
	}

	/**
	Creates Sql characters. Encapsulates a string with ' ' signs. And surrounding
	Space.
	Example. this.sqlP("myString")  --> " 'myString' "
	*/

	public static String sqlP(String s) {
		return " '" + s + "' " ;
	}


	/**
	Adds a delimiter to the end of the string.
	Example. this.sqlDelim("myString")  --> "myString, "
	*/

	public static String sqlDelim(String s) {
		return s + ", " ;
	}


	/**
	Prepare user for the conference
	**/

	public boolean prepareUserForConf(HttpServletRequest req, HttpServletResponse res,
		Properties params, String loginUserId)  throws ServletException, IOException {

		// Lets get the user object
		imcode.server.User user = this.getUserObj(req,res) ;
		if(user == null) return false ;

		// Lets get userparameters
		Properties userParams = this.getUserParameters(user) ;
		String metaId = params.getProperty("META_ID") ;
		RmiConf rmi = new RmiConf(user) ;

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String ConfPoolServer = Utility.getDomainPref("conference_server",host) ;

		// Ok, Lets prepare the user for the conference.
		// Lets get his lastLoginDate and update it to today
		String sqlStoredProc = "GetLastLoginDate2 " + metaId + ", " + loginUserId ;
		String lastLoginDate = rmi.execSqlProcedureStr(ConfPoolServer, sqlStoredProc) ;
		String firstName = "" ;
		String lastName = "" ;

		// Ok, if lastlogindate is null, then it has to be the a user who has logged in
		// to the system and comes here to the conference for the first time
		// Lets add the user to conference db.
		if(lastLoginDate == null) {
			log("Ok, det är första gången användaren är här.") ;

			firstName = userParams.getProperty("FIRST_NAME") ;
			lastName = userParams.getProperty("LAST_NAME") ;
			String firstTimeInConfSql = "ConfUsersAdd " + loginUserId + ", " + metaId + ", '";
			firstTimeInConfSql += firstName + "', '" + lastName + "'" ;

			log("AddExistingUserToConf: " + firstTimeInConfSql) ;
			rmi.execSqlUpdateQuery(ConfPoolServer, firstTimeInConfSql) ;

			// Ok, try to get the lastLoginDate now and validate it
			log("Ok, nu försöker vi hämta lastlogindate igen") ;
			lastLoginDate	= rmi.execSqlProcedureStr(ConfPoolServer, sqlStoredProc) ;

			if(lastLoginDate == null) {
				String header = "ConfManager servlet. " ;
				ConfError err = new ConfError(req,res,header,30) ;
				log(header + err.getErrorMsg()) ;
				return false;
			}	// End lastLoginCheck
			// Exta add 2000-09-14, Lets set the lastlogindate in the users
			// object to an old date so all discussions will have a new flag
			// so all flags will be shown
			lastLoginDate = "1997-01-01 00:00" ;
			// log("Nytt last login date:" + lastLoginDate) ;

		}
		else {
			// Ok, the user has logged in to the conference by the loginpage
			// for the conference, he has a logindate. Lets get his names
			// Lets get the users first and last names
			String sqlName = "GetConfLoginNames " + metaId +", "+ loginUserId +", "+  1 ;
			firstName = (String) rmi.execSqlProcedureStr(ConfPoolServer, sqlName ) ;
			sqlName = "GetConfLoginNames " + metaId +", "+ loginUserId +", "+  2 ;
			lastName = (String) rmi.execSqlProcedureStr(ConfPoolServer, sqlName ) ;
		} // end else

		// Lets update his logindate and usernames
		String sqlQuest = "ConfUsersUpdate " + metaId +", "+ loginUserId +", '" ;
		sqlQuest += firstName + "', '" + lastName + "'" ;
		// log("Update users info in db: " + sqlQuest) ;
		rmi.execSqlUpdateQuery(ConfPoolServer, sqlQuest) ;
		// Lets store some values in his session object
		HttpSession session = req.getSession(false) ;
		if (session != null) {
			session.putValue("Conference.meta_id", params.getProperty("META_ID")) ;
			session.putValue("Conference.parent_meta_id", params.getProperty("PARENT_META_ID")) ;
			session.putValue("Conference.cookie_id", params.getProperty("COOKIE_ID")) ;
			session.putValue("Conference.viewedDiscList", new Properties()) ;
			session.putValue("Conference.last_login_date", lastLoginDate);
			session.putValue("Conference.user_id", loginUserId) ;
			session.putValue("Conference.disc_index", "0");


			// Ok, we need to catch a forum_id. Lets get the first one for this meta_id.
			// if not a forumid exists, the sp will return -1
			rmi = new RmiConf(user) ;
			String aForumId = rmi.execSqlProcedureStr(ConfPoolServer, "GetFirstForum " + params.getProperty("META_ID")) ;
			session.putValue("Conference.forum_id", aForumId) ;

			// Ok, Lets get the last discussion in that forum
			String aDiscId = rmi.execSqlProcedureStr(ConfPoolServer, "GetLastDiscussionId " +
				params.getProperty("META_ID") + ", " + aForumId) ;

			// Lets get the lastdiscussionid for that forum
			// if not a aDiscId exists, then the  sp will return -1
			session.putValue("Conference.disc_id", aDiscId) ;

			String url = MetaInfo.getServletPath(req) ;
			url += "ConfViewer" ;
			// this.log("Redirects to:" + url) ;
			res.sendRedirect(url) ;
			return true;
		}
		return false ;

	} // End prepare user for conference

	// ****************** GetImageFolder Functions *********************

	/**
	Gives the folder where All the html templates for a language are located.
	This method will call its helper method getTemplateLibName to get the
	name of the folder which contains the templates for a certain meta id
	*/

	public String getExternalImageFolder (HttpServletRequest req) throws ServletException, IOException {
		String metaId = this.getMetaId(req) ;
		if( metaId == null) {
			log("No meta_id could be found! Error in Conference.class") ;
			return "No meta_id could be found!" ;
		}

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String confPoolServer = Utility.getDomainPref("conference_server",host) ;

		String extFolder = this.getExternalImageFolder(imcServer, metaId) ;
		return extFolder += this.getTemplateLibName(confPoolServer, metaId) ;
	}


	/**
	Returns the folder where this templates are situated for a certain metaid.
	**/
	protected String getExternalImageFolder(String server, String meta_id) throws ServletException, IOException {
		RmiConf rmi = new RmiConf() ;
		// Ok, Lets get the language for the system
		String imageLib = rmi.getExternalImageFolder(server, meta_id) ;

		// Lets get the foldername used for this meta id . Default is original
		//imageLib += this.getTemplateLibName(server, meta_id )  ;
		//log("ImageLib: " + imageLib) ;
		return imageLib ;
	} // End of getImageLibName


	/**
	Returns the foldername where the templates are situated for a certain metaid.
	**/
	protected String getImageLibName(String server, String meta_id ) throws ServletException, IOException {
		String sqlQ = "GetTemplateLib " + meta_id ;
		String libName = "" + RmiConf.execSqlProcedureStr(server, sqlQ) + "/";
		return libName ;

	} // End of getImageLibName

	/**
	Returns the foldername where the templates are situated for a certain metaid.
	**/
	protected String getInternalImageFolder(String server)  	 throws ServletException, IOException {
		return RmiConf.getInternalImageFolder(server) ;

	} // End of getInternalImageFolder

	// ***************** RETURNS THE HTML CODE TO THE ADMINIMAGE **************
	/**
	 * Checks whether or not the user is an administrator and
	 * Creates the html code, used to view the adminimage and an appropriate link
	 * to the adminservlet.
	 *
	 * @param reg requestobject
	 * @param user userobject
	 * @param adminButtonTags hashtabele of tags to replace
	 *
	 * @return returns string of html code for adminlink
	*/

	public String getAdminButtonLink(HttpServletRequest req,imcode.server.User user, VariableManager adminButtonVM )
	throws ServletException, IOException {

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String ConfPoolServer = Utility.getDomainPref("conference_server",host) ;

		String adminLink = "&nbsp;";
		String metaId = getMetaId( req );
		int intMetaId = Integer.parseInt( metaId );

		//log("before getAdminRights") ;
		//lets generat adminbutton if user has administrator rights and rights to edit
		if ( userHasAdminRights( imcServer, intMetaId, user ) ) {

			//lets save tags we need later
			VariableManager adminLinkVM = new VariableManager();
			adminLinkVM.addProperty( "SERVLET_URL", adminButtonVM.getProperty( "SERVLET_URL" ) );
			String adminLinkFile = adminButtonVM.getProperty( "ADMIN_LINK_HTML" );

			//lets create adminbuttonhtml
			String templateLib = this.getExternalTemplateFolder( req );
			HtmlGenerator htmlObj = new HtmlGenerator( templateLib, this.ADMIN_BUTTON_TEMPLATE );
                        String adminBtn = htmlObj.createHtmlString( adminButtonVM, req );

			//lets create adminlink
			adminLinkVM.addProperty( "ADMIN_BUTTON", adminBtn );
			HtmlGenerator linkHtmlObj = new HtmlGenerator( templateLib, adminLinkFile );
			adminLink = linkHtmlObj.createHtmlString( adminLinkVM, req );

		}
		//log("After getAdminRights") ;
		return adminLink ;
	} // End CreateAdminHtml

	/**
	Checks whether or not the user is an administrator and
	Creates the html code, used to view the adminimage and an appropriate link
	to the adminservlet.
	*/
	public String getUnAdminButtonLink(HttpServletRequest req,imcode.server.User user, VariableManager unAdminButtonVM )
	throws ServletException, IOException {

		// Lets get serverinformation
		String host = req.getHeader("Host") ;
		String imcServer = Utility.getDomainPref("userserver",host) ;
		String ConfPoolServer = Utility.getDomainPref("conference_server",host) ;

		String unAdminLink = "&nbsp;";
		String metaId = getMetaId(req);
		int intMetaId = Integer.parseInt( metaId );

		//lets generat unadminbutton if user has administrator rights and rights to edit
		if ( userHasAdminRights( imcServer, intMetaId, user ) ) {

			//lets save tags we need later
			VariableManager unAdminLinkVM = new VariableManager();
			unAdminLinkVM.addProperty( "SERVLET_URL", unAdminButtonVM.getProperty( "SERVLET_URL" ) );
			String unAdminLinkFile = unAdminButtonVM.getProperty( "UNADMIN_LINK_HTML" );

			//lets create unadminbuttonhtml
			String templateLib = this.getExternalTemplateFolder( req );
			HtmlGenerator htmlObj = new HtmlGenerator( templateLib, this.UNADMIN_BUTTON_TEMPLATE );
			String unAdminBtn = htmlObj.createHtmlString( unAdminButtonVM, req );

			//lets create unadminlink
			unAdminLinkVM.addProperty( "UNADMIN_BUTTON", unAdminBtn );
			HtmlGenerator linkHtmlObj = new HtmlGenerator( templateLib, unAdminLinkFile );
			unAdminLink = linkHtmlObj.createHtmlString( unAdminLinkVM, req );
		}
		return unAdminLink ;
	} // End CreateAdminHtml

	/**
	Examines a text, and watches for ' signs, which will extended with another ' sign
	*/
	public String verifySqlText(String str ) {
		StringBuffer buf =  new StringBuffer(str) ;
		// log("Innan: " + str) ;
		char apostrof = '\'' ;
		for(int i = 0 ; i < buf.length() ; i++) {
			//log(""+ buf.charAt(i)) ;
			if (buf.charAt(i) == apostrof ) {
				buf.insert(i,apostrof) ;
				i+=1 ;
			}
		}
		str = buf.toString() ;
		// log("Efter: " + str) ;
		return str ;

	} // End CreateAdminHtml


	/**
	Checks for illegal sql parameters.
	**/
	public Properties verifyForSql(Properties aPropObj) {
		// Ok, Lets find all apostrofes and if any,add another one
		Enumeration enumValues = aPropObj.elements() ;
		Enumeration enumKeys = aPropObj.keys() ;
		while((enumValues.hasMoreElements() && enumKeys.hasMoreElements())) {
			Object oKeys = (enumKeys.nextElement()) ;
			Object oValue = (enumValues.nextElement()) ;
			String theVal = oValue.toString() ;
			String theKey = oKeys.toString() ;
			aPropObj.setProperty(theKey, verifySqlText(theVal)) ;
		}
		// log(aPropObj.toString()) ;
		return aPropObj ;
	} // verifyForSql

	/**
	Checks for illegal sql parameters.
	**/
	public String props2String(Properties p) {

		Enumeration enumValues = p.elements() ;
		Enumeration enumKeys = p.keys() ;
		String aLine = "";
		while((enumValues.hasMoreElements() && enumKeys.hasMoreElements())) {
			String oKeys = (String) (enumKeys.nextElement()) ;
			String oValue = (String) (enumValues.nextElement()) ;
			if(oValue == null) {
				oValue = "NULL" ;

			}
			aLine += oKeys.toString() + "=" + oValue.toString() + '\n' ;
		}
		return aLine ;
	}

	/**
	 * checks if user is authorized
	 * @param req
	 * @param res is used if error (send user to conference_starturl )
	 * @param user
	*/
	protected boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, imcode.server.User user )
					 	  throws ServletException, IOException {

		// Lets get serverinformation
		String host = req.getHeader( "Host" ) ;
		String imcServer = Utility.getDomainPref( "userserver", host ) ;

		HttpSession session = req.getSession( true );

		//lets get if user authorized or not
		boolean authorized = true;
		String stringMetaId = (String)session.getValue( "Conference.meta_id" );
		if ( stringMetaId == null ) {
			authorized = false;
			//lets send unauthorized users out
			String serverName = MetaInfo.getServerName(req) ;
			String startUrl = RmiConf.getLoginUrl(host) ;
			res.sendRedirect(serverName + startUrl) ;
		} else {
			int metaId = Integer.parseInt( stringMetaId );
			authorized = isUserAuthorized( req, res, metaId, user );
		}

		return authorized;
	}

	/**
	 * checks if user is authorized
	 * @param req is used for collecting serverinfo and session
	 * @param res is used if error (send user to conference_starturl )
	 * @param metaId conference metaId
	 * @param user
	*/
	protected boolean isUserAuthorized( HttpServletRequest req, HttpServletResponse res, int metaId, imcode.server.User user )
					 	  throws ServletException, IOException {

		// Lets get serverinformation
		String host = req.getHeader( "Host" ) ;
		String imcServer = Utility.getDomainPref( "userserver", host ) ;

		HttpSession session = req.getSession( true );

		//is user authorized?
		boolean authorized = IMCServiceRMI.checkDocRights( imcServer, metaId, user );

		//lets send unauthorized users out
		if ( !authorized ) {
			String serverName = MetaInfo.getServerName(req) ;
			String startUrl = RmiConf.getLoginUrl(host) ;
			res.sendRedirect(serverName + startUrl) ;
		}

		return authorized;
	}

	/**
	 * check if user has right to edit
	 * @param imcServer rmi
	 * @param metaId metaId for conference
	 * @param user
	*/
	protected boolean userHasRightToEdit( String imcServer, int metaId,
	                      imcode.server.User user ) throws java.io.IOException {

		return ( IMCServiceRMI.checkDocRights( imcServer, metaId, user ) &&
			 IMCServiceRMI.checkDocAdminRights( imcServer, metaId, user ) );
	}

	/**
	 * check if user is admin and has rights to edit
	 * @param imcServer rmi
	 * @param metaId metaId for conference
	 * @param user
	*/
	protected boolean userHasAdminRights( String imcServer, int metaId,
	                      imcode.server.User user ) throws java.io.IOException {
		return ( IMCServiceRMI.checkDocAdminRights( imcServer, metaId, user ) &&
		     IMCServiceRMI.checkDocAdminRights( imcServer, metaId, user, 65536 ) );

	}
} // End class
