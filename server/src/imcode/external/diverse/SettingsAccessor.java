package imcode.external.diverse ;
import java.util.*;
import java.io.*;


public class SettingsAccessor {

    private File FILE_NAME ;
    private Properties settingsTable ;
    private String delimiter ;
    
    public SettingsAccessor(File fileName) {
        super() ;
        // load the data into the table
        FILE_NAME = fileName ;
        settingsTable = new Properties();
        delimiter = "||" ;
        //	loadSettings();
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
    
    private synchronized Properties readSettings(BufferedReader inputFromFile) throws IOException {
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
        private void log(Object msg) {
        
        System.out.println("SettingsDataAccessor: " + msg);
    }
    
        /**
         *  Saves the data to a storage device.  <p>
         *
         *  <b><i> NOTE: This method is left as an exercise for the student.  </i></b><br>
         */



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

    private String convertArgument(String theProp) {
        return theProp.toUpperCase() ;
    }

} // END SETTINGSACCESSOR
