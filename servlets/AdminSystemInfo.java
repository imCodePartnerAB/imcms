import java.io.* ;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.* ;
import imcode.external.diverse.* ;
import imcode.util.* ;

public class AdminSystemInfo extends Administrator {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	
    String HTML_TEMPLATE ;	
    String FILE_PATH ;

    /**
       The GET method creates the html page when this side has been
       redirected from somewhere else.
    **/
    public void doGet(HttpServletRequest req, HttpServletResponse res)
  	throws ServletException, IOException {
	
  	String host 				= req.getHeader("Host") ;
  	String server 			= Utility.getDomainPref("adminserver",host) ;
  	
	// Lets validate the session
	if (super.checkSession(req,res) == false)	return ;

	// Lets get an user object 
	imcode.server.User user = super.getUserObj(req,res) ;
  	if(user == null) {
	    String header = "Error in AdminSystemMessage." ;
	    String msg = "Couldnt create an user object."+ "<BR>" ;
	    this.log(header + msg) ;
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
  	}

	// Lets get the system message from db
  	//RmiLayer rmi = new RmiLayer(user) ;

	imcode.server.SystemData sysData = IMCServiceRMI.getSystemData(server) ;
	
	String msg = sysData.getSystemMessage() ;
//    	String msg = rmi.execSqlProcedureStr(server, "SystemMessageGet") ;
//    	if(msg == null) {
//  	    msg = "" ;
//  	    log("No system message was returned from db") ;
//  	}
    
//    	String[] servermaster = rmi.execSqlProcedure(server, "ServerMasterGet") ;
//    	if(servermaster == null || servermaster.length < 2) {
//  	    servermaster = new String[]{ "", "" } ;
//  	    log("No ServerMaster was returned from db") ;
//  	}

//    	String[] webmaster = rmi.execSqlProcedure(server, "WebMasterGet") ;
//    	if(webmaster == null || webmaster.length < 2) {
//  	    webmaster = new String[]{ "", "" } ;
//  	    log("No WebMaster was returned from db") ;
//  	}


	// Lets get the webmaster info from file
//  	String webMaster = webmaster[0] ;
//     	String webMasterEmail = webmaster[1];
//    	String serverMaster = servermaster[0];
//    	String serverMasterEmail = servermaster[1] ;

	String webMaster = sysData.getWebMaster() ;
	String webMasterEmail = sysData.getWebMasterAddress() ;
	String serverMaster = sysData.getServerMaster() ;
	String serverMasterEmail = sysData.getServerMasterAddress() ;

	/* 	// Lets get the properties from the imcserver.cfg file
		String aPathFromFile = "" ;
		try { 
		JanusPrefs prop = new JanusPrefs() ;
  	  	prop.loadConfig("imcserver.cfg") ;
	 		
		// Lets get the servernumber 
 	    	String serverNo = prop.getProperty(server) ;
     		webMaster = prop.getProperty(serverNo +"."+ "web_master") ;
		webMasterEmail = prop.getProperty(serverNo +"."+"web_master_email") ;
		serverMaster = prop.getProperty(serverNo +"."+"server_master") ;
		serverMasterEmail = prop.getProperty(serverNo +"."+"server_master_email") ;
  	
		} catch(Exception e) {
		log("Error in getImageFolder") ;
		//return "Error" ;
		}
	*/

  
	// Lets generate the html page
  	VariableManager vm = new VariableManager() ;
  	vm.addProperty("SYSTEM_MESSAGE", msg) ;
	vm.addProperty("WEB_MASTER", webMaster ) ;
	vm.addProperty("WEB_MASTER_EMAIL", webMasterEmail ) ;
	vm.addProperty("SERVER_MASTER", serverMaster ) ;
	vm.addProperty("SERVER_MASTER_EMAIL", serverMasterEmail ) ;

	// log("HTML_TEMPLATE: " + HTML_TEMPLATE) ;
	// log("SYSTEM_MESSAGE: " + msg) ;
	// log("WEB_MASTER: " + webMaster) ;
	// log("WEB_MASTER_EMAIL: " + webMasterEmail) ;
	// log("SERVER_MASTER: " + serverMaster) ;
	// log("SERVER_MASTER_EMAIL: " + serverMasterEmail) ;


  	this.sendHtml(req,res,vm, HTML_TEMPLATE) ;
    } // End doGet 

