import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import imcode.util.* ;

public class ChangeDiagramCoordinator extends HttpServlet {
		String HTML_TEMPLATE ; 				// The template file to generate the html page
			
	public void doPost(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException {    
    
    // Lets get the parameters and validate them
    	
    	MetaInfo metaInf = new MetaInfo() ;
    	Properties params = metaInf.getParameters(req) ;
    
    	if (metaInf.checkParameters(params) == false) {
	 		  String msg = "The parameters was not correct in call to ChangeDiagramCoordinator." + "<BR>" ;
	 		  msg += "The parameters was: " + params.toString() ;
	 		  Error err = new Error(req,res, "ERROR.HTM", msg) ;
	 		  err = null ;
	 		  return ;
	 		} 
  	  
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
      String serverName = MetaInfo.getServerName(req) ;
      String startUrl = MetaInfo.getStartUrl() ;
     // log("StartUrl: " + serverName + startUrl) ;
      res.sendRedirect(serverName + startUrl);
      return  ;
    } 
 	  
  	  String url = metaInf.getServletPath(req) ;
 			VariableManager vm = new VariableManager() ;
 // Lets add the information needed for the Change Metadata, tillbaka till MetaData
 			vm.addProperty("META_ID", params.getProperty("META_ID") ) ;
			vm.addProperty("PARENT_META_ID", params.getProperty("PARENT_META_ID") ) ;
			vm.addProperty("COOKIE_ID", params.getProperty("COOKIE_ID") ) ;
			vm.addProperty("META_ID_CHANGE", params.getProperty("META_ID") ) ;
			vm.addProperty("PARENT_META_ID_CHANGE", params.getProperty("PARENT_META_ID") ) ;
			vm.addProperty("COOKIE_ID_CHANGE", params.getProperty("COOKIE_ID") ) ;
 	 		vm.addProperty("SERVER_URL", url ) ;
			vm.addProperty("SERVER_URL2", url ) ;

 	// Lets get the TemplateFolder
			String templateLib = MetaInfo.getExternalTemplateFolder(req) ;

  	  HtmlGenerator htmlObj = new HtmlGenerator(templateLib, HTML_TEMPLATE) ;
		  String htm = htmlObj.createHtmlString(vm,req) ;
		  htmlObj.sendToBrowser(req,res,htm) ;
			htmlObj = null ;
			vm = null ;
			return ;	
				
  } // end of doGet 
	
/**
	Redirect to a new URL
**/
		public void reDirect(HttpServletRequest req, HttpServletResponse res, String url)
                               throws ServletException, IOException {
      res.sendRedirect(url) ;
		}


/**
	Opens the appropriate method, POST/GET 
**/

public void service (HttpServletRequest req, HttpServletResponse res)
	  throws ServletException, IOException {
	
		String action = req.getMethod() ;
	//	log("Action:" + action) ;
		if(action.equals("POST")) {
			  this.doPost(req,res) ;
		}	else {
			  this.doPost(req,res) ;
		}
	}


	/**
		Detects inital parameters like paths and filenames.
	*/
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
    HTML_TEMPLATE = "ChangeDiagramCoordinator.htm" ;
  }
  
/**
	Log function, will work for both servletexec and Apacheservers
**/

public void log( String str) {
			super.log(str) ;
		  System.out.println("ChangeDiagramCoordinator: " + str ) ;	
	}

} // End of class