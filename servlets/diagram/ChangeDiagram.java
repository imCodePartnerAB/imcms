import java.io.* ;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.* ;
import imcode.external.diverse.* ;
import imcode.util.* ;

public class ChangeDiagram extends HttpServlet {
	
		String HTML_TEMPLATE ;		    // The template HTML file
	
/**
		The GET method creates the html page when this side has been
		redirected from somewhere else.
**/

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	
  // Lets check the session, if not found we'll be redirected 
	  if (MetaInfo.checkSession(req, res) == false) return ;
	
	// Lets get the parameters and validate them
	 	Properties params = this.getParameters(req) ;
	 	MetaInfo metaInf = new MetaInfo() ;
	 	if(metaInf.checkParameters(params) == false) {
	 		  String msg = "The parameters was not correct in call to ChangeDiagram."+ "<BR>" ;
	 		  msg += "The parameters was: " + params.toString() + "<BR>";
	 		  this.log("GET: function. Error in checkingparameters") ;
	 		  Error err = new Error(req,res, "ERROR.HTM", msg) ;
	 		  err = null ;
	 		  return ;
	 	} 

 		String aDiaDataFile = params.getProperty("DIA_DATA_FILE") ;
		String aDiaPrefsFile = params.getProperty("DIA_PREFS_FILE") ;
		String aTabDataFile = params.getProperty("TAB_DATA_FILE") ;
		String aTabPrefsFile = params.getProperty("TAB_PREFS_FILE") ;
	
	// Lets find out the diagramType
		String aDiaType = params.getProperty("DIAGRAM_TYPE") ;
		if( aDiaType.equalsIgnoreCase("NOT_FOUND")) {
			// Ok, were probably coming from the DiagramChangerServlet
			// we have to find out the diagramtype by ourselves
			aDiaType = MetaInfo.getDiagramTypeFromFileName(aDiaPrefsFile) ;
			// this.log("GetDiagramTypeFromFileName returned: " + aDiaType) ;
				
			if( aDiaType.equalsIgnoreCase("")) { 
				String msg = "The parameters was not correct in call to ChangeDiagram."+ "<BR>" ;
	 		  msg += "The parameters was: " + params.toString() + "<BR>" ;
	 		  this.log("GET: function. Error in checkingparamters") ;
	 		  Error err = new Error(req,res, "ERROR.HTM", msg) ;
	 		  err = null ;
	 		  return ;
			} else {	
					params.setProperty("DIAGRAM_TYPE", aDiaType) ;
			}
		
		}

		// Lets get the path to the diagramfiles
			String host 				= req.getHeader("Host") ;
			String filePath	    = Utility.getDomainPref("diagram_path",host) ;

	// ********* OPEN FILES **********
		
		 	VariableManager vmDiagram = new VariableManager() ;
	 		VariableManager vmData = new VariableManager() ;
		 	vmDiagram = this.loadDiagramDataFromFiles(filePath, aDiaDataFile, aDiaPrefsFile) ;
			vmData = this.loadTableDataFromFiles(filePath, aTabDataFile, aTabPrefsFile) ;
		
			VariableManager vm = new VariableManager() ;
			vm.merge(vmData) ;
			vm.merge(vmDiagram) ;

		// Lets add the file params to the vm obj
			vm.addProperty("DIA_DATA_FILE", aDiaDataFile) ;
			vm.addProperty("DIA_PREFS_FILE", aDiaPrefsFile) ;
			vm.addProperty("TAB_DATA_FILE", aTabDataFile) ;
			vm.addProperty("TAB_PREFS_FILE", aTabPrefsFile) ;
			
		// Lets add the meta data
			vm.addProperty("META_ID", params.getProperty("META_ID")) ;
		  vm.addProperty("PARENT_META_ID", params.getProperty("PARENT_META_ID")) ;
			vm.addProperty("COOKIE_ID", params.getProperty("COOKIE_ID")) ;
		  
		// Lets add the server host
		  String servletHome = MetaInfo.getServletHost(req) ;
		  vm.addProperty("SERVLET_URL", servletHome) ;
		  
		// Lets add the diagramType
			vm.addProperty("DIAGRAM_TYPE", params.getProperty("DIAGRAM_TYPE")) ;
		    	
		// Lets get the TemplateFolder
			String templateLib = MetaInfo.getExternalTemplateFolder(req) ;
			// this.log("templateLib was:" + templateLib) ;	
			
