/*
 *
 * @(#)Administrator.java
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
import imcode.util.* ;


/**
 * Parent servlet for administration.
 *
 * Html template in use:
 * AdminListDocs.html
 * Error.html
 *
 *
 * stored procedures in use:
 * - GetLangPrefixFromId
 *
 * @version 1.1 27 Oct 2000
 *
*/
public class Administrator extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	private static final String TEMPLATE_ERROR = "Error.html";

	public boolean checkParameters(Properties aPropObj) {
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
	} // checkparameters

	/**
		Returns an user object
	*/

	protected static imcode.server.User getUserObj(HttpServletRequest req,
				HttpServletResponse res) throws ServletException, IOException {

		if(checkSession(req,res) == true) {

			// Get the session
			HttpSession session = req.getSession(true);
		    // Does the session indicate this user already logged in?
		    Object done = session.getAttribute("logon.isDone");  // marker object
		    imcode.server.User user = (imcode.server.User) done ;
		    return user ;
		} else
			return null ;
	}


	/**
		Verifies that the user is logged in
	*/

	 protected static boolean checkSession(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {

		// Get the session
	    HttpSession session = req.getSession(true);
	    // Does the session indicate this user already logged in?
	    Object done = session.getAttribute("logon.isDone");  // marker object
	    imcode.server.User user = (imcode.server.User) done ;

	    if (done == null) {
		// No logon.isDone means he hasn't logged in.

		// Lets get the login page
		String host				= req.getHeader("Host") ;
		// String imcserver			= Utility.getDomainPref("adminserver", host) ;
		String start_url	= Utility.getDomainPref( "start_url",host ) ;

		// Save the request URL as the true target and redirect to the login page.
		session.setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
		String serverName = MetaInfo.getServerName(req) ;
		String startUrl = Utility.getDomainPref( "start_url",host ) ;

		//String startUrl = RmiCon f.getLoginUrl() ;
		res.sendRedirect(serverName + startUrl) ;
		return false;
		}
	    return true ;
	}

	/**
		CheckAdminRights, returns true if the user is an superadmin. Only an superadmin
		is allowed to create new users
		False if the user isn't an administrator.
		1 = administrator
		0 = superadministrator
	*/

	public static boolean checkAdminRights(String server, imcode.server.User user) {

	    // Lets verify that the user who tries to add a new user is an SUPERADMIN
	    RmiLayer imc = new RmiLayer(user) ;
	    int currUser_id = user.getInt("user_id") ;
	    String checkAdminSql = "CheckAdminRights " + currUser_id ;
	    String[] roles = imc.execSqlProcedure(server, checkAdminSql) ;
	    
	    for(int i = 0 ; i< roles.length; i++ ){
		String aRole = roles[i] ;
		if(aRole.equalsIgnoreCase("0") )
		    return true ;
	    }
	    return false ;
	} // checkAdminRights


	/**
		CheckAdminRights, returns true if the user is an admin.
		False if the user isn't an administrator
	*/

	protected static boolean checkAdminRights(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {

		String host				= req.getHeader("Host") ;
		String server			= Utility.getDomainPref("adminserver",host) ;
		imcode.server.User user = getUserObj(req,res) ;
		if(user == null) {
			//this.log("CheckadminRights: an error occured, getUserObj") ;
			return false ;
		} else
			return checkAdminRights(server, user) ;
	}


	/**
		GetAdminTemplateFolder. Takes the userobject as argument to detect the language
	  from the user and and returns the base path to the internal folder, hangs on the
	  language prefix and an "/admin/" string afterwards...

	  Example : D:\apache\htdocs\templates\se\admin\
	*/
	public File getAdminTemplateFolder (String server, imcode.server.User user) throws ServletException, IOException {

		RmiLayer rmi = new RmiLayer(user) ;

		// Since our templates are located into the admin folder, we'll have to hang on admin
	    File templateLib = rmi.getInternalTemplateFolder(server) ;

		// Lets get the users language id. Use the langid to get the lang prefix from db.
	    String langId = user.getString("lang_id") ;
		String langPrefix = rmi.execSqlProcedureStr(server, "GetLangPrefixFromId " + langId) ;
		templateLib = new File(templateLib, langPrefix + "/admin") ;
		 //this.log("lang_id:" + langId) ;
	    //this.log("langPrefix:" + langPrefix) ;
	    //this.log("InternalTemplatePath:" + templateLib) ;
		return templateLib ;
	}



	/**
		SendHtml. Generates the html page to the browser.
	**/

	public String createHtml (HttpServletRequest req, HttpServletResponse res,
		VariableManager vm, String htmlFile) throws ServletException, IOException {

		// Lets get the path to the admin templates folder
		String host				= req.getHeader("Host") ;
		String server			= Utility.getDomainPref("adminserver",host) ;
		imcode.server.User user = getUserObj(req,res) ;
		File templateLib = this.getAdminTemplateFolder(server, user) ;

    /*
    RmiLayer rmi = new RmiLayer(user) ;

		// Since our templates are located into the admin folder, we'll have to hang on admin
		// String templateLib = MetaInfo.getInternalTemplateFolder() ;

    String templateLib = rmi.getInternalTemplateFolder(server) ;

		// Lets get the users language id. Use the langid to get the lang prefix from db.
		String langId = user.getString("lang_id") ;
		String langPrefix = rmi.execSQlProcedureStr(server, "GetLangPrefixFromId " + langId) ;
		templateLib += langPrefix + "/admin/" ;
	 // this.log("InternalTemplatePath:" + templateLib) ;
	*/

		// Lets add the server host
		String servletHome = MetaInfo.getServletHost(req) ;
		vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req))  ;
		vm.addProperty("SERVLET_URL2", MetaInfo.getServletPath(req))  ;

		HtmlGenerator htmlObj = new HtmlGenerator(templateLib, htmlFile) ;
		String html = htmlObj.createHtmlString(vm,req) ;
	return html ;
	}

	/**
	  SendHtml. Generates the html page to the browser.
	*/

	  public void sendHtml (HttpServletRequest req, HttpServletResponse res,
	    VariableManager vm, String htmlFile) throws ServletException, IOException {

	    String str = this.createHtml(req, res, vm, htmlFile) ;
	    HtmlGenerator htmlObj = new HtmlGenerator() ;
	    htmlObj.sendToBrowser(req,res,str) ;
	  }

	/**
		Log function. Logs the message to the log file and console
	*/

		public void log(String msg) {
			super.log(msg) ;
			System.out.println("Administrator: " + msg) ;
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
		Convert array to vector
		Does exactly the same as new Vector(java.util.Arrays.toList(arr)), but is less generic.
	*/

    public Vector convert2Vector(String[] arr) {
		Vector rolesV  = new Vector() ;
		for(int i = 0; i<arr.length; i++)
			rolesV.add(arr[i]) ;
		return rolesV ;
	}

    /**

       Does exactly the same as new Vector(java.util.Arrays.toList(multi[row])), but is less generic.

     **/

	public Vector getOneRow( String[][] multi, int row) {
		Vector v = new Vector() ;
		try {
			String[] theRow = multi[row] ;
			for(int i = 0 ; i < theRow.length ; i++ ) {
				v.add(theRow[i]) ;
			}
			return v ;
	} catch(Exception e) {
		return v ;
	}
	} // getOneRow

	/**
	  Returns the nbr of rows in the multiarray
	**/
	public int getNbrOfRows( String[][] multi ) {
	try {
		return multi.length ;
	} catch(Exception e) {
		return 0 ;
	}
	} // getNbrOfRows

	/**
	 * send error message
	 *
	 * @param server
	 * @param eMailServerMaster
	 * @param languagePrefix
	 * @param errorHeader
	 * @param errorCode is the code to loock upp in ErrMsg.ini file
	*/
	protected void sendErrorMessage( String imcserver, String eMailServerMaster,
				       String languagePrefix, String errorHeader,
				       int errorCode, HttpServletResponse response ) throws IOException {

		ErrorMessageGenerator errroMessage = new ErrorMessageGenerator( imcserver, eMailServerMaster,
				languagePrefix,	errorHeader, this.TEMPLATE_ERROR, errorCode );

		errroMessage.sendHtml( response );
	}

	/**
	 * get users language
	 *
	 * @param server
	 * @param langId from userObject
	*/
	protected String getLanguagePrefix( String server, int langId ) throws IOException {

		String sqlQ = "GetLangPrefixFromId " + String.valueOf( langId );
		String languagePrefix = IMCServiceRMI.sqlProcedureStr( server, sqlQ );
		return languagePrefix;
	}


} // End class
