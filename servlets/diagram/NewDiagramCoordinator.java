import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;

public class NewDiagramCoordinator extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;

	String HTML_TEMPLATE ; 				// The template file to generate the html page
			
	public void doPost(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException {    
    
    // Lets get the parameters and validate them
    
    	Properties params = this.getParameters(req,res) ;
    	MetaInfo metaInf = new MetaInfo() ;
    
    	if (metaInf.checkParameters(params) == false) {
	 		  String msg = "The parameters was not correct in call to NewDiagramCoordinator." ;
	 		  msg += "The parameters was: " + params.toString() ;
	 		  Error err = new Error(req,res, "ERROR.HTM", msg) ;
	 		  err = null ;
	 		  return ;
	 		} 
  	  	
  	  	VariableManager vm = new VariableManager() ;
  	  	
  	  	// Ok Lets build the string where the NewDiagrams Editstuff will
  	  	// be showed in the new Window this html internalDocument will open for us.
  		
  			String serverUrl = metaInf.getServletPath(req) ;
  	    String url = serverUrl + "NewDiagram?" ;
  	    String paramStr = metaInf.passMeta(params) ;
  	    paramStr +=  "&diagramType=" + params.getProperty("DIAGRAM_TYPE") ;
  	    url += paramStr ;
  	  	vm.addProperty("NEW_DIAGRAM_URL", url ) ;
  	  	
  	  	String janusUrl = serverUrl + "BackToJanus" ;
	
	 	  // Lets add the information needed for the open edit window 
  	  	vm.addProperty("META_ID", params.getProperty("META_ID") ) ;
				vm.addProperty("PARENT_META_ID", params.getProperty("PARENT_META_ID") ) ;
				vm.addProperty("COOKIE_ID", params.getProperty("COOKIE_ID") ) ;
				vm.addProperty("GO_JANUS_URL", janusUrl ) ;
				
			// Lets add the information needed for the Change Metadata, tillbaka till MetaData
   			vm.addProperty("META_ID_EXT", params.getProperty("META_ID") ) ;
				vm.addProperty("PARENT_META_ID_EXT", params.getProperty("PARENT_META_ID") ) ;
				vm.addProperty("COOKIE_ID_EXT", params.getProperty("COOKIE_ID") ) ;
			
			// Lets add the information which i used to get the servername
				vm.addProperty("SERVER_URL", serverUrl ) ;
			// Lets get the TemplateFolder
				String templateLib = MetaInfo.getExternalTemplateFolder(req) ;
   	
			 	HtmlGenerator htmlObj = new HtmlGenerator(templateLib, HTML_TEMPLATE) ;
			  String htm = htmlObj.createHtmlString(vm, req) ;
			  htmlObj.sendToBrowser(req,res,htm) ;
				htmlObj = null ;
				vm = null ;
				return ;	
  	  
  	} // end of doGet 
	
	
	
/**
	Collects the parameters from the request object 
**/
	
public Properties getParameters( HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {
		
	// Lets get the standard metainformation 
	MetaInfo metaInf = new MetaInfo() ;
	Properties ReqParams = metaInf.getParameters(req) ;
	
	// Lets get the parameters we know we are supposed to get from the request object
	String dType = (req.getParameter("diagramType")==null) ? "" : (req.getParameter("diagramType")) ;
	ReqParams.setProperty("DIAGRAM_TYPE", dType) ;
	
	// this.log("Properties:" + infoObj.toString()) ;
	return ReqParams ;
}

/**
	Opens the appropriate method, POST/GET 
**/

public void service (HttpServletRequest req, HttpServletResponse res)
	  throws ServletException, IOException {
	
		String action = req.getMethod() ;
		log("Action:" + action) ;
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
    HTML_TEMPLATE = "NewDiagramCoordinator.htm" ;
  }

/**
	Log function, will work for both servletexec and Apacheservers
**/

public void log( String str) {
			super.log(str) ;
		  System.out.println("NewDiagramCoordinator: " + str ) ;	
	}


} // End of class