			HtmlGenerator htmlObj = new HtmlGenerator(templateLib, HTML_TEMPLATE) ;
			//this.log("Parsing on server") ;
			String html = htmlObj.createHtmlString(vm,req) ;
   		htmlObj.sendToBrowser(req,res,html) ;
			htmlObj = null ;
			vm = null ;
			
	} // End doGet


	/**
		POST		
	*/	
	public void doPost(HttpServletRequest req, HttpServletResponse res)	throws ServletException, IOException {
	
		String host 				= req.getHeader("Host") ;
		String server 			= Utility.getDomainPref("userserver",host) ;
	
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

  // Lets get the path to the diagramfiles
 		String filePath	    = Utility.getDomainPref("diagram_path",host) ;
   
			
// Lets get the parameters and validate them. First, lets get the standard parameters
	 	Properties params = this.getParameters(req) ;
	 	MetaInfo metaInf = new MetaInfo() ;

// Second, lets get those parameters which we have in the hidden fields in the htmlpage
		Properties htmlParams = this.getHtmlFileParameters(req) ;	
		params.putAll(htmlParams) ;
		htmlParams = null ;
			
	//	this.log("Alla parametrear" + params.toString()) ;
	 	if(metaInf.checkParameters(params) == false) {
  	  String msg = "The parameters was not correct in call to ChangeDiagram" + "<BR>";
	 	  this.log("POST: function. Error in checkingparameters") ;
	 	  Error err = new Error(req,res,"ERROR.HTM", msg + params.toString()) ;
	 	  err = null ;
	 	  return ;
	 	} 
	 
	  // Lets check which button was pushed
		String whichButton = req.getParameter("action") ;
		// this.log("Argument till server:" + whichButton) ;	
		if(whichButton == null)
			whichButton = "" ;		
	 //	this.log("POST: Properties: " + params.toString() ) ;
	 	
	
	// ******* OPEN FILES **********
   // if( whichButton.equalsIgnoreCase("Återgå")) {
	  if(req.getParameter("revert") != null ) {	
      String url = this.createRedirectUrl(req, res, params) ;
	  	this.log("redirect till" + url) ;
	  	MetaInfo.reDirect(res, url) ;

	    return ;
	 } 
	// ******* SAVE TO FILES **********
		// else if( whichButton.equalsIgnoreCase("Spara") || whichButton.equalsIgnoreCase("Spara och avsluta") ) {
		 	else if( req.getParameter("save") != null ) {
 
		// Lets put the filenames into local variables 
	 		String aDiaDataFile = params.getProperty("DIA_DATA_FILE") ;
			String aDiaPrefsFile = params.getProperty("DIA_PREFS_FILE") ;
			String aTabDataFile = params.getProperty("TAB_DATA_FILE") ;
			String aTabPrefsFile = params.getProperty("TAB_PREFS_FILE") ;
			
		// Lets get the DIAGRAMSETTINGS and save them to file
			SettingsAccessor diaSettingsObj = new SettingsAccessor(filePath + aDiaPrefsFile) ;
	    diaSettingsObj = this.getDiagramSettingsFromHtml(req, diaSettingsObj ) ;
	    synchronized(diaSettingsObj) { 
	    	diaSettingsObj.saveSettings() ;
	    }
			diaSettingsObj = null ; 
		//	this.log("DiagramSettings saved successfully...") ;
	
		
		// Lets get the TABLESETTINGS and save them to file
			SettingsAccessor tabSettingsObj = new SettingsAccessor(filePath + aTabPrefsFile) ;
	    tabSettingsObj = this.getTableSettingsFromHtml(req, tabSettingsObj) ;
	    synchronized(tabSettingsObj) { 
	    	tabSettingsObj.saveSettings() ; 
	    }
			tabSettingsObj = null ;
		//	this.log("Tablesettings saved successfully...") ;

	// Lets get the DIAGRAMVALUES and save them to file	
			Vector theValues = new Vector() ;
			theValues = this.getDataFromHtml(theValues, "diagram", req, params) ;
			theValues = this.checkDataValues(theValues) ;
			ValueAccessor dataAcc = new ValueAccessor(filePath + aDiaDataFile) ;
			dataAcc.add(theValues) ;
			synchronized(dataAcc) {
				dataAcc.saveValues() ;
			}
			dataAcc = null ;
			theValues = null ;
			
		// Lets get the TABLEVALUES and save them to file	
			Vector tabValues = new Vector() ;
			tabValues = this.getDataFromHtml(tabValues, "table", req, params) ;
			tabValues = this.checkDataValues(tabValues) ;
			ValueAccessor tabDataAcc = new ValueAccessor(filePath + aTabDataFile) ;
			tabDataAcc.add(tabValues) ;
			synchronized(tabDataAcc) {
				tabDataAcc.saveValues() ;
			}
			tabDataAcc = null ;
			tabValues	= null ;
			this.log("The diagramfiles were saved successfully") ;
		
		// Ok, were done saving files. Lets tell Janus system to show this child.
			RmiLayer rmiObj = new RmiLayer(user) ;
			rmiObj.activateChild(server, params.getProperty("META_ID")) ;
			rmiObj = null ;
			
		// Ok Lets open up our just saved files
			String url = this.createRedirectUrl(req, res, params) ;
	  	MetaInfo.reDirect(res, url) ;
	    return ;				
			}
			
			// Unidentified action to ChangeDiagram			
			else {
			  String msg = "Unidentified action in call to ChangeDiagram: " + whichButton + "<BR>" ;
	 		  this.log(msg) ;
	 		  Error err = new Error(req,res, "ERROR.HTM", msg) ;
	 		  return ;
			}
   	} // end HTTP POST

	
