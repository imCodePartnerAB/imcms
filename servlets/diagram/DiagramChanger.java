import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;

public class DiagramChanger extends HttpServlet {

			
	public void doPost(HttpServletRequest req, HttpServletResponse res)
                               throws ServletException, IOException {
      
    // Lets get the parameters and validate them, we dont have any own
    // parameters so were just validate the metadata
    
    MetaInfo metaInf = new MetaInfo() ;
    Properties params = metaInf.getParameters(req) ;
    
    if (metaInf.checkParameters(params) == false) {
	 		  String msg = "The parameters was not correct in call to DiagramChanger." + "<BR>" ;
	 		  msg += "The parameters was: " + params.toString() ;
	 		  this.log("GET: function. Error in checkingparamters") ;
	 		  Error err = new Error(req,res, "ERROR.HTM", msg) ;
	 		  err = null ;
	 		  return ;
	 	} 
      
	 		// Lets get a redirect url so we can redirect further
	 		String url = this.createRedirectUrl(req,res,params) ;   
  	  if (url != "") {
  	    //  url += "&" + metaStr ;
   	  	 this.reDirect(req,res, url) ; 
   	  	 return ;
   	  } else {
  	  	
  	  // We did NOT found the METAID  
  	   String msg = "MetaID:et kunde inte hittas i db:n hos DiagramChanger!" ;  
	 		  msg += "The parameters was: " + params.toString() ;

  	   Error err = new Error(req,res,"ERROR.HTM", msg) ;
  	   err = null ;
       return ;
      }	
  	     
  } // end of doGet 


		public void service (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
	
		String action = req.getMethod() ;
		// log("Action:" + action) ;
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
	Create the string which we will send the user to change his diagram
**/

	public String createRedirectUrl(HttpServletRequest req,HttpServletResponse res, Properties params)
                               throws ServletException, IOException {
		          
    // Since were here with a properties object, we know its validated
    	String meta_id = params.getProperty("META_ID") ;
    
  	// Lets get the TemplateFolder
			String templateLib = MetaInfo.getExternalTemplateFolder(req) ;

    // Lets get the metaid from our DB
      MetaTranslator meta = new MetaTranslator(templateLib + "DIAGRAM_DB.INI") ;
    // Lets synchronize while were reading the Meta db
     synchronized(meta) {
     	meta.loadSettings() ;
     }
     	
     	String theServer = MetaInfo.getServletPath(req) + "ChangeDiagram";
     //	this.log(meta_id) ;
			String args = meta.getParameterInfo("CHANGE", meta_id) ;
						
      if( args == "" ) {
			  String msg = "Change: Could not identify the metaid:" + params.toString() ;
	 		  msg += "The parameters was: " + params.toString() + "<BR>";
	 		  this.log("GET: function. Error in checkingparamters") ;
	 		  Error err = new Error(req,res, "ERROR.HTM", msg) ;
	 		  err = null ;
	 		  return "";
      }
     	
     	// this.log("Nu är args:" + args) ;
     	
     // Lets find the diagramType we will change
      String diaType = meta.getDiagramType(params.getProperty("META_ID")) ;
    
    // Lets redirect to the appropriate changeservlet, for the moment, only Diagram1
    // has its own changeServlet, so lets send number 1 to ChangeDiagram2
      if(diaType.equalsIgnoreCase("1") ) 
      	theServer += diaType ;		
      
     	MetaInfo metaInf = new MetaInfo() ;
      String metaStr = metaInf.passMeta(params) ;
      metaStr += "&" + "diagramType=" + diaType ; 
      // this.log("Metastr:" + metaStr) ;
      
      String reDirectStr = theServer + args + "&" + metaStr ;
			
     //log("Redirects to url:" + reDirectStr ) ;
      return reDirectStr ;
		}

	/**
		Detects paths and filenames.
	*/
	
	public void init(ServletConfig config) throws ServletException {    	
		super.init(config);
  }

/**
	Log function, will work for both servletexec and Apache
**/

public void log( String str) {
			super.log(str) ;
		  System.out.println("DiagramChanger: " + str ) ;	
	}

} // End of class