import java.io.* ;
import javax.servlet.*;
import javax.servlet.http.*;
import java.awt.* ;
import java.util.* ;

public class NewDiagram2 extends HttpServlet {
	
		String FILE_PATH ;            // the path to where the servlets are
		String HTML_TEMPLATE ;		    // The template HTML file
	 	String SERVLET_PATH ;
	 	
	/**
		POST		
	*/	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		
	// Lets validate the parameters
	MetaInfo metaInf = new MetaInfo() ;
  Properties params = this.getParameters(req) ;
  if (metaInf.checkParameters(params) == false) {
	 		  String msg = "The parameters was not correct in call to NewDiagram." ;
	 		  msg += "The parameters was: " + params.toString() ;
	 		  Error err = new Error(req,res, FILE_PATH + "ERROR.HTM", msg) ;
	 		  err = null ;
	 		  return ;
	} 
 
		String diaType = params.getProperty("DIAGRAM_TYPE") ;
		String metaId =  params.getProperty("META_ID") ;
		
	// Lets generate new filenames 
			String newDiagDB = "NEWDIAG_DB.INI" ;
			SettingsAccessor setAcc = new SettingsAccessor(FILE_PATH + newDiagDB) ;
			synchronized(setAcc) {
				setAcc.loadSettings() ;
			}
	 		
	 		String fileNum= setAcc.getSetting("FILE_ID") ;
	 		if(fileNum == "") {
	 			String msg = "Ett FILE_ID kunde inte skapas! " + newDiagDB ;
	 			msg += "  " + params.toString() ;
	 			this.showError(req, res, msg ) ;
				return ;
	 		} 
	 				
	 	// Lets create new fileNames
	 			
	 		String aDiaDataFile  = "DIADATA" + diaType + "_" + fileNum + ".TXT" ;
			String aDiaPrefsFile = "PREFS" + diaType + "_" + fileNum + ".TXT" ;
			String aTabDataFile  = "TABDATA" + diaType + "_" + fileNum + ".TXT" ;
			String aTabPrefsFile = "TABPREFS" + diaType + "_" + fileNum + ".TXT" ;
			
					
		// Lets create settingsfiles 
				FileManager fileObj = new FileManager() ;
			
		// Lets generate the tablefiles
				String srcTabData = "template_tabdata" + diaType + ".txt" ;
				boolean ok = this.createFile(req,res,FILE_PATH, srcTabData, FILE_PATH, aTabDataFile) ;
				if(ok != true) {
					this.showError(req, res, "Skapandet av " + srcTabData + " misslyckades!") ;
					return ;
				}
						
				String srcTabPrefs = "template_tabprefs" + diaType + ".txt" ;
				ok = this.createFile(req,res,FILE_PATH, srcTabPrefs, FILE_PATH, aTabPrefsFile) ;
				if(ok != true) {
					this.showError(req, res, "Skapandet av " + srcTabPrefs + " misslyckades!") ;
					return ;
				}			
				
	
				this.log("Nu har vi skapat tabellfiler") ;
				if(diaType.equalsIgnoreCase("10")) {	
				
				} else {	
				// Lets generate the diagramFiles
	
					String srcDiaData = "template_diadata" + diaType + ".txt" ;
					ok = this.createFile(req,res,FILE_PATH, srcDiaData, FILE_PATH, aDiaDataFile) ;	
					if(ok != true) {
						this.showError(req, res, "Skapandet av " + srcDiaData + " misslyckades!") ;
						return ;
					}						
				
					String srcDiagPrefs = "template_prefs" + diaType + ".txt" ;
					ok = this.createFile(req,res,FILE_PATH, srcDiagPrefs, FILE_PATH, aDiaPrefsFile) ;
					if(ok != true) {
						this.showError(req, res, "Skapandet av " + srcDiagPrefs + " misslyckades!") ;
						return ;
					}	
				}
			this.log("Creation of new diagramfiles succeded...") ;
			
			// Lets fix the new counter value and save it do disk
	 				int file_ID = Integer.parseInt(fileNum) ;
	 				file_ID += 1 ;
	 				setAcc.setSetting("FILE_ID" ,("" + file_ID )) ;
	 				synchronized(setAcc) {
						setAcc.saveSettings() ;
					}
					setAcc = null ;
				
				
		// Lets create the metaid in our DB
      MetaTranslator meta = new MetaTranslator(FILE_PATH + "DIAGRAM_DB.INI") ;
      synchronized(meta) {
     		meta.loadSettings() ;
     		meta.setSetting(metaId, "" + diaType + ";" + fileNum ) ;
     		meta.saveSettings() ;
     	}
     	
     	
 /*  HERE IS THE OLD WORKING VERSION   	 
    // Ok, were done creating files, lets redirect to the change diagram servlet
			
			String anUrl = meta.getParameterInfo("CHANGE", metaId) ;
      if( anUrl == "" ) {
				  String msg = "Change: Could not identify the metaid:" + params.toString() ;
				 	this.showError(req, res, "Skapandet av " + srcTabPrefs + " misslyckades!") ;
       	  return  ;
      }
      
      
      // Lets append the metainfo which we put into the string into the beginning in
      String metaStr = metaInf.passMeta(params) ;
      anUrl += "&" + metaStr ;  
      this.log("Redirects to: " + anUrl) ;
			this.reDirect(req,res, anUrl) ;
	    return ;
*/

// HERE WE HAVE THE NEW STYLE... 
// Ok, were done creating files, lets CREATE the NEW html template