/**
		Loads all information stored in the files into a variableManager object
*/
	
	public VariableManager loadTableDataFromFiles(String path, String dataFile, String prefsFile){
	
		// Lets get the tablevalues
	  // Lets synchronize the file reading
	 		ValueAccessor tableAcc = new ValueAccessor(path + dataFile) ;
	 		char replace = '|' ;
	 		String tableStr  = "" ;
			
			synchronized(tableAcc) {
			 tableStr = tableAcc.loadAsTabDelimited(replace) ;
			}
			String harTass = "" + '"' ;
			if( ! tableStr.trim().equals("") ) {
				tableStr = harTass + tableStr + harTass ;
			}
		
			tableAcc = null ;
			
		// Lets get the settings
		SettingsAccessor tableSetAcc = new SettingsAccessor(path + prefsFile) ;
		synchronized(tableSetAcc) {
			tableSetAcc.loadSettings() ;
		}
									
			// Lets add the settings and datavalues to vManager
			VariableManager myManager = new VariableManager() ;	
			myManager.merge(tableSetAcc.getAllProps()) ;
			myManager.addProperty("TABLE_DATA", tableStr) ;
			return myManager ;	  
	}

/**
		Loads all information stored in the files into a String object.
		The string will we put into a hidden field into the html file
		so the html file can create the table instead of we shall do it.
*/
	
	public VariableManager loadDiagramDataFromFiles(String path, String dataFile, String prefsFile){
		
		// Lets get the datavalues
		ValueAccessor myValAcc = new ValueAccessor(path + dataFile) ;
	  char replace = '|' ;
		String diagramDataStr  = "" ;
		
		synchronized(myValAcc) {
			 diagramDataStr = myValAcc.loadAsTabDelimited(replace) ;
		}
		String harTass = "" + '"' ;
		if( ! diagramDataStr.trim().equals("") ) {
			diagramDataStr = harTass + diagramDataStr + harTass ;
		}
		
	//	this.log("Diagramdata är:" + diagramDataStr ) ;
		myValAcc = null ;
	// Lets get the settings
		SettingsAccessor mySetAcc = new SettingsAccessor(path + prefsFile) ;
		this.log("Settingsfile är :" + path + prefsFile ) ;
		mySetAcc.loadSettings() ;
		
	// Lets add the settings and datavalues to vManager
		VariableManager myManager = new VariableManager() ;
		myManager.addProperty("DIAGRAM_DATA",diagramDataStr) ;
		myManager.merge(mySetAcc.getAllProps()) ;
		return myManager ;	  
	}

/**
	Collects the counter variables from the html page and then collects the 
	number of rows and columns from that page.
*/
	
