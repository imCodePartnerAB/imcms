import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import java.rmi.* ;
import java.rmi.registry.* ;

public class ConfHelp extends Conference {

  String USER_TEMPLATE ;
	String ADMIN_TEMPLATE ;  // The name on this servlet
	String ADMIN_TEMPLATE2 ; 
		
	public void doPost(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException {
     
 	// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;
   
 	// Lets get all parameters for this servlet
		Properties params = this.getParameters(req) ;
    	if (super.checkParameters(req, res, params) == false) {
		  String header = "ConfLogin servlet. " ;
		  String msg = params.toString() ;
		  ConfError err = new ConfError(req,res,header,1) ;
		  return ;
	 	} 
	 	
 	// Lets get the user object 	
	 	imcode.server.User user = super.getUserObj(req,res) ;
	 	if(user == null) return ;

 	// Lets detect which helparea the user wants
    String helpArea = params.getProperty("HELP_AREA") ;

  // Lets get a VariableManager
   	VariableManager vm = new VariableManager() ;

	// Lets create the path to our html page
		String file = USER_TEMPLATE	;
		if( params.getProperty("HELP_MODE").equalsIgnoreCase("ADMIN") ) 
			file = ADMIN_TEMPLATE ;
		else 	if( params.getProperty("HELP_MODE").equalsIgnoreCase("TEMPLATESPEC") )
			file = ADMIN_TEMPLATE2;	
		//if( params.getProperty("HELP_SPEC").equalsIgnoreCase("SPEC") ) file = ADMIN_TEMPLATE2 ;
		
			
		this.sendHtml(req,res,vm, file) ;
 	 	return ;
 	} //DoPost
 
/**
	Collects all the parameters used by this servlet
**/
	
public Properties getParameters( HttpServletRequest req)
		throws ServletException, IOException {
		
	Properties params = super.getSessionParameters(req) ;

// Lets get the EXTENDED SESSION PARAMETERS 
	super.getExtSessionParameters(req, params) ;

// Lets get our REQUESTPARAMETERS	
	String helpInfo = (req.getParameter("helparea")==null) ? "" : (req.getParameter("helparea")) ;
	String helpMode = (req.getParameter("helpmode")==null) ? "" : (req.getParameter("helpmode")) ;
	//String helpSpec = (req.getParameter("helpspec")==null) ? "-1" : (req.getParameter("helpspec")) ;

	//params.setProperty("HELP_SPEC", helpSpec) ;
	params.setProperty("HELP_AREA", helpInfo) ;
	params.setProperty("HELP_MODE", helpMode) ;
	return params ;
}
  
/**
	Service method. Sends the user to the post method
**/

public void service (HttpServletRequest req, HttpServletResponse res)
	  throws ServletException, IOException {
	
		String action = req.getMethod() ;
		// log("Action:" + action) ;
		if(action.equals("POST")) 
			  this.doPost(req,res) ;
		else 
			  this.doPost(req,res) ;
}


/**
	Init
**/
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
 		USER_TEMPLATE = "Conf_help_user.htm" ;
		ADMIN_TEMPLATE = "Conf_help_admin.htm" ;
		ADMIN_TEMPLATE2 = "Conf_help_admin2.htm" ;
	}
	 
/**
	Log function, will work for both servletexec and Apache
**/

public void log( String str) {
			super.log(str) ;
		  System.out.println("ConfHelp: " +  str ) ;	
}
	
} // End of class