import java.io.* ;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.* ;
import imcode.external.diverse.* ;
import imcode.util.* ;

public class NewDiagram extends HttpServlet {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
	
//		String FILE_PATH ;            // the path to where the servlets are
		String HTML_TEMPLATE ;		    // The template HTML file
	
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
	 		  Error err = new Error(req,res, "ERROR.HTM", msg) ;
	 		  err = null ;
	 		  return ;
	} 
 
 // Lets get the TemplateFolder
		String templateLib = MetaInfo.getExternalTemplateFolder(req) ;

		String diaType = params.getProperty("DIAGRAM_TYPE") ;
		String metaId =  params.getProperty("META_ID") ;
		
	// Lets generate new filenames 
			String newDiagDB = "NEWDIAG_DB.INI" ;
			SettingsAccessor setAcc = new SettingsAccessor(templateLib + newDiagDB) ;
			synchronized(setAcc) {
				setAcc.loadSettings() ;
			}
	 		
	 		String fileNum= setAcc.getSetting("FILE_ID") ;
	 		if(fileNum == "") {
	 			String msg = "Ett FILE_ID kunde inte skapas! " + newDiagDB ;
	 			msg += "The parameters was: " + params.toString() ;
	 		  Error err = new Error(req,res, "ERROR.HTM", msg) ;
	 		  err = null ;
	 		  return ;
	 		} 
	 				
	 	// Lets create new fileNames
	 			
	 		String aDiaDataFile  = "DIADATA" + diaType + "_" + fileNum + ".TXT" ;
			String aDiaPrefsFile = "PREFS" + diaType + "_" + fileNum + ".TXT" ;
			String aTabDataFile  = "TABDATA" + diaType + "_" + fileNum + ".TXT" ;
			String aTabPrefsFile = "TABPREFS" + diaType + "_" + fileNum + ".TXT" ;
			
					
		// Lets create settingsfiles 
				FileManager fileObj = new FileManager() ;
			

    // Lets get the path to the directory where the diagramfiles are located
    	String host 				= req.getHeader("Host") ;
			String filePath 	= Utility.getDomainPref("diagram_path",host) ;
              
		// Lets generate the tablefiles
				String srcTabData = "template_tabdata" + diaType + ".txt" ;
				boolean ok = this.createFile(req,res,filePath, srcTabData, filePath, aTabDataFile) ;
				if(ok != true) {
					this.showError(req, res, "Skapandet av " + srcTabData + " misslyckades!",params) ;
				 	return ;
				}
						
				String srcTabPrefs = "template_tabprefs" + diaType + ".txt" ;
				ok = this.createFile(req,res,filePath, srcTabPrefs, filePath, aTabPrefsFile) ;
				if(ok != true) {
					this.showError(req, res, "Skapandet av " + srcTabPrefs + " misslyckades!",params) ;
					return ;
				}			
	
				this.log("Nu har vi skapat tabellfiler") ;
			
				// Lets generate the diagramFiles
	
					String srcDiaData = "template_diadata" + diaType + ".txt" ;
					ok = this.createFile(req,res,filePath, srcDiaData, filePath, aDiaDataFile) ;	
					if(ok != true) {
						this.showError(req, res, "Skapandet av " + srcDiaData + " misslyckades!",params) ;
						return ;
					}						
				
					String srcDiagPrefs = "template_prefs" + diaType + ".txt" ;
					ok = this.createFile(req,res,filePath, srcDiagPrefs, filePath, aDiaPrefsFile) ;
					if(ok != true) {
						this.showError(req, res, "Skapandet av " + srcDiagPrefs + " misslyckades!",params) ;
						return ;
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
      MetaTranslator meta = new MetaTranslator(templateLib + "DIAGRAM_DB.INI") ;
      synchronized(meta) {
     		meta.loadSettings() ;
     		meta.setSetting(metaId, "" + diaType + ";" + fileNum ) ;
     		meta.saveSettings() ;
     	}
     	
     	
    // Ok, were done creating files, lets redirect to the change diagram servlet
			
			String theServer = MetaInfo.getServletPath(req) + "ChangeDiagram";
			String anUrl = meta.getParameterInfo("CHANGE", metaId) ;
      
      if( anUrl == "" ) {
			  String msg = "Change: Could not identify the metaid:" + params.toString() ;
	 		  msg += "The parameters was: " + params.toString() + "<BR>";
	 		  this.log("GET: function. Error in checkingparamters") ;
	 		  Error err = new Error(req,res, "ERROR.HTM", msg) ;
	 		  err = null ;
	 		  return ;
      }
      
      
      // Lets redirect to the appropriate changeservlet, for the moment, only Diagram1
      // has its own changeServlet, so lets send number 1 to ChangeDiagram2
      
      if(diaType.equalsIgnoreCase("1") ) 
      	theServer += diaType ;		
      
      anUrl = theServer + anUrl ;     
      
           
      // Lets append the metainfo which we put into the string into the beginning in
      String metaStr = metaInf.passMeta(params) ;
      metaStr += "&" + "diagramType=" + diaType ; 
      anUrl += "&" + metaStr ;  
      this.log("Redirects to: " + anUrl) ;
			this.reDirect(req,res, anUrl) ;
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
  	this.log("CreateFile: src: " + srcPath + src) ;
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


public void showError(HttpServletRequest req, HttpServletResponse res, String msg, Properties params)
			throws ServletException, IOException {
 				this.log(msg) ;
 				
 				msg += "The parameters was: " + params.toString() ;
	 		  Error err = new Error(req,res, "ERROR.HTM", msg) ;
	 		  err = null ;
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
	
	public void init(ServletConfig config) throws ServletException {
    	
		super.init(config);
    HTML_TEMPLATE = "NewDiagram_template.htm" ; 
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


} // End of class
	