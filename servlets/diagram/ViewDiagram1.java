import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import imcode.util.* ;

public class ViewDiagram1 extends HttpServlet {

		String VIEW_TEMPLATE ; 		// The html file used as template
			
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    
  // Lets check that we still have a session, Get the session
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
     
    // Lets get the parameters and validate them, we dont have any own
    // parameters so were just validate the metadata
    
    MetaInfo metaInf = new MetaInfo() ;
    Properties params = metaInf.getParameters(req) ;
    //this.log("Ok här är params:" + params.toString()) ;
    
    if (metaInf.checkParameters(params) == false) {
	 		  String msg = "The parameters was not correct in call to ViewDiagram1." ;
	 		  msg += "The parameters was: " + params.toString() ;
	 		  this.log("Error in checkingparamters for ViewDiagram1") ;
	 		  Error err = new Error(req,res, "ERROR.HTM", msg) ;
	 		  err = null ;
	 		  return ;
	 	} 
  	
    // Lets get the diagramgenerator url
    	String host 				= req.getHeader("Host") ;
    	String diagramGen 	= Utility.getDomainPref("diagramGenerator",host) ;
    	//log("Diagramgen. " + diagramGen) ;
      
      
  	// Lets get the TemplateFolder
			String templateLib = MetaInfo.getExternalTemplateFolder(req) ;      	
    	String diagramUrl = this.createUrl("DIAGRAM_URL", params, templateLib, diagramGen) ;  
 
 	 // We have a Table and a header  
  
  		if ( diagramUrl != "" ) {
  	  // Lets generate the html code
  	  	VariableManager vm = new VariableManager() ;
  	   	vm.addProperty("DIAGRAM_PICTURE", diagramUrl ) ;
  	   	vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req) ) ;
  	  
			  HtmlGenerator htmlObj = new HtmlGenerator(templateLib, VIEW_TEMPLATE) ;
			  String htm = htmlObj.createHtmlString(vm, req) ;
			  // this.log("Följande parsades:" + htm) ;
				htmlObj.sendToBrowser(req,res,htm) ;
				htmlObj = null ;
				vm = null ;
				return ;
  	} else {
  	  	
  	  // We did NOT found the METAID   
  	   	VariableManager vm = new VariableManager() ;
  	   	String msg = "Meta_ID was not found in Database! Parameters was:" + params.toString() ;
      	vm.addProperty("ERROR_MESSAGE", msg ) ;			  
			  HtmlGenerator htmlObj = new HtmlGenerator("ERROR.HTM") ;
			  String htm = htmlObj.createHtmlString(vm, req) ;
				htmlObj.sendToBrowser(req,res,htm) ;
				htmlObj = null ;
				vm = null ;
  	  }	
  	
  } // end of doPost 


public void service (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	
		String action = req.getMethod() ;
		//log("Action:" + action) ;
		if(action.equals("POST")) {
			  this.doPost(req,res) ;
		}	else {
			  this.doPost(req,res) ;
		}
	}

/**
	Redirect to a new URL
**/
		public void reDirect(HttpServletRequest req, HttpServletResponse res, String url)
       throws ServletException, IOException {
      res.sendRedirect(url) ;
		}


/**
	Create the url which shall be used to redirect to / send arguments  
**/

	public String createUrl(String type, Properties params, String path, String diagramGen) {
		    
      String metaId = params.getProperty("META_ID");
		
	 // Lets get the metaid from our DB. Synchronize while reading the Meta db
      MetaTranslator meta = new MetaTranslator(path + "DIAGRAM_DB.INI") ;
      synchronized(meta) {
     		meta.loadSettings() ;
      }
     	
     	String anUrl = meta.getParameterInfo(type, metaId) ;
      if( anUrl == "" ) {
      	this.log("MetaID: " + metaId + " wasn't found in the db!") ;
      	return "" ;
      }
     	
      diagramGen += anUrl ; 
     // this.log("Redirect to url:" + reDirectStr ) ;
      return diagramGen ;
		}

	/**
		Detects paths and filenames.
	*/
	
	public void init(ServletConfig config) throws ServletException {
    super.init(config);
		VIEW_TEMPLATE = "template_DiagramView1.htm" ;
	}

/**
	Log function, will work for both servletexec and Apache
**/

public void log( String str) {
			super.log(str) ;
		  System.out.println("ViewDiagram1: " + str ) ;	
	}
} // End of class