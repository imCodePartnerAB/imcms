import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;

public class DiagramCreator extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

		String HTML_TEMPLATE ; 				// The template file to generate the html page
			
	public void doPost(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException {    
    // Lets get the parameters and validate them, we dont have any own
    // parameters so were just validate the metadata
    
    MetaInfo metaInf = new MetaInfo() ;
    Properties params = metaInf.getParameters(req) ;
    if (metaInf.checkParameters(params) == false) {
	 		  String msg = "The parameters was not correct in call to DiagramCreator." ;
	 		  msg += "The parameters was: " + params.toString() ;
	 		  Error err = new Error(req,res, "ERROR.HTM", msg) ;
	 		  err = null ;
	 		  return ;
	 	} 

  	  	VariableManager vm = new VariableManager() ;
  	   	vm.addProperty("META_ID", params.getProperty("META_ID")) ;
      	vm.addProperty("PARENT_META_ID", params.getProperty("PARENT_META_ID")) ;
				vm.addProperty("COOKIE_ID", params.getProperty("COOKIE_ID")) ;
				
				String server = MetaInfo.getServletPath(req) ;
			//	this.log("servletpath: " + server) ;
				vm.addProperty("SERVER_URL", server) ;
				vm.addProperty("SERVER_URL2", server) ;

			// Lets get the TemplateFolder
			  String templateLib = MetaInfo.getExternalTemplateFolder(req) ;
			  
			  HtmlGenerator htmlObj = new HtmlGenerator(templateLib, HTML_TEMPLATE) ;
			  String htm = htmlObj.createHtmlString(vm, req) ;
			  htmlObj.sendToBrowser(req,res,htm) ;
				htmlObj = null ;
				vm = null ;
				return ;	
  	  
  	} // end of doGet 
	
	
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
		Detects paths and filenames.
	*/
	
	public void init(ServletConfig config) throws ServletException {
    	
		super.init(config);
    HTML_TEMPLATE = "template_diagramCreator.htm" ; 
  }

/**
	Log function, will work for both servletexec and Apache
**/

public void log( String str) {
			super.log(str) ;
		  System.out.println("DiagramCreator: " + str ) ;	
	}

} // End of class