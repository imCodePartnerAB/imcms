package imcode.external.diverse ;
import java.util.*;
import java.io.*;

/**
 * Example of 2 rows in the dataFile
 * Kliogest;252,420;245,364; 9,3;-7 056;- 2,8;582,585
 * Behepan Tabl;193,096;268,647; 10,2;75,551; 39,1;394,899
 */


public class ValueAccessor {
    private String FILE_NAME ;
    private Vector diagramValues ;
    
    public ValueAccessor (String aFileName) {
        // super() ;
        // load the data into the table
        FILE_NAME = aFileName ;
        diagramValues = new Vector() ;
        //	load();
    }
    
    
    
    
/**
 * Loads a file into a vector
 */
    public synchronized void load() {
        
        StringTokenizer st = null;
        try	{
            // Lets open the valuefile
            log("Loading File: " + FILE_NAME + "...");
            BufferedReader inputFromFile = new BufferedReader(new FileReader(FILE_NAME));
            
            // Lets get the Values from file into settingsTable
            diagramValues = readValues(inputFromFile);
            
            inputFromFile.close();
            log("Valuefile loaded successfully!");
            
        }	catch (FileNotFoundException exc) {
            log("Could not find the file \"" + FILE_NAME + "\".");
            log("Make sure it is in the current directory.") ;
            log(exc);
            
        }	catch (IOException exc) {
            log("IO error occurred while reading file: " + FILE_NAME);
            log(exc);
        }
    } // loadvalues
    
    protected synchronized Vector readValues(BufferedReader inputFromFile) throws IOException {
        String aLine;
        Vector v = new Vector() ;
        
        while ((aLine = inputFromFile.readLine()) != null ) {
            v.add(aLine) ;
        }
        
        return v ;
    }
    
/**
 * Loads the values into a tabDelimtied string AND returns the string
 */
    public synchronized String loadAsTabDelimited(char replaceChar) {
        
        StringTokenizer st = null;
        String retStr = "" ;
        try	{
            // Lets open the valuefile
            log("Loading File: " + FILE_NAME + "...");
            BufferedReader inputFromFile = new BufferedReader(new FileReader(FILE_NAME));
            
            // Lets get the Values from file into settingsTable
            retStr = readValuesAsTabDelimited(inputFromFile, replaceChar);
            
            inputFromFile.close();
            log("Valuefile loaded successfully!");
            
            
        }	catch (FileNotFoundException exc) {
            log("Could not find the file \"" + FILE_NAME + "\".");
            log("Make sure it is in the current directory.") ;
            log(exc);
            
        }	catch (IOException exc) {
            log("IO error occurred while reading file: " + FILE_NAME);
            log(exc);
        }
        return retStr ;
    } // loadvalues
    
    
    
/**
 * Reads a file into a tabdelimited string.
 */
    public synchronized String readValuesAsTabDelimited(BufferedReader inputFromFile,
    char replaceChar) throws IOException {
        String aLine;
        String str = new String() ;
        char newLine = '\n' ;
        char tab = '\t' ;
        
        while ((aLine = inputFromFile.readLine()) != null ) {
            // Ok, Lets analyze it and replace all semicolons with tab
            aLine = aLine.trim() ;
            //	System.out.println("Rad: " + aLine) ;
            if(!aLine.equals("")) {
                aLine = aLine.replace(replaceChar, tab) ;
                str  += aLine ;
            }
            str += newLine ;
        }
        return str ;
    }
    
    
/**
 * Loads the values into a tabDelimtied string AND returns the string
 */
    public synchronized void loadAndReplace(char oldChar, char newChar) {
        
        StringTokenizer st = null;
        String retStr = "" ;
        try	{
            // Lets open the valuefile
            log("Loading File: " + FILE_NAME + "...");
            BufferedReader inputFromFile = new BufferedReader(new FileReader(FILE_NAME));
            
            // Lets get the Values from file into settingsTable
            replaceChar(inputFromFile, oldChar, newChar );
            
            inputFromFile.close();
            //	log("Valuefile saved successfully!");
            
            
        }	catch (FileNotFoundException exc) {
            log("Could not find the file \"" + FILE_NAME + "\".");
            log("Make sure it is in the current directory.") ;
            log(exc);
            
        }	catch (IOException exc) {
            log("IO error occurred while reading file: " + FILE_NAME);
            log(exc);
        }
        
    } // loadvalues
    
    
    
    
/**
 * Reads a file, and replace all chars for another one.
 */
    public synchronized void replaceChar(BufferedReader inputFromFile,
    char oldChar, char newChar) throws IOException {
        String aLine;
        String str = new String() ;
        char newLine = '\n' ;
        Vector v = new Vector() ;
        
        while ((aLine = inputFromFile.readLine()) != null ) {
            // Ok, Lets analyze it and replace all oldchars with newChars
            aLine = aLine.trim() ;
            if(!aLine.equals("")) {
                aLine = aLine.replace(oldChar, newChar) ;
                // log("Här är en line: " + aLine) ;
            }
            v.add(aLine) ;
        }
        
        //	log("Här är: " + v.toString() ) ;
        // Lets exchange diagramValues vector to this vector
        diagramValues = v ;
        // Lets save to file
        saveValues() ;
        log("Successfully saved: " + FILE_NAME) ;
    }
    
    
    