public Vector getDataFromHtml( Vector v, String nameBase, HttpServletRequest req, 
	Properties params) throws ServletException {
		
		// Lets collect the number of rows and columns
			int numOfRows = 0 ;
			int numOfColumns = 0 ;
			if( nameBase.equalsIgnoreCase("diagram")) {
					numOfRows = Integer.parseInt(params.getProperty("DIA_NBR_OF_ROWS")) ;			
					numOfColumns = Integer.parseInt(params.getProperty("DIA_NBR_OF_COLS")) ;
			} else if( nameBase.equalsIgnoreCase("table")) {
					numOfRows = Integer.parseInt(params.getProperty("TAB_NBR_OF_ROWS")) ;			
					numOfColumns = Integer.parseInt(params.getProperty("TAB_NBR_OF_COLS")) ;
			}
			
		// this.log(nameBase + " Antal rader:" + numOfRows) ;
		// this.log(nameBase + " Antal kolumner:" + numOfColumns) ;

			// Lets create a charVector
			Vector charVect = MetaInfo.createCharVector() ;
			for(int i=1; i <= numOfRows ; i++) {
		 		 String aLine = "" ;
				 for(int j = 1; j <= numOfColumns; j++) {
		  	 // Lets get the correct character
		     		String colName = (String) charVect.elementAt(j-1) ;
		     		colName = colName.toLowerCase() ;
		     	  String argument = nameBase + "_" + colName + "_" + i  ;
				 // this.log("Diagramfältet som skall hämtas är: " + argument) ; 
				  
				    String aDiaValue = "" ;
					  aDiaValue = aDiaValue +	req.getParameter(argument) ;
					  if (aDiaValue == null) {
					  	aDiaValue = " " ;
					  	this.log("Ett av diagramfälten är felaktigt i html sidan:" + argument) ;
					  }
					  
			  		aDiaValue = aDiaValue.trim() ;
						if(aDiaValue.equals("")) 
					  	aDiaValue = " " ;
						
						// Lets check if were on the last column, in that case
						// dont hang on the doublePipe!
						aLine = aLine + aDiaValue	;
						if(j != numOfColumns)
							aLine = aLine + "|" ;	
									 }
				 		v.add(aLine) ;
			}
		return v ;	
	}


 public Vector checkDataValues(Vector v) {
 		for(int i = 0; i < v.size(); i++) {
 			String tmp = (String) v.get(i) ;
 			if( tmp.equals(""))	{
 				tmp = "0" ;	
 				v.setElementAt(tmp,i)	;
 			}	
 		}		
 		return v ;
 }
	
	
	public SettingsAccessor getTableSettingsFromHtml(HttpServletRequest req, SettingsAccessor setAcc) {
  	
   	setAcc.setSetting("TABLEHEADER", req.getParameter("tableHeader")) ;
  	return setAcc ;
  } // end getSettingsFromHtml
	
	
	public SettingsAccessor getDiagramSettingsFromHtml(HttpServletRequest req, SettingsAccessor setAcc) {
  	
  	// OBServe that the argument to getParameter must match the value in html file
   	setAcc.setSetting("HEADER", req.getParameter("diaHeader")) ;
	 	setAcc.setSetting("WIDTH", req.getParameter("diaWidth")) ;
  	setAcc.setSetting("HEIGHT", req.getParameter("diaHeight")) ;
  	setAcc.setSetting("XHEADER", req.getParameter("diaXHeader")) ;
		setAcc.setSetting("YHEADER", req.getParameter("diayHeader")) ;
	 	setAcc.setSetting("HORIZAXISMAX", req.getParameter("diaHorizMax")) ;
  	setAcc.setSetting("HORIZAXISMIN", req.getParameter("diaHorizMin")) ;
  	setAcc.setSetting("VERTAXISMAX", req.getParameter("diaVertMax")) ;
		setAcc.setSetting("VERTAXISMIN", req.getParameter("diaVertMin")) ;
  	setAcc.setSetting("SERIESTITLE1", req.getParameter("diaSeriesTitle1")) ;
  	setAcc.setSetting("SERIESTITLE2", req.getParameter("diaSeriesTitle2")) ;
  	setAcc.setSetting("SERIESTITLE3", req.getParameter("diaSeriesTitle3")) ;
  	setAcc.setSetting("SERIESTITLE4", req.getParameter("diaSeriesTitle4")) ;
    setAcc.setSetting("LEFTAXISINCREMENT", req.getParameter("diaLeftAxisIncrement")) ;
  	setAcc.setSetting("BOTTOMAXISINCREMENT", req.getParameter("diaBottomAxisIncrement")) ;

  	return setAcc ;
  } // end getSettingsFromHtml
	


/**
	Collects the parameters from the request object 
**/
	
