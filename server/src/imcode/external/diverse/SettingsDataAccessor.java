package imcode.external.diverse ;
import java.util.*;
import java.io.*;


public class SettingsDataAccessor extends DataAccessor {


	//////////////////////////////////////////////////////
	//
	//  DATA FILE FORMAT:
	//
	//  The settings file has the following format:
	// FILENAME=c:\kalle.txt
	// HEADER=rubrik
	// TYPE=1
	// SIZEX=300
	// SIZEY=200
	// IMAGEGQUALITY=100
	// XHEADER=x
	// YHEADER=y
	//
	/**
	 *  The name of the database file to read/write.
	 */
	protected String FILE_NAME ;
	 
	/**
	 *  Constructs the data accessor and calls the load() method
	 *  to load data.
	 *  
	 */
	public SettingsDataAccessor() {
		super() ;
		// load the data into the table
		loadSettings();
	}

	public SettingsDataAccessor(String fileName) {
		super() ;
		// load the data into the table
		FILE_NAME = fileName ;
		loadSettings();
	}

	/**
	*	Creates new settings and value files
	*/
	private void createSettingsFile(String theFile) {
		FILE_NAME = theFile ;
	/*
		this.setSetting("FileName", theFile) ;
		this.setSetting("Header", "Rubrik");
		this.setSetting("DiagramQuality", "80") ;
	*/	
	}
	


/**
	* returns a setting	
	*/
	public String getSetting(String wantedProp) {
		String retStr = null ;
		retStr = (String) settingsTable.get(this.convertArgument(wantedProp)) ;
		return retStr ;
	}
	
  /**
	* Sets a setting	
	*/
	
	public void setSetting(String theProp, String theValue) {
		theProp = this.convertArgument(theProp) ;	
		settingsTable.put(theProp, theValue) ;
	}

	private String convertArgument(String theProp) {
		return theProp.toUpperCase() ;
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
			
			// Lets get the settings from file into settingsTable
			settingsTable = readSettings(inputFromFile);
			
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
			st = new StringTokenizer(aLine, "=");
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
	
		System.out.println("SettingsDataAccessor: " + msg);
	}

	/**
	 *  An overridden toString method for printing the object to the console.
	 */

	public String toString() {
		Enumeration enumValues = settingsTable.elements() ;
		Enumeration enumKeys = settingsTable.keys() ;

	  String str = "" ;
	//	System.out.println(enum.toString()) ;
	
		while((enumValues.hasMoreElements() && enumKeys.hasMoreElements())) {
         Object oKeys = (enumKeys.nextElement()) ;		 
				 Object oValue = (enumValues.nextElement()) ;
				// System.out.println("" + oKeys.toString() + oValue.toString()) ;
				 str = str +  oKeys.toString() + " = " + oValue.toString() + "\n" ;
     }
		//System.out.println(str) ;
		return str ;
	}
	 
	
	/**
	 *  Saves the data to a storage device.  <p>
	 *
	 *  <b><i> NOTE: This method is left as an exercise for the student.  </i></b><br>
	 *//**
	 *  Saves the data to a storage device.  <p>
	 *
	 *  <b><i> NOTE: This method is left an exercise for the student.  </i><b><br>
	 *
	 *
	 *  @exception IOException thrown if error occurs during IO
	 */ 
	 
	 public void saveSettings() {
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
			log("Doing save...");
			
		// create a file writer for the file "music.db" and set append to true			
		//	boolean append = true;
			FileWriter myFileWriter = new FileWriter(FILE_NAME, false);
			
			// create a print writer based on fileWriter and set autoflush to true		
			boolean autoFlush = true;
			PrintWriter outputToFile = new PrintWriter(myFileWriter, autoFlush);
		
			Enumeration enumValues = settingsTable.elements() ;
			Enumeration enumKeys = settingsTable.keys() ;
	
			while((enumValues.hasMoreElements() && enumKeys.hasMoreElements())) {
         Object oKeys = (enumKeys.nextElement()) ;		 
				 Object oValue = (enumValues.nextElement()) ;
				 String aLine = new String(oKeys.toString() + "=" + oValue.toString());
     		 outputToFile.println(aLine) ;		
			}

			outputToFile.close() ;
			
		}	catch (IOException exc) {
			log("Error occurred during the save.");
			log(exc);
		}
		
	} 


	public boolean fileExist(	String theDir, String theFile){
		try {
			File fileObj = new File(theDir) ;
			if(fileObj.exists()) 
				return true ;
		} catch (NullPointerException e) {
					log("The file couldnt be found " + e.getMessage()) ;
			}
		catch (Exception e) {
					log("The file couldnt be found " + e.getMessage()) ;
		}
		finally	{ return false ; }
	}	


	public boolean fileDelete() {
		try {
			String theDir = System.getProperty("user.dir") ;
			String theFile = FILE_NAME ;
			File fileObj = new File(theDir, theFile) ;
			if(this.fileExist(theDir, theFile))
				return fileObj.delete() ;
		
		} catch (NullPointerException e) {
					log("The file couldnt be deleted " + e.getMessage()) ;
			}
		catch (Exception e) {
					log("The file couldnt be deleted " + e.getMessage()) ;
		}
		finally	{ return false ; }
	}	




} // END SETTINGSDATAACCESSOR