import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import imcode.external.diverse.* ;
import imcode.util.* ;

public class DiagramViewer extends HttpServlet {

		// String FILE_PATH ;            // The physical path to the path to where this
		String HTML_TEMPLATE ;         // the relative path from web root to where the servlets are

		public void doPost(HttpServletRequest req, HttpServletResponse res)
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
      String serverName = MetaInfo.getServerName(req) ;
      String startUrl = MetaInfo.getStartUrl(req) ;
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
	 		  String msg = "The parameters was not correct in call to DiagramViewer." ;
	 		  msg += "The parameters was: " + params.toString() ;
	 		  this.log("Error in checkingparamters for DiagramViewer") ;
	 		  Error err = new Error(req,res, "ERROR.HTM", msg) ;
	 		  err = null ;
	 		  return ;
	 	}

  // Lets get the TemplateFolder
	  String templateLib = MetaInfo.getExternalTemplateFolder(req) ;
  	// log("Templatelib: " + templateLib) ;

  // Ok, Lets detect which DiagramType it is
  	MetaTranslator meta = new MetaTranslator(templateLib + "DIAGRAM_DB.INI") ;
    synchronized(meta) {
    	meta.loadSettings() ;
    }

		String diagramType = meta.getDiagramType(params.getProperty("META_ID")) ;
		// this.log("Diagramtype:" + diagramType) ;

		if(diagramType.equals("")) {
		// Ok, we couldnt identify the diagramType
   	   	VariableManager vm = new VariableManager() ;
  	   	String msg = "DiagramType not found! Parameters was:" + params.toString() + "<BR>" ;
      	msg += "Diagramtype=" + diagramType ;
      //	this.log(msg) ;
      	vm.addProperty("ERROR_MESSAGE", msg ) ;

   		  HtmlGenerator htmlObj = new HtmlGenerator(templateLib + "ERROR.HTM") ;
			  String htm = htmlObj.createHtmlString(vm, req) ;
				htmlObj.sendToBrowser(req,res,htm) ;
				htmlObj = null ;
				vm = null ;
				return ;

	// Lets check if we shall use another viewer servlet
		} else if(diagramType.equals("1")) {
			  String url = metaInf.getServletPath(req) ;
			  url += "ViewDiagram" + diagramType + "?" ;
				url += metaInf.passMeta(params) ;
				// this.log("DiagramViewer redirects to viewer:" + url) ;
				this.reDirect(req, res, url) ;
				return ;
		}

		// Lets get the diagramgenerator url
			String host 				= req.getHeader("Host") ;
			String diagramGen 	= Utility.getDomainPref("diagramGenerator",host) ;

    // Lets get the path to the directory where the diagramfiles are located
    	String filePath 	= Utility.getDomainPref("diagram_path",host) ;

    // log("Ok, DiagramViewer kör") ;
   	HtmlGenerator aHtml = new HtmlGenerator() ;
	  String diagramUrl = this.createUrl("DIAGRAM_URL", params, templateLib, diagramGen) ;
	  String tableHeader = aHtml.createTableHeader(params, templateLib, filePath) ;
	  String table = aHtml.createTable(params, templateLib, filePath) ;

	 	// Lets check the diagramUrl and the table, forget about the tableheader if
    // the user has forgot to set a header
    if ( diagramUrl.equals("")) {
  	 // if ( diagramUrl.equals("") || table.equals("")) {

  	   	VariableManager vm = new VariableManager() ;
  	   	String msg = "Error in creating html page! Parameters were:" + params.toString() + "<BR>" ;
       	msg += "DiagramURL:" + diagramUrl + "<BR>" ;
      	msg += "TableHeader:" + tableHeader + "<BR>" ;
      	msg += "Table:" + table + "<BR>" ;

      	vm.addProperty("ERROR_MESSAGE", msg ) ;
			  HtmlGenerator htmlObj = new HtmlGenerator(templateLib, "ERROR.HTM") ;
			  String htm = htmlObj.createHtmlString(vm, req) ;
				htmlObj.sendToBrowser(req,res,htm) ;
				htmlObj = null ;
				vm = null ;
				return ;
    } else {
	 			if(tableHeader == null) {
	 				tableHeader = "" ;
	 				this.log("Table header was null") ;
	 			}

	 			if(table == null) {
	 				table = "" ;
	 				this.log("Table was null") ;
	 			}
				// this.log("passed") ;

  	  	// Lets generate the html code
  	  	VariableManager vm = new VariableManager() ;
				vm.addProperty("SERVLET_URL", MetaInfo.getServletPath(req) ) ;
  	   	vm.addProperty("DIAGRAM_PICTURE", diagramUrl ) ;
  	  	vm.addProperty("TABLE_HEADER", tableHeader ) ;
		    vm.addProperty("TABLE", table ) ;
			//	this.log("Här är tabellen" + table) ;

			  HtmlGenerator htmlObj = new HtmlGenerator(templateLib, HTML_TEMPLATE) ;
			  String htm = htmlObj.createHtmlString(vm, req) ;
				htmlObj.sendToBrowser(req, res, htm) ;
				htmlObj = null ;
				vm = null ;
		}
  } // end of doPost


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
      	this.log("MetaID: " + metaId + " kunde inte hittas i databasen!") ;
      	return "" ;
      }

      diagramGen += anUrl ;
     // this.log("Redirect to url:" + diagramGen ) ;
      return diagramGen ;
		}

	/**
		Detects paths and filenames.
	*/

	public void init(ServletConfig config) throws ServletException {

		super.init(config);
    // FILE_PATH = getInitParameter("file_path");
		// ASP_URL = getInitParameter("asp_url") ;
		HTML_TEMPLATE = "template_DiagramViewer.htm" ;

		/*
		if( ASP_URL == null || HTML_TEMPLATE == null || FILE_PATH == null) {
	    Enumeration initParams = getInitParameterNames();
	    System.err.println("DiagramViewer: The init parameters were: ");
	    while (initParams.hasMoreElements()) {
				System.err.println(initParams.nextElement());
	    }
	    System.err.println("DiagramViewer: Should have seen one parameter name");
	    throw new UnavailableException (this,
		"Not given a path to the asp diagram files");
		}

	  this.log("FilePath:" + getInitParameter("file_path")) ;
	  this.log("Asp url:" + getInitParameter("asp_url")) ;
	  this.log("HtmlTemplate:" + getInitParameter("html_template")) ;

*/
	 }
/**
	Log function, will work for both servletexec and Apache
**/

public void log( String str) {
			super.log(str) ;
		  System.out.println("DiagramViewer: " + str ) ;
	}
} // End of class