			String formUrl = this.getServerUrl(req) ;
      //String metaStr = metaInf.passMeta(params) ;
		  formUrl += SERVLET_PATH + "ExcelPaste"  ;
		  
	  	VariableManager vm = new VariableManager() ;
     	vm.addProperty("META_ID", params.getProperty("META_ID")) ;
     	vm.addProperty("PARENT_META_ID", params.getProperty("PARENT_META_ID")) ;
			vm.addProperty("COOKIE_ID", params.getProperty("COOKIE_ID")) ;
			
			vm.addProperty("DIA_PREFS_FILE", aDiaPrefsFile) ;
			vm.addProperty("DIA_DATA_FILE", aDiaDataFile) ;
			vm.addProperty("TAB_PREFS_FILE", aTabPrefsFile) ;
			vm.addProperty("TAB_DATA_FILE", aTabDataFile) ;


			// Heres the information for the fast generator
		//	this.log("Ok - nu klipper vi in FAST informationen") ;
			this.log("Vi skickar informationen till:" + formUrl) ;
			vm.addProperty("DIAGRAM_TYPE", params.getProperty("DIAGRAM_TYPE")) ;
		 	vm.addProperty("SERVLET_TARGET", formUrl) ;
     //	vm.addProperty("FAST_META_ID", params.getProperty("META_ID")) ;
     //	vm.addProperty("FAST_PARENT_META_ID", params.getProperty("PARENT_META_ID")) ;
		//	vm.addProperty("FAST_COOKIE_ID", params.getProperty("COOKIE_ID")) ;
			
		  HtmlGenerator htmlObj = new HtmlGenerator(FILE_PATH, HTML_TEMPLATE) ;
			String htm = htmlObj.createHtmlString(vm, req) ;
			
			htmlObj.sendToBrowser(req,res,htm) ;
			htmlObj = null ;
			vm = null ;
			return ;	

		
 } // end HTTP POST

/*
	CreateTablePrefsFile	
*/
private boolean createFile(HttpServletRequest req, HttpServletResponse res,
	  String srcPath, String src, String targPath, String target) 
	  	throws ServletException, IOException {
	  	
	  FileManager fileObj = new FileManager() ;	
  	boolean ok = fileObj.copyFile(srcPath, src, targPath, target) ;
  	this.log("CreateFile: src" + srcPath + src) ;
	//	if(ok != true) 
	//		this.showError(req, res, "Skapandet av " + src + " misslyckades!") ;
		return ok ;	
}

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


public void showError(HttpServletRequest req, HttpServletResponse res, String msg)
			throws ServletException, IOException {
 				this.log(msg) ;
  	   	VariableManager vm = new VariableManager() ;
      	vm.addProperty("ERROR_MESSAGE", msg ) ;			  
			  HtmlGenerator htmlObj = new HtmlGenerator(FILE_PATH, "ERROR.HTM") ;
			  String htm = htmlObj.createHtmlString(vm, req) ;
				htmlObj.sendToBrowser(req,res,htm) ;
				htmlObj = null ;
				vm = null ;
				return ;
		}

	
/**
	Redirect to a new URL
**/
		public void reDirect(HttpServletRequest req, HttpServletResponse res, String url)
                               throws ServletException, IOException {
      res.sendRedirect(url) ;
		}
	

/**
		Init: Detects paths and filenames.
	*/
	
	public void init(ServletConfig config)
	throws ServletException
    {
    	
		super.init(config);
    FILE_PATH = getInitParameter("file_path");
    HTML_TEMPLATE = getInitParameter("html_template"); 
    SERVLET_PATH = getInitParameter("servlet_path"); 
    
	  if (FILE_PATH == null || HTML_TEMPLATE == null || SERVLET_PATH == null) {
	    Enumeration initParams = getInitParameterNames();
	    System.err.println("The init parameters were: ");
	    while (initParams.hasMoreElements()) {
				System.err.println(initParams.nextElement());
	    }
	    System.err.println("Should have seen one parameter name");
	    throw new UnavailableException (this,
		"Not given a directory to read diagram files");
		}
	
		this.log("FilePath:" + getInitParameter("file_path")) ;
		this.log("html_template:" + getInitParameter("html_template")) ;
	}
	
	
	public void sendToBrowser(HttpServletRequest req, HttpServletResponse res, String str)
		throws ServletException, IOException {

		// Lets send settings to a browser
		PrintWriter out = res.getWriter() ;
		res.setContentType("Text/html") ;
		out.println(str) ;
	}

/**
	Collects the parameters from the html file 
**/
	public Properties getParameters( HttpServletRequest req )
		throws ServletException, IOException {
			
	// Lets get standard parameters and validate them
    
    MetaInfo metaInf = new MetaInfo() ;
    Properties params = metaInf.getParameters(req) ;
    
  // Lets get our own parameters from the request object
	  String diaType = (req.getParameter("diagramType")==null) ? "" : (req.getParameter("diagramType")) ;
	  params.setProperty("DIAGRAM_TYPE", diaType) ;
	  
	return params ;
}


/**
	Log function, will work for both servletexec and Apache
**/

public void log( String str) {
			super.log(str) ;
		 System.out.println("NewDiagram: " + str ) ;	
}


/**
	Creates a string with the serveradress where this servlet is hosted.
*/	

public String getServerUrl(HttpServletRequest req) throws ServletException {

				String protocol = req.getScheme();
      	String serverName = req.getServerName();
      	int p = req.getServerPort();
      	String port = (p == 80) ? "" : ":" + p;
      	String serverUrl = protocol + "://" + serverName + port  ;
        
  	    return serverUrl ;	
} // getServerUrl



} // End of class
	