    /**
       POST		
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

	// ******* UPDATE THE SYSTEM MESSAGE IN THE DB **********
  
	if( req.getParameter("SetSystemMsg") != null) {
       
	    // Lets get the parameters from html page and validate them
	    String sysMsg = (req.getParameter("SYSTEM_MESSAGE")==null) ? "" : (req.getParameter("SYSTEM_MESSAGE")) ;
   
	    // Lets build the users information into a string and add it to db 
//  	    String sqlStr = "SystemMessageSet '" + sysMsg + "'" ;
//  	    log("SystemMessage Sql: " + sqlStr ) ;
    
	    imcode.server.SystemData sysData = IMCServiceRMI.getSystemData(server) ;
	    sysData.setSystemMessage(sysMsg) ;

	    IMCServiceRMI.setSystemData(server, sysData) ;
	    //RmiLayer rmi = new RmiLayer(user) ;
	    //rmi.execSqlUpdateProcedure(server, sqlStr) ;
    
	    doGet(req, res) ;
	    return ;
	} 
  
	// ******* UPDATE THE SYSTEM SetServerMasterInfo IN THE DB **********
  
	if( req.getParameter("SetServerMasterInfo") != null) {
       
	    // Lets get the parameters from html page and validate them
	    String serverMaster = (req.getParameter("SERVER_MASTER")==null) ? "" : (req.getParameter("SERVER_MASTER")) ;
	    String serverMasterEmail = (req.getParameter("SERVER_MASTER_EMAIL")==null) ? "" : (req.getParameter("SERVER_MASTER_EMAIL")) ;

	    // Lets validate the parameters
	    if(serverMaster.equalsIgnoreCase("") || serverMasterEmail.equalsIgnoreCase("") ) { 
		String header = "Error in AdminSystemInfo." ;
		String msg = "Alla parameterar fanns inte tillgängliga!" + "<BR>" ;
		this.log(header + msg) ;
		AdminError err = new AdminError(req,res,header,msg) ;
		return ;
	    }
 
	    // Lets build the users information into a string and add it to db 
//  	    String sqlStr = "ServerMasterSet '" + serverMaster + "','"+serverMasterEmail+"'" ;
//  	    log("ServerMaster Sql: " + sqlStr ) ;
    
//  	    RmiLayer rmi = new RmiLayer(user) ;
//  	    rmi.execSqlUpdateProcedure(server, sqlStr) ;
	    
	    imcode.server.SystemData sysData = IMCServiceRMI.getSystemData(server) ;
	    sysData.setServerMaster(serverMaster) ;
	    sysData.setServerMasterAddress(serverMasterEmail) ;

	    IMCServiceRMI.setSystemData(server, sysData) ;
 
	    doGet(req, res) ;
	    return ;
	} 
  
	// ******* UPDATE THE SYSTEM WEBMASTER IN THE FILE **********
  
	if( req.getParameter("SetWebMasterInfo") != null) {
       
	    // Lets get the parameters from html page and validate them
	    String webMaster = (req.getParameter("WEB_MASTER")==null) ? "" : (req.getParameter("WEB_MASTER")) ;
	    String webMasterEmail = (req.getParameter("WEB_MASTER_EMAIL")==null) ? "" : (req.getParameter("WEB_MASTER_EMAIL")) ;
 
	    // Lets validate the parameters
	    if(webMaster.equalsIgnoreCase("") || webMasterEmail.equalsIgnoreCase("") ) { 
		String header = "Error in AdminSystemInfo." ;
		String msg = "Alla parameterar fanns inte tillgängliga!" + "<BR>" ;
		this.log(header + msg) ;
		AdminError err = new AdminError(req,res,header,msg) ;
		return ;
	    }
 
	    // Lets build the users information into a string and add it to db 
	    imcode.server.SystemData sysData = IMCServiceRMI.getSystemData(server) ;
	    sysData.setWebMaster(webMaster) ;
	    sysData.setWebMasterAddress(webMasterEmail) ;

	    IMCServiceRMI.setSystemData(server,sysData) ;
	    //String sqlStr = "WebMasterSet '" + webMaster + "','"+webMasterEmail+"'" ;
	    //log("WebMaster Sql: " + sqlStr ) ;
    
	    //RmiLayer rmi = new RmiLayer(user) ;
	    //rmi.execSqlUpdateProcedure(server, sqlStr) ;
 
	    doGet(req, res) ;
	    return ;
	} 
	if( req.getParameter("Cancel") != null) {
	    res.sendRedirect(MetaInfo.getServletPath(req) + "AdminManager") ;
	    return ;
	}
  
    } // end HTTP POST




    public synchronized Vector readFile(File fileObj) {
	try {
	    // Lets update the information into the file
  	
	    FileReader file = new FileReader(fileObj) ;
	    BufferedReader buff = new BufferedReader(file) ;
	    boolean eof = false ;
	    Properties p = new Properties() ;
	    Vector v = new Vector() ;
	    while(!eof) { 
		String line = buff.readLine() ;
		if(line == null)
		    eof = true ;
		else {
		    v.add(line) ;
		}  
	    }
	    return v ;
	} catch(Exception e) {
	    return null ;
	}
    }


    public synchronized void writeFile(	File fileObj, Vector v) {
	try {
	    log("Doing save...");
			
	    // create a file writer for the file "music.db" and set append to true			
	    //	boolean append = true;
	    FileWriter file = new FileWriter(fileObj) ;
			
	    // create a print writer based on fileWriter and set autoflush to true		
	    boolean autoFlush = true;
	    PrintWriter outputToFile = new PrintWriter(file, autoFlush);

	    for( int i = 0 ; i < v.size() ; i++ ) { 
    		String aLine = (String) v.get(i) ;
		outputToFile.println(aLine) ;		
	    }

	    outputToFile.close() ;
			
	}	catch (IOException exc) {
	    log("Error occurred during the save.");
	}
		
    } 



    public synchronized String getProp(Vector v, String wantedProp) {
	// The new style, should fix if there´s no more tokens
	for( int i = 0 ; i < v.size() ; i++ ) { 
	    String aLine = (String) v.get(i) ;
	    String propName = "" ;
	    String propVal = "" ;
	    StringTokenizer st = new StringTokenizer(aLine, "=") ;
	    if(st.hasMoreTokens() )
		propName = st.nextToken().trim() ;
	    if(st.hasMoreTokens() )
		propVal = st.nextToken().trim() ;
			
	    if(propName.equalsIgnoreCase(wantedProp) ) return propVal ; 
	}	
	return ""  ;
    }

    public synchronized Vector setProp(Vector v, String prop, String val) {
	// The new style, should fix if there´s no gmore tokens
	for( int i = 0 ; i < v.size() ; i++ ) { 
	    String aLine = (String) v.get(i) ;
	    String propName = "" ;
	    String propVal = "" ;
	    StringTokenizer st = new StringTokenizer(aLine, "=") ;
	    if(st.hasMoreTokens() )
		propName = st.nextToken().trim() ;
	    if(st.hasMoreTokens() )
		propVal = st.nextToken().trim() ;
			
	    if(propName.equalsIgnoreCase(prop) ) {
		v.set(i, (prop + "=" + val) ) ;
		return v ;
	    }
	}
    	v.add((prop + "=" + val) ) ;
	return v  ;
    }


    /**
       Init: Detects paths and filenames.
    */

    public void init(ServletConfig config) throws ServletException {
	super.init(config);
	FILE_PATH = getInitParameter("file_path");
	HTML_TEMPLATE = "AdminSystemMessage.htm"; 
    
	if( FILE_PATH == null ) {
	    Enumeration initParams = getInitParameterNames();
	    System.err.println("The init parameters were: ");
	    while (initParams.hasMoreElements()) {
		System.err.println(initParams.nextElement());
	    }
	    System.err.println("The filepath was not set. Default path will be used");
	    FILE_PATH = "C:\\IMCSERVER\\" ;
	    // throw new UnavailableException (this,
	    // "Not given a path to serverhome library") ;

	}
	
	log("Serverhome:" + getInitParameter("file_path")) ;
	// log("ServletPath:" + getInitParameter("servlet_path")) ;

    }
			
    public void log( String str) {
	super.log(str) ;
	System.out.println("AdminSystemMessage: " + str ) ;	
    }
			
	
} // End of class
