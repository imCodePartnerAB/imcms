package imcode.external.diverse ;
import java.util.*;
import java.io.*;


public class SettingsAccessor extends DataAccessor {
	private final static String CVS_REV = "$Revision$" ;
	private final static String CVS_DATE = "$Date$" ;
    
    protected String FILE_NAME ;
    protected Properties settingsTable ;
    protected String delimiter ;
    
    public SettingsAccessor(String fileName) {
        super() ;
        // load the data into the table
        FILE_NAME = fileName ;
        settingsTable = new Properties();
        delimiter = "||" ;
        //	loadSettings();
    }
    
    
        /**
         *  Returns the current used delimiter
         */
    public String getDelimiter() {
        return delimiter ;
    }
    
        /**
         *  Sets the delimiter which should be used
         */
    
    public void setDelimiter(String newDelim) {
        delimiter = newDelim ;
    }
    
    
    
    
        /**
         *  Loads the data from a storage device.
         */
    public synchronized void loadSettings() {
        
        StringTokenizer st = null;
        try	{
            // Lets open the settingsfile
            log("Loading File: " + FILE_NAME + "...");
            BufferedReader inputFromFile = new BufferedReader(new FileReader(FILE_NAME));
            
            // Lets get the settings from file into settingsTable, its a property
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
    
    protected synchronized Properties readSettings(BufferedReader inputFromFile) throws IOException {
        Properties table = new Properties() ;
        
        StringTokenizer st;
        String aLine;
        String propName, propVal ;
        
        // The new style, should fix if there´s no more tokens
        while (	(aLine = inputFromFile.readLine()) != null ) {
            propName = "" ;
            propVal = "" ;
            st = new StringTokenizer(aLine, delimiter) ;
            if(st.hasMoreTokens() )
                propName = this.convertArgument(st.nextToken().trim()) ;
            if(st.hasMoreTokens() )
                propVal = st.nextToken().trim() ;
            
            table.put(propName, propVal) ;
        }
        
        
        // This is the old style, which cant take nothing after the token
        // it needs an empty backspace if nothing should be there
        /*
                while (	(aLine = inputFromFile.readLine()) != null ) {
                        st = new StringTokenizer(aLine, "||");
                        propName = st.nextToken().trim() ;
                        propVal = (st.nextToken().trim()) ;
                        table.put(propName, propVal) ;
                }
         */
        return table ;
    }
    
        /**
         *  Helper method for logging message to the console.
         */
    protected void log(Object msg) {
        
        System.out.println("SettingsDataAccessor: " + msg);
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
    
    public synchronized void saveSettings() {
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
    
    protected synchronized void writeSettings() throws IOException {
        try {
            
            this.checkSettings() ;
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
                if(oValue == null)
                    oValue = " " ;
                String aLine = new String(oKeys.toString() + delimiter + oValue.toString());
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
        
        Enumeration enumValues = settingsTable.elements() ;
        Enumeration enumKeys = settingsTable.keys() ;
        
        while((enumValues.hasMoreElements() && enumKeys.hasMoreElements())) {
            Object oKeys = (enumKeys.nextElement()) ;
            Object oValue = (enumValues.nextElement()) ;
            //	 System.out.println("Här är:" + oValue.toString()) ;
            String theVal = oValue.toString() ;
            
            if(theVal.equals("")) {
                theVal = " " ;
                settingsTable.setProperty(oKeys.toString(), theVal) ;
            }
            
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
    
/*
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
 */
    
    public String toString(){
        return settingsTable.toString()	;
    }
    
/**
 * returns a setting
 */
    public String getSetting(String wantedProp) {
        String retStr = null ;
        String wantedStr = this.convertArgument(wantedProp) ;
        retStr = (String) settingsTable.get(wantedStr) ;
        return retStr ;
    }
    
  /**
   * Sets a setting
   */
    
    public void setSetting(String theProp, String theValue) {
        if(theValue == null){
            theValue = " " ;
        }
        theProp = this.convertArgument(theProp) ;
        settingsTable.put(theProp, theValue) ;
    }
    
    private String convertArgument(String theProp) {
        return theProp.toUpperCase() ;
    }
    
/**
 * Returns the properties
 */
    public Properties getAllProps() {
        return settingsTable ;
    }
    
/**
 * Resets the settingstable
 */
    public void resetProps() {
        settingsTable = new Properties() ;
    }
    
/**
 * Replaces all current settings with the new properties
 */
    public void replaceAllProps(Properties newProps) {
        settingsTable = newProps ;
    }
    
    
} // END SETTINGSACCESSOR