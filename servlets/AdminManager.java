import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import imcode.util.* ;

public class AdminManager extends Administrator {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
    String HTML_TEMPLATE ;

    /**
       The GET method creates the html page when this side has been
       redirected from somewhere else.
    **/

    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	
	// Lets get the server this request was aimed for
	String host = req.getHeader("Host") ;	
	String imcServer = Utility.getDomainPref("adminserver",host) ;
	// log("THIS SERVER: " + imcServer) ;
  
	//log("ADMINTEMPLATEFOLDER" + super.getAdminTemplateFolder(req,res)) ;
  
	// Lets validate the session
	if (super.checkSession(req,res) == false)	return ;
		
	// Lets get an user object  
	imcode.server.User user = super.getUserObj(req,res) ;
	if(user == null) {
	    String header = "Error in AdminManager." ;
	    String msg = "Couldnt create an user object."+ "<BR>" ;
	    this.log(header + msg) ;
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}
	
	// Lets verify that the user who tries to add a new user is an admin  	
	if (super.checkAdminRights(imcServer, user) == false) { 
	    String header = "Error in AdminManager." ;
	    String msg = "The user is not an administrator."+ "<BR>" ;
	    this.log(header + msg) ;
	 		
	    // Lets get the path to the admin templates folder
	    String server 			= Utility.getDomainPref("adminserver",host) ;  
	    File templateLib = getAdminTemplateFolder(server, user) ;
	    //this.log("Host: " + host) ;
	    //this.log("Server: " + server) ;
	    //this.log("TemplateLib: " +  templateLib) ;
 
	    AdminError err = new AdminError(req,res,header,msg) ;
	    return ;
	}

	// Lets generate the html page
	VariableManager vm = new VariableManager() ;
	vm.addProperty("STATUS","..." ) ;
 	super.sendHtml(req, res, vm, HTML_TEMPLATE) ;
			
    } // End doGet

    /**
       doPost
    */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {    
	// Lets get the parameters and validate them, we dont have any own
	// parameters so were just validate the metadata
  		  
	String whichButton = req.getParameter("AdminTask") ;
	this.log("Argument till server:" + whichButton) ;	
	if(whichButton == null)	whichButton = "" ;		
	 	
	String url = MetaInfo.getServletPath(req) ;
   	if( whichButton.equalsIgnoreCase("UserStart")) {
	    url += "AdminUser" ;
	} else if( whichButton.equalsIgnoreCase("CounterStart")) { 
	    url += "AdminCounter" ;
   	} else if( whichButton.equalsIgnoreCase("AddTemplates")) { 
	    url += "TemplateAdmin" ;
	} else if( whichButton.equalsIgnoreCase("DeleteDocs")) { 
	    url += "AdminDeleteDoc" ;
	} else if( whichButton.equalsIgnoreCase("IP-access")) { 
	    url += "AdminIpAccess" ;
	} else if( whichButton.equalsIgnoreCase("SystemMessage")) { 
	    url += "AdminSystemInfo" ;
	} else if( whichButton.equalsIgnoreCase("AdminRoles")) { 
	    url += "AdminRoles" ;
	} else if( whichButton.equalsIgnoreCase("UrlDocTest")) { 
	    url += "UrlDocTest" ;
	} else if( whichButton.equalsIgnoreCase("MetaAdmin")) { 
	    url += "MetaAdmin" ;      
	} else if( whichButton.equalsIgnoreCase("FileAdmin")) { 
	    url += "FileAdmin" ;
	} else if( whichButton.equalsIgnoreCase("ListDocs")) { 
	    url += "AdminListDocs" ;
	} else if( whichButton.equalsIgnoreCase("AdminConference")) { 
	    url += "AdminConference" ;
	} else {
	    // Ok, were came here cause no valid argument was sent to us
	    // Lets send the user back to the Get function.	
	    this.doGet(req,res) ;
	    return ;
	}
	// Ok, Lets redirect the user to the right adminservlet	
	this.log("redirects + to:" + url) ;		
 	res.sendRedirect(url) ;
    } 
	

    /**
       Init: Detects paths and filenames.
    */
	
    public void init(ServletConfig config) throws ServletException {
    	
	super.init(config);
	HTML_TEMPLATE = "AdminManager.htm" ;
	this.log("Initializing AdminManager") ;
    
	/*    HTML_TEMPLATE = getInitParameter("html_template"); 
  
	      if (HTML_TEMPLATE == null ) {
	      Enumeration initParams = getInitParameterNames();
	      System.err.println("The init parameters were: ");
	      while (initParams.hasMoreElements()) {
	      System.err.println(initParams.nextElement());
	      }
	      System.err.println("Should have seen one parameter name");
	      throw new UnavailableException (this,
	      "Not given a directory to read init files");
	      }
	
	      this.log("html_template:" + getInitParameter("html_template")) ;
	*/

    }


    /**
       Log function, will work for both servletexec and Apache
    **/

    public void log( String str) {
	super.log(str) ;
	System.out.println("AdminManager: " + str ) ;	
    }



} // End of class