    public synchronized boolean saveValues() {
        try {
            writeValues() ;
            
        }	catch (FileNotFoundException exc) {
            log("Could not find the file \"" + FILE_NAME + "\".");
            log("Make sure it is in the current directory.") ;
            log(exc);
            return false ;
            
        }	catch (IOException exc) {
            log("IO error occurred while reading file: " + FILE_NAME);
            log(exc);
            return false ;
        }
        return true ;
    }
    
    public synchronized void writeValues() throws IOException {
        try {
            log("Doing save...");
            
            // create a file writer for the file "music.db" and set append to true
            //	boolean append = true;
            FileWriter myFileWriter = new FileWriter(FILE_NAME, false);
            
            // create a print writer based on fileWriter and set autoflush to true
            boolean autoFlush = true;
            PrintWriter outputToFile = new PrintWriter(myFileWriter, autoFlush);
            String str = "" ;
            
            for(int i = 0 ; i < diagramValues.size() ; i++) {
                str = (String) diagramValues.get(i) ;
                outputToFile.println(str) ;
            }
            
            outputToFile.close() ;
            
        }	catch (IOException exc) {
            log("Error occurred during the save.");
            log(exc);
        }
        
    }
    
    
        /**
         *  Helper method for logging message to the console.
         */
    public void log(Object msg) {
        System.out.println("ValuesAccessor: " + msg);
    }
    
        /**
         *  An overridden toString method for printing the object to the console.
         */
    
    public String toString() {
        String str = "" ;
        for(int i = 0 ; i < diagramValues.size() ; i++) {
            str = str + diagramValues.get(i) + "\n" ;
            //System.out.println("DiagramValuesAccessor: " + msg);
        }
        return str ;
    }
    
        /**
         * Returns all values
         */
    public Vector getAllValues() {
        return diagramValues ;
    }
    
/**
 * Adds the new vectors arguments to the current vector
 */
    public synchronized void add(Vector newVect) {
        
        diagramValues.addAll(newVect) ;
    }
    
    public void reset() {
        diagramValues.removeAllElements() ;
    }
    
/*
        Replaces the current vector with the new one
 */
    public void replaceAll(Vector v) {
        diagramValues = v ;
    }
    
    
    public void createTableSize(int rows, int cols) {
        // Lets create a row
        String aLine = " " ;
        Vector v = new Vector() ;
        for (int i = 0 ; i< cols -1 ; i++) {
            aLine += "; " ;
        }
        this.log("aline:" + aLine ) ;
        
        for (int j = 0 ; j< rows  ; j++) {
            v.add(aLine) ;
        }
        
        this.add(v) ;
    }
    
    
} // DIAGRAMVALUES
