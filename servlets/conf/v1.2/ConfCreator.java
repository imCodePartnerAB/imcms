import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.*;
import imcode.util.* ;

public class ConfCreator extends Conference {
	String HTML_TEMPLATE ;
	
	/**
		The POST method creates the html page when this side has been
		redirected from somewhere else.
	**/

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	
	// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;
	 
  // Lets get the standard parameters and validate them
    Properties params = super.getSessionParameters(req) ;
    if (super.checkParameters(req, res, params) == false) return ;
	
	// Lets get the new conference parameters
		Properties confParams = this.getNewConfParameters(req) ;
		if (super.checkParameters(req, res, confParams) == false) return ;
		
	// Lets get an user object  
	  imcode.server.User user = super.getUserObj(req,res) ;
	  if(user == null) return ;
		  
	 	String action = req.getParameter("action") ;
	 	if(action == null) {
    	action = "" ;
    	String header = "ConfCreator servlet. " ;
		  ConfError err = new ConfError(req,res,header,3) ;
	 		log(header + err.getErrorMsg()) ;
	 		return ;
    }
   
    // Lets get serverinformation
   		String host = req.getHeader("Host") ;	
   		String imcServer = Utility.getDomainPref("userserver",host) ;
   		String confPoolServer = Utility.getDomainPref("conference_server",host) ;
      
   	// ********* NEW ********
	  if(action.equalsIgnoreCase("ADD_CONF")) {
			log("OK, nu skapar vi konferens") ;
      
			// Added 000608
			 // Ok, Since the conference db can be used from different servers
			 // we have to check when we add a new conference that such an meta_id
			 // doesnt already exists.
       	RmiConf rmi = new RmiConf(user) ;
      	String metaId = params.getProperty("META_ID") ;
				String foundMetaId = rmi.execSqlProcedureStr(confPoolServer, "FindMetaId " + metaId) ;
      	if(!foundMetaId.equals("1")) { 
         	action = "" ;
    			String header = "ConfCreator servlet. " ;
		  		ConfError err = new ConfError(req,res,header,90) ;
	 				log(header + err.getErrorMsg()) ;
	 				return ;
    		}
        
			// Lets add a new Conference to DB
		  // AddNewConf @meta_id int, @confName varchar(255)	
	   		
				
				String confName = confParams.getProperty("CONF_NAME") ;
				// String sortType = "1" ;	// Default value, unused so far
				String sqlQ = "AddNewConf " + metaId + ", '" + confName + "'" ;
				log("AddNewConf sql:" + sqlQ ) ;
				rmi.execSqlUpdateProcedure(confPoolServer, sqlQ) ;
			
 			// Lets add a new forum to the conference
			// AddNewForum @meta_id int, @forum_name varchar(255), @archive_mode char, @archive_time int
				String newFsql = "AddNewForum " + metaId +", '" + confParams.getProperty("FORUM_NAME") + "', ";
				newFsql += "'A' , 30" ; 
				//newFsql += "'" + confParams.getProperty("ARCHIVE_MODE") + "', " ;
				//newFsql += confParams.getProperty("ARCHIVE_TIME")	;
				log("AddNewForum sql:" + newFsql ) ;
				rmi.execSqlUpdateProcedure(confPoolServer, newFsql) ;
			
			// Lets get the administrators user_id
				String user_id = user.getString("user_id") ;
		
			// Lets get the recently added forums id
				String forum_id = rmi.execSqlProcedureStr(confPoolServer, "GetFirstForum " + metaId) ;
				
			// Lets add this user into the conference if hes not exists there before were
			// adding the discussion
				String confUsersAddSql = "ConfUsersAdd "+ user_id +", "+ metaId +", '"+ user.getString("first_name") + "', '";
			  confUsersAddSql += user.getString("last_name") + "'";	
				rmi.execSqlUpdateProcedure(confPoolServer, confUsersAddSql) ;
				
	    // Ok, were done creating the conference. Lets tell Janus system to show this child.
				rmi.activateChild(imcServer, metaId) ;
		 
	  // Ok, Were done adding the conference, Lets go back to the Manager
				String loginPage = MetaInfo.getServletPath(req) + "ConfLogin?login_type=login" ;
				res.sendRedirect(loginPage) ;
				return ;
		}
		
} // End POST


	/**
		The GET method creates the html page when this side has been
		redirected from somewhere else.
	**/

	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	
	// Lets validate the session, e.g has the user logged in to Janus?
		if (super.checkSession(req,res) == false)	return ;
	 
	// Lets get the standard parameters and validate them
	  Properties params = super.getSessionParameters(req) ;
	  if (super.checkParameters(req, res, params) == false) return ;
		
	// Lets get an user object  
	  imcode.server.User user = super.getUserObj(req,res) ;
	  if(user == null) return ;
		
	 	String action = req.getParameter("action") ;
	 	if(action == null) {
	  	action = "" ;
	  	String header = "ConfCreator servlet. " ;
		  ConfError err = new ConfError(req,res,header,3) ;
	 		log(header + err.getErrorMsg()) ;
	 		return ;
	  }
	 
	 	// ********* NEW ********
	  if(action.equalsIgnoreCase("NEW")) {
					// Lets build the Responsepage to the loginpage
			VariableManager vm = new VariableManager() ;
			vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req)) ;
			sendHtml(req,res,vm, HTML_TEMPLATE) ;			
			return ;
		}
	} // End doGet


