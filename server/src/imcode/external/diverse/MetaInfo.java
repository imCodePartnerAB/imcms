package imcode.external.diverse ;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.util.* ;
import imcode.server.* ;

import org.apache.log4j.Category;

public class MetaInfo extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	static Category log = Category.getInstance( "server" ) ;

    private MetaInfo(){

    }


    /**
     * Verifies that the user has logged in. If he hasnt, he will be redirected to
     * /janusweb/login.htm
     */

    public static  boolean checkSession(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {


	// Lets check that we still have a session, well need to pass it later to Janus
	// Get the session
	HttpSession session = req.getSession(true);
	// Does the session indicate this user already logged in?
	Object done = session.getAttribute("logon.isDone");  // marker object
	imcode.server.user.UserDomainObject user = (imcode.server.user.UserDomainObject) done ;

	if (done == null) {
	    // No logon.isDone means he hasn't logged in.
	    // Save the request URL as the true target and redirect to the login page.
	    session.setAttribute("login.target", HttpUtils.getRequestURL(req).toString());
	    String serverName = getServerName(req) ;
        imcode.server.IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
	    String startUrl = imcref.getStartUrl() ;
	    res.sendRedirect(serverName + startUrl);
	    return false ;
	}

	return true ;
    }


    /**
     * Collects the parameters from the request object
     **/

    public static Properties getParameters( HttpServletRequest req)
	throws ServletException, IOException {

	// Lets get the META ID PARAMETERS
	String metaId = (req.getParameter("meta_id")==null) ? "" : (req.getParameter("meta_id")) ;
	String parentId = (req.getParameter("parent_meta_id")==null) ? "" : (req.getParameter("parent_meta_id")) ;
	String cookieId = (req.getParameter("cookie_id")==null) ? "" : (req.getParameter("cookie_id")) ;

	Properties ReqParams= new Properties() ;
	ReqParams.setProperty("META_ID", metaId) ;
	ReqParams.setProperty("PARENT_META_ID", parentId) ;
	ReqParams.setProperty("COOKIE_ID", cookieId) ;

	return ReqParams ;
    }


    /**
     * check the meta Parameters
     */

    public static boolean checkParameters(Properties aPropObj) {

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
    }


    /**
     * Creates a parameterstring with the standard metadata
     **/

    public static String passMeta(Properties params) {
	String args = "" ;
	args += "meta_id=" + params.getProperty("META_ID") + "&"  ;
	args += "parent_meta_id=" + params.getProperty("PARENT_META_ID") + "&" ;
	args += "cookie_id=" + params.getProperty("COOKIE_ID")  ;
	return args ;
    }

    /**
     * Creates a parameterstring with the standard metadata
     **/

    public static String passMeta(HttpServletRequest req,HttpServletResponse res, Properties params)
	throws ServletException, IOException {
	String args = "" ;
	args += "meta_id=" + params.getProperty("META_ID") + "&"  ;
	args += "parent_meta_id=" + params.getProperty("PARENT_META_ID") + "&" ;
	args += "cookie_id=" + params.getProperty("COOKIE_ID")  ;
	return args ;
    }


    /**
     * Creates a parameterstring with the standard metadata
     **/

    public static String passMeta(HttpServletRequest req, Properties params)
	throws ServletException, IOException {
	String args = "" ;
	args += "meta_id=" + params.getProperty("META_ID") + "&"  ;
	args += "parent_meta_id=" + params.getProperty("PARENT_META_ID") + "&" ;
	args += "cookie_id=" + params.getProperty("COOKIE_ID")  ;
	return args ;
    }


    /**
     * Gets the name on the server who hosts the servlets
     * Returnvalue example : http://www.imcode.com
     */

    public static String getServerName(HttpServletRequest req) throws ServletException {

	String scheme = req.getScheme();
	String serverName = req.getServerName();
	int p = req.getServerPort();
	String port = (p == 80) ? "" : ":" + p;
	String url = scheme + "://" + serverName + port ;
	return url ;
    }

    /**
     * Gets the servletname and the path to where the servlets are hosted
     * Returnvalue example : http://www.imcode.com/servlet/KalleServlet
     *
     */

    public static String getServletHost(HttpServletRequest req)
	throws ServletException, IOException {
	String protocol = req.getScheme();
	String serverName = req.getServerName();
	int p = req.getServerPort();
	String port = (p == 80) ? "" : ":" + p;
	String servletPath = req.getServletPath() ;

	String url = protocol + "://" + serverName + port + servletPath ;
	return url ;
    }



    /**
     * Helper class to create a vector with all the character in
     **/

    public static Vector createCharVector() {
	Vector chars = new Vector() ;
	chars.add("A") ;	chars.add("B") ;	chars.add("C") ;
	chars.add("D") ;	chars.add("E") ;	chars.add("F") ;
	chars.add("G") ;	chars.add("H") ;	chars.add("I") ;
	chars.add("J") ;	chars.add("K") ;	chars.add("L") ;
	chars.add("M") ;	chars.add("N") ;	chars.add("O") ;
	chars.add("P") ;	chars.add("Q") ;	chars.add("R") ;
	chars.add("S") ;	chars.add("T") ;	chars.add("U") ;
	chars.add("V") ;	chars.add("W") ;	chars.add("X") ;
	chars.add("Y") ;	chars.add("Z") ;
	return chars ;
    }


    /**
     * Determines the diagramType from the filename. Expects a format like
     * Type of File | diagramType | _ | counterValue | .txt
     * Ex:
     * DIADATA1_333.TXT
     *
     **/

    public static String getDiagramTypeFromFileName(String fileName) {
	String diaType = "" ;


	// Lets check if it is a integer
	try {
	    int beginIndex = fileName.indexOf("_") ;
	    String aNumberStr = fileName.substring(beginIndex - 1, beginIndex) ;
	    diaType = "" + Integer.parseInt(aNumberStr) ;

	} catch( NumberFormatException ignored) {
	    // ignored
	}

	return diaType ;
    }
} // end class
