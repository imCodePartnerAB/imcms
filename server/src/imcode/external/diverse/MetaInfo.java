package imcode.external.diverse ;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.util.* ;
import imcode.server.* ;

public class MetaInfo extends HttpServlet {
    
    public MetaInfo(){
	
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
	Object done = session.getValue("logon.isDone");  // marker object
	imcode.server.User user = (imcode.server.User) done ;
	
	if (done == null) {
	    // No logon.isDone means he hasn't logged in.
	    // Save the request URL as the true target and redirect to the login page.
	    session.putValue("login.target", HttpUtils.getRequestURL(req).toString());
	    String serverName = getServerName(req) ;
	    String startUrl = getStartUrl(req) ;
	    // log("StartUrl: " + serverName + startUrl) ;
	    res.sendRedirect(serverName + startUrl);
	    return false ;
	}
	
	return true ;
    }
    
    
    /**
     * Collects the parameters from the request object
     **/
    
    public Properties getParameters( HttpServletRequest req) 
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
     * Returns the templateFolder.
     **/
    
    public static String getExternalTemplateFolder(String  server, int metaId) throws ServletException, IOException {
	return getExternalTemplateFolder(server, "" + metaId) ;
    }
    
    /**
     * Returns the templateFolder.
     **/
    
    public static String getExternalTemplateFolder( HttpServletRequest req)	throws ServletException, IOException {
	
	String metaId =(req.getParameter("meta_id")==null)?"-1":(req.getParameter("meta_id")) ;
	String host				= req.getHeader("Host") ;
	String server			= imcode.util.Utility.getDomainPref("userserver",host) ;
	return getExternalTemplateFolder(server, metaId) ;
    }
    
    /**
     * Returns the templateFolder.
     **/
    
    public static String getExternalTemplateFolder(String server, String meta_id)
	throws ServletException, IOException  {
	int metaId = -1 ;
	// Lets get the metaId, if no meta_id is found, use -1 instead. Janus System
	// will return the path to the default folder (where Janus templates are located)
	try {
	    metaId = Integer.parseInt(meta_id) ;
	} catch(NumberFormatException  e) {
	    System.out.println("No MetaId could be found. Lets use -1 instead" ) ;
	    metaId = -1 ;
	}
	return RmiLayer.getExternalTemplateFolder(server, metaId) ;
    }
    
    
    /**
     * Returns the templateFolder.
     **/
    
    public static String getInternalTemplateFolder(String server) {
	
	// Lets get the metaId, if no meta_id is found, use -1 instead. Janus System
	// will return the path to the default folder (where Janus templates are located)
	try {
	    return RmiLayer.getInternalTemplateFolder(server, -1) ;
	} catch (Exception e) {
	    return "Error in RmiLayer call" ;
	}
    } // GetInternalTemplateFolder
    
    
    
    /**
     * check the meta Parameters
     */
    
    public boolean checkParameters(Properties aPropObj) {
	//	this.log("Verifying Parameters...");
	
	Enumeration enumValues = aPropObj.elements() ;
	Enumeration enumKeys = aPropObj.keys() ;
	
	while((enumValues.hasMoreElements() && enumKeys.hasMoreElements())) {
	    Object oKeys = (enumKeys.nextElement()) ;
	    Object oValue = (enumValues.nextElement()) ;
	    String theVal = oValue.toString() ;
	    if(theVal.equals(""))
		return false ;
	}
	//		this.log("Verifying Parameters successfully...");
	
	return true ;
    }
    
    
    /**
     * Creates a parameterstring with the standard metadata
     **/
    
    public String passMeta(Properties params) {
	String args = "" ;
	args += "meta_id=" + params.getProperty("META_ID") + "&"  ;
	args += "parent_meta_id=" + params.getProperty("PARENT_META_ID") + "&" ;
	args += "cookie_id=" + params.getProperty("COOKIE_ID")  ;
	return args ;
    }
    
    /**
     * Creates a parameterstring with the standard metadata
     **/
    
    public String passMeta(HttpServletRequest req,HttpServletResponse res, Properties params)
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
    
    public String passMeta(HttpServletRequest req, Properties params)
	throws ServletException, IOException {
	String args = "" ;
	args += "meta_id=" + params.getProperty("META_ID") + "&"  ;
	args += "parent_meta_id=" + params.getProperty("PARENT_META_ID") + "&" ;
	args += "cookie_id=" + params.getProperty("COOKIE_ID")  ;
	return args ;
    }
    
    
    public void log( String str) {
	super.log(str) ;
	System.out.println("MetaInfo: " + str ) ;
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
     * Gets the servletname and the path to where the servlets are hosted
     * Returnvalue example : http://www.imcode.com/servlet/
     *
     * @deprecated Use ServletContext.getContextPath() + ServletContext.getServletPath() instead.
     */
    
    public static String getServletPath(HttpServletRequest req)	throws ServletException, IOException {
	/*
	String protocol = req.getScheme();
	String serverName = req.getServerName();
	int p = req.getServerPort();
	String port = (p == 80) ? "" : ":" + p;
	String servletPath = req.getServletPath() ;
	
	
	int lastSlash = servletPath.lastIndexOf("/") ;
	if( lastSlash != -1 ) {
	    servletPath =  servletPath.substring(0,lastSlash +1) ;
	    String url = protocol + "://" + serverName + port + servletPath ;
	    return url ;
	    }*/
	return "" ;
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
     * Redirect to a new URL
     **/
    public static void reDirect(HttpServletResponse res, String url)
	throws ServletException, IOException {
	res.sendRedirect(url) ;
    }
    
    
    
    /**
     * Returns the starturl of the Janus system, reads from ini file rmi.cfg
     **/
    public static String getStartUrl(HttpServletRequest req) throws IOException {
	try {
	    String host = req.getHeader("Host") ;
	    // log("host: " + host) ;
	    return imcode.util.Utility.getDomainPref("start_url",host) ;
	} catch (IOException e){
	    System.out.println("Could not find property. " + e.getMessage()) ;
	    return "" ;
	}
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
	    
	} catch( NumberFormatException e) {
	    System.out.println("Error in converting string to number. Src=" + fileName) ;
	    System.out.println(e.getMessage()) ;
	    
	}
	
	return diaType ;
    }
} // end class