/**
	Collects the parameters from the request object 
**/
	
protected Properties getNewConfParameters( HttpServletRequest req) throws ServletException, IOException {
		
	Properties confP = new Properties() ;	
	String conf_name = (req.getParameter("conference_name")==null) ? "" : (req.getParameter("conference_name")) ;
	String forum_name = (req.getParameter("forum_name")==null) ? "" : (req.getParameter("forum_name")) ;
//	String archive_mode = (req.getParameter("archive_mode")==null) ? "" : (req.getParameter("archive_mode")) ;
//	String archive_time = (req.getParameter("archive_time")==null) ? "" : (req.getParameter("archive_time")) ;
//	String disc_header = (req.getParameter("disc_header")==null) ? "" : (req.getParameter("disc_header")) ;
//	String disc_text = (req.getParameter("disc_text")==null) ? "" : (req.getParameter("disc_text")) ;

	confP.setProperty("CONF_NAME", conf_name.trim()) ;
	confP.setProperty("FORUM_NAME", forum_name.trim()) ;
//	confP.setProperty("ARCHIVE_MODE", archive_mode.trim()) ;
//	confP.setProperty("ARCHIVE_TIME", archive_time.trim()) ;
//	confP.setProperty("DISC_HEADER", disc_header.trim()) ;
//	confP.setProperty("DISC_TEXT", disc_text.trim()) ;

//	this.log("Conference paramters:" + confP.toString()) ;
	return confP ;
}
	
/**
	Detects paths and filenames.
*/
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		HTML_TEMPLATE = "CONF_CREATOR.HTM" ;
		
		// log("Nu init vi") ;
		/*
		HTML_TEMPLATE = getInitParameter("html_template") ;
		
		if( HTML_TEMPLATE == null ) {
			Enumeration initParams = getInitParameterNames();
			System.err.println("ConfCreator: The init parameters were: ");
			while (initParams.hasMoreElements()) {
				System.err.println(initParams.nextElement());
			}
			System.err.println("ConfCreator: Should have seen one parameter name");
			throw new UnavailableException (this,
				"Not given a path to the asp diagram files");
		}

		log("HTML_TEMPLATE:" + HTML_TEMPLATE ) ;
	*/
	} // End of INIT

/**
	Log function, will work for both servletexec and Apache
**/

public void log( String str) {
			super.log(str) ;
		  System.out.println("ConfCreator: " + str ) ;	
	}


} // End class