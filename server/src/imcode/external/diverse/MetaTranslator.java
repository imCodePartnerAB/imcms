package imcode.external.diverse ;
import java.util.*;
import java.io.*;


public class MetaTranslator {

	protected String FILE_NAME ;
	protected Properties MetaTable ;
	
	
	public MetaTranslator(String aFileName) {
		super() ;
		// load the data into the table
		FILE_NAME = aFileName ;
	}



/**
	Returns the filename associated with a certain metaId
**/

public String getFileName(String whatService, String metaId) {
	  
	  String key = this.getSetting(metaId) ;
    if(key == null ) {
    	System.out.println("MetaID:" + metaId + "kunde inte hittas i databasen!") ;
    	return "" ;		
    }

 	// Lets analyze the key..
 	// Item 1 should be the diagramType
 	// Item 2 should be the id to collect the right prefs/dataFiles
 	
 		StringManager strMan = new StringManager(key) ;
     		
  // Lets get which servlet should be used
  	String diaType = strMan.getItem(";", 1) ;
  	String fileID = strMan.getItem(";", 2) ;

  // Lets detect the arguments
  	String aDiaDataFile  = "DIADATA" + diaType + "_" + fileID + ".TXT" ;
		String aDiaPrefsFile = "PREFS" + diaType + "_" + fileID + ".TXT" ;
		String aTabDataFile  = "TABDATA" + diaType + "_" + fileID + ".TXT" ;
		String aTabPrefsFile = "TABPREFS" + diaType + "_" + fileID + ".TXT" ;
 
	// TABLE FILE
   	if( whatService.equalsIgnoreCase("TABLE_DATA")) {
       	return aTabDataFile ;	
   	}	else if( whatService.equalsIgnoreCase("TABLE_PREFS")) {
       	return aTabPrefsFile ;	
   	} else if( whatService.equalsIgnoreCase("DIAGRAM_DATA")) {
       	return aDiaDataFile ;	
   	} else if( whatService.equalsIgnoreCase("DIAGRAM_PREFS")) {
       	return aDiaPrefsFile ;	
   	}

	return "" ;
}


/**
	Returns the DiagramType associated with a certain metaId
**/

public String getDiagramType(String metaId) {
		
		String key = this.getSetting(metaId) ;
    if(key == null ) {
    	System.out.println("MetaID:" + metaId + "kunde inte hittas i databasen!") ;
    	return "" ;		
    }
     		
    // Lets get the diagramType
    StringManager strMan = new StringManager(key) ;
    String diaType = strMan.getItem(";", 1) ;
		return diaType ;
}


public String getParameterInfo(String whatService, String metaId) {
		
		String key = this.getSetting(metaId) ;
    if(key == null ) {
    	System.out.println("MetaID:" + metaId + "kunde inte hittas i databasen!") ;
    	return "" ;		
    }
 
 		// VIEW_DIAGRAM
		if( whatService.equals("DIAGRAM_URL")) {
    
     		// Lets analyze the key..
     		// Item 1 should be the diagramType
     		// Item 2 should be the id to collect the right prefs/dataFiles
     		StringManager strMan = new StringManager(key) ;
     		
     		// Lets get which diagramFile should be used
     		String diaType = strMan.getItem(";", 1) ;
     		String theDiagramFile = "DIAGRAM" + diaType + ".ASP";
     	  
     	// Lets detect the arguments
     		String fileID = strMan.getItem(";", 2) ;
     		String aDiaDataFile  = "DIADATA" + diaType + "_" + fileID + ".TXT" ;
     		String aDiaPrefsFile = "PREFS" + diaType + "_" + fileID + ".TXT" ;
     		
				// This code is used when ASP diagram is used
				//String args = "?" + "diaP=" + aDiaPrefsFile + "&" + "diaD=" + aDiaDataFile  ; 
     		//String url = theDiagramFile + args ;
   		
			// This code should be activated when Christoffer diagramviewer is used
				String url = "?" + "diaP=" + aDiaPrefsFile + "&" + "diaD=" + aDiaDataFile  ; 

     		System.out.println("VIEW_DIAGRAM: " + url) ;
     		return url ;	
    	}	
    	
    		// VIEW_TABLE
			else if( whatService.equals("TABLE_URL")) {
    
     		// Lets analyze the key..
     		// Item 1 should be the diagramType
     		// Item 2 should be the id to collect the right prefs/dataFiles
     		StringManager strMan = new StringManager(key) ;
     		
     		// Lets get which diagramFile should be used
     		String diaType = strMan.getItem(";", 1) ;
     		String theAspFile = "TABELL" + diaType + ".ASP";
     	  
     	// Lets detect the arguments
     		String fileID = strMan.getItem(";", 2) ;
     		String aTabPrefsFile  = "TABPREFS" + diaType + "_" + fileID + ".TXT" ;
     		String aTabDataFile = "TABDATA" + diaType + "_" + fileID + ".TXT" ;
     		String args = "?" + "tabP=" + aTabPrefsFile + "&" + "tabD=" + aTabDataFile  ; 
     	
     		String url = theAspFile + args ;
     		System.out.println("VIEW_DIAGRAM: " + url) ;
     		return url ;
    	}	

    	// CHANGE
    	else if( whatService.equalsIgnoreCase("CHANGE")) {
    			// Lets analyze the key..
     		// Item 1 should be the diagramType
     		// Item 2 should be the id to collect the right prefs/dataFiles
     		StringManager strMan = new StringManager(key) ;
     		
     		// Lets get which servlet should be used
     		String diaType = strMan.getItem(";", 1) ;
     	  String servletChanger = "" ; // = "ChangeDiagram" + diaType;
     		String fileID = strMan.getItem(";", 2) ;

     		// Lets detect the arguments
     		String aDiaDataFile  = "DIADATA" + diaType + "_" + fileID + ".TXT" ;
				String aDiaPrefsFile = "PREFS" + diaType + "_" + fileID + ".TXT" ;
				String aTabDataFile  = "TABDATA" + diaType + "_" + fileID + ".TXT" ;
				String aTabPrefsFile = "TABPREFS" + diaType + "_" + fileID + ".TXT" ;
     		
     		
     		String args = "?" + "diaP=" + aDiaPrefsFile + "&" + "diaD=" + aDiaDataFile  ; 
      	args +=  "&" + "tabP=" + aTabPrefsFile + "&" + "tabD=" + aTabDataFile ; 
				
				// Lets pass the metaid as well
			//	args += "&" + "METAID=" + metaId ;
				      		
     		String url = servletChanger + args ;
       	System.out.println("CHANGE: " + url) ;
     		return url ;	

       		
    	}	
    	return "" ;
}