public Properties getParameters( HttpServletRequest req)
		throws ServletException, IOException {
		
// Lets get the standard metainformation 
	MetaInfo metaInf = new MetaInfo() ;
	Properties ReqParams = metaInf.getParameters(req) ;
	
// Lets get the parameters we know we are supposed to get from the request object
	String dPrefsFile = (req.getParameter("diaP")==null) ? "" : (req.getParameter("diaP")) ;
	String dDataFile = (req.getParameter("diaD")==null) ? "" : (req.getParameter("diaD")) ;
	String tPrefsFile = (req.getParameter("tabP")==null) ? "" : (req.getParameter("tabP")) ;
	String tDataFile = (req.getParameter("tabD")==null) ? "" : (req.getParameter("tabD")) ;

	String dType = (req.getParameter("diagramType")==null) ? "NOT_FOUND" : (req.getParameter("diagramType")) ;
	ReqParams.setProperty("DIAGRAM_TYPE", dType) ;
	
	ReqParams.setProperty("DIA_PREFS_FILE", dPrefsFile) ;
	ReqParams.setProperty("DIA_DATA_FILE", dDataFile) ;
	ReqParams.setProperty("TAB_PREFS_FILE", tPrefsFile) ;
	ReqParams.setProperty("TAB_DATA_FILE", tDataFile) ;
	
	// this.log("Properties:" + infoObj.toString()) ;
	return ReqParams ;
}
	
	
/**
	Collects parameters (Hidden fields) from the HTML file 
**/
	
public Properties getHtmlFileParameters( HttpServletRequest req )
		throws ServletException {
		
// Lets get the standard metainformation 
	Properties hiddenFieldsParams = new Properties()  ;
	
// Lets get the parameters we know we are supposed to get from the request object
	String diaRows = (req.getParameter("diaNbrOfRows")==null) ? "" : (req.getParameter("diaNbrOfRows")) ;
	String diaCols = (req.getParameter("diaNbrOfCols")==null) ? "" : (req.getParameter("diaNbrOfCols")) ;
	String tabRows = (req.getParameter("tabNbrOfRows")==null) ? "" : (req.getParameter("tabNbrOfRows")) ;
	String tabCols = (req.getParameter("tabNbrOfCols")==null) ? "" : (req.getParameter("tabNbrOfCols")) ;

	hiddenFieldsParams.setProperty("DIA_NBR_OF_ROWS", diaRows) ;
	hiddenFieldsParams.setProperty("DIA_NBR_OF_COLS", diaCols) ;
	hiddenFieldsParams.setProperty("TAB_NBR_OF_ROWS", tabRows) ;
	hiddenFieldsParams.setProperty("TAB_NBR_OF_COLS", tabCols) ;
	
	
// Lets fix the diagramType, the diagramType is only available when were coming from
// the newDiagram servlet, so if the value isnt available, then dont care about it
// just add a default value
	String diaType = (req.getParameter("diagramType")==null) ? "NOT_FOUND" : (req.getParameter("diagramType")) ;
  hiddenFieldsParams.setProperty("DIAGRAM_TYPE", diaType) ;
	
	// this.log("Properties:" + infoObj.toString()) ;
	return hiddenFieldsParams ;
}

			
/**
	Init: Detects paths and filenames.
*/
	
	public void init(ServletConfig config) throws ServletException {
  	super.init(config);
    HTML_TEMPLATE = "template_changeDiagram.htm" ; 
  }
			
	public void log( String str) {
			super.log(str) ;
		  System.out.println("ChangeDiagram: " + str ) ;	
	}
		
		
		
		/********************************* REDIRECTION FUNCTIONS *********/
	
/**
	Creates a URl
**/
	
	public String createRedirectUrl(HttpServletRequest req,HttpServletResponse res, Properties params)
	 	throws ServletException, IOException {
		// Were creating the path to the getfunction and redirects there
		  String thisServletUrl = MetaInfo.getServletHost(req) ;
	 	  String args = this.createParameterStr(req,res,params) ;
	 	 // log("RedirectURL:" + thisServletUrl + "?" + args) ;
	 	  
	 	  return thisServletUrl + "?" + args ;
	}
	
	/**
		Creates the parameters into a string, creates an url to redirect to
	**/
	
	public String createParameterStr(HttpServletRequest req,HttpServletResponse res,
		 Properties params)
	 	throws ServletException, IOException {
			// Lets get the standard meta data
			MetaInfo metaInf = new MetaInfo() ;
			String args = "" ;
			args += metaInf.passMeta(params) + "&" ;
				 	
      // Lets add this class special parameters	 	
	 	  args += "diaP=" + params.getProperty("DIA_PREFS_FILE") + "&" ;
	 	  args += "diaD=" + params.getProperty("DIA_DATA_FILE") + "&" ;
	 	  args += "tabP=" + params.getProperty("TAB_PREFS_FILE") + "&" ;
	 	  args += "tabD=" + params.getProperty("TAB_DATA_FILE") + "&" ;
	 	  args += "diagramType=" + params.getProperty("DIAGRAM_TYPE") ;

			return args ;
	}
} // End of class