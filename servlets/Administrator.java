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
import imcode.server.* ;

import org.apache.log4j.* ;

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

    private static Category log = Logger.getInstance( Administrator.class.getName() ) ;

    public boolean checkParameters(Properties aPropObj) {
	// Ok, lets check that the user has typed anything in all the fields
	Enumeration enumValues = aPropObj.elements() ;
	Enumeration enumKeys = aPropObj.keys() ;
	while((enumValues.hasMoreElements() && enumKeys.hasMoreElements())) {
	    Object oKeys = (enumKeys.nextElement()) ;
	    Object oValue = (enumValues.nextElement()) ;
	    String theVal = oValue.toString() ;
	//System.out.println(oKeys.toString() + " = " + theVal);
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
	    IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;

	    // Save the request URL as the true target and redirect to the login page.
	    session.setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
	    String startUrl = imcref.getStartUrl() ;

	    res.sendRedirect(startUrl) ;
	    return false;
	}
	return true ;
    }


    /**
       CheckAdminRights, returns true if the user is an admin.
       False if the user isn't an administrator
    */

    protected static boolean checkAdminRights(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	imcode.server.User user = getUserObj(req,res) ;
	if(user == null) {
	    return false ;
	} else {
	    return imcref.checkAdminRights(user) ;
	}
    }


    /**
       GetAdminTemplateFolder. Takes the userobject as argument to detect the language
       from the user and and returns the base path to the internal folder, hangs on the
       language prefix and an "/admin/" string afterwards...

       Example : D:\apache\htdocs\templates\se\admin\
    */
    public File getAdminTemplateFolder (IMCServiceInterface imcref, imcode.server.User user) throws ServletException, IOException {

	// Since our templates are located into the admin folder, we'll have to hang on admin
	File templateLib = imcref.getInternalTemplateFolder(-1) ;

	// Lets get the users language id. Use the langid to get the lang prefix from db.
	String langPrefix = user.getLangPrefix() ;
	templateLib = new File(templateLib, langPrefix + "/admin") ;
	return templateLib ;
    }


    /**
       SendHtml. Generates the html page to the browser.
    **/

    public String createHtml (HttpServletRequest req, HttpServletResponse res,
			      VariableManager vm, String htmlFile) throws ServletException, IOException {

	// Lets get the path to the admin templates folder
	IMCServiceInterface imcref = IMCServiceRMI.getIMCServiceInterface(req) ;
	imcode.server.User user = getUserObj(req,res) ;
	File templateLib = this.getAdminTemplateFolder(imcref, user) ;


	// Lets add the server host
	vm.addProperty("SERVLET_URL", "")  ;
	vm.addProperty("SERVLET_URL2", "")  ;

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
	log.debug("Administrator: " + msg) ;
    }

    /**
       Date function. Returns the current date and time in the swedish style
       @deprecated Use SimpleDateFormat.format(Date) instead.
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
       @deprecated Use SimpleDateFormat.format(Date) instead.
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

    @deprecated Does exactly the same as new Vector(java.util.Arrays.asList(multi[row])), but is less generic.

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
    protected void sendErrorMessage( IMCServiceInterface imcref, String eMailServerMaster,
				     String languagePrefix, String errorHeader,
				     int errorCode, HttpServletResponse response ) throws IOException {

	ErrorMessageGenerator errroMessage = new ErrorMessageGenerator( imcref, eMailServerMaster,
									languagePrefix,	errorHeader, this.TEMPLATE_ERROR, errorCode );

	errroMessage.sendHtml( response );
    }


} // End class