	/**
	 *  Loads the data from a storage device.
	 */
	public void loadSettings() {
	
		StringTokenizer st = null;	
		try	{
		// Lets open the settingsfile
			log("Loading File: " + FILE_NAME + "...");
			BufferedReader inputFromFile = new BufferedReader(new FileReader(FILE_NAME));
			
			// Lets get the settings from file into settingsTable, its a property
			MetaTable = readSettings(inputFromFile);
			
			inputFromFile.close();
			log(FILE_NAME + " loaded successfully!");
		
		}	catch (FileNotFoundException exc) {
			log("Could not find the file \"" + FILE_NAME + "\".");
			log("Make sure it is in the current directory.") ;
			log(exc);
			
		}	catch (IOException exc) {
			log("IO error occurred while reading file: " + FILE_NAME);
			log(exc);
		}
	}

	/**
	 *  Helper method for reading settings from a file.
	 */

	protected Properties readSettings(BufferedReader inputFromFile) throws IOException {
		Properties table = new Properties() ;
		
		StringTokenizer st;
		String aLine;
		String propName, propVal ;
	
		while (	(aLine = inputFromFile.readLine()) != null ) {
			st = new StringTokenizer(aLine, "||");
			propName = st.nextToken().trim() ;
			propVal = (st.nextToken().trim()) ; 
			table.put(propName, propVal) ;
		}	
		
		return table ;
	}

 	/**
	 *  Helper method for logging message to the console.
	 */
	protected void log(Object msg) {
	
		System.out.println("MetaTranslator: " + msg);
	}
	
	/**
	 *  Saves the data to a storage device.  <p>
	 *  @exception IOException thrown if error occurs during IO
	 */ 
	 
	 public void saveSettings() throws IOException{
	 	try {
			writeSettings() ;
	 	
	 	}	catch (FileNotFoundException exc) {
			log("Could not find the file \"" + FILE_NAME + "\".");
			log("Make sure it is in the current directory.") ;
			log(exc);
			
		}	catch (IOException exc) {
			log("IO error occurred while reading file: " + FILE_NAME);
			log(exc);
		}
	}

protected void writeSettings() throws IOException {
		try {
			
		this.checkSettings() ;
			log("Doing save...");
			
		// create a file writer for the file "music.db" and set append to true			
		//	boolean append = true;
			FileWriter myFileWriter = new FileWriter(FILE_NAME, false);
			
			// create a print writer based on fileWriter and set autoflush to true		
			boolean autoFlush = true;
			PrintWriter outputToFile = new PrintWriter(myFileWriter, autoFlush);
		
			Enumeration enumValues = MetaTable.elements() ;
			Enumeration enumKeys = MetaTable.keys() ;
	
			while((enumValues.hasMoreElements() && enumKeys.hasMoreElements())) {
         Object oKeys = (enumKeys.nextElement()) ;		 
				 Object oValue = (enumValues.nextElement()) ;
				 if(oValue == null) 
				 		oValue = " " ; 
				 String aLine = new String(oKeys.toString() + "||" + oValue.toString());
     		 outputToFile.println(aLine) ;		
			}

			outputToFile.close() ;
			
		}	catch (IOException exc) {
			log("Error occurred during the save.");
			log(exc);
		}
		
	} 



public void checkSettings(){
	
			log("Verifying settings...");
			
			Enumeration enumValues = MetaTable.elements() ;
			Enumeration enumKeys = MetaTable.keys() ;
	
			while((enumValues.hasMoreElements() && enumKeys.hasMoreElements())) {
         Object oKeys = (enumKeys.nextElement()) ;
				 Object oValue = (enumValues.nextElement()) ;
			//	 System.out.println("Här är:" + oValue.toString()) ;
				 String theVal = oValue.toString() ;
				 
				 if(theVal.equals("")) {
				 	theVal = " " ; 
				 	MetaTable.setProperty(oKeys.toString(), theVal) ;
				 }
				 
			}
	} 


public String toString(){
	return MetaTable.toString()	;
}

/**
	* returns a setting	
	*/
	public String getSetting(String wantedProp) {
		String retStr = null ;
		retStr = (String) MetaTable.get(this.convertArgument(wantedProp)) ;
		return retStr ;
	}
	
  /**
	* Sets a setting	
	*/
	
	public void setSetting(String theProp, String theValue) {
		theProp = this.convertArgument(theProp) ;	
		MetaTable.put(theProp, theValue) ;
	}

	private String convertArgument(String theProp) {
		return theProp.toUpperCase() ;
	}

/**
	Returns the properties
*/
	public Properties getAllProps() {
		return MetaTable ;	
	}


} // END METATRANSLATOR