package imcode.external.diverse ;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class ReadTextFile {
    
    public synchronized static String getFile(String theFile) {
        String retStr = "" ;
        try {
            String fileLine ;
            String tempStr ;
            
            
            // Get the  file specified by InputFile
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(theFile)));
            //while there are still lines in the file, get-em.
            while((fileLine = br.readLine())!= null) {
                //add each line to the vector, each line will have a CRLF
                tempStr = fileLine.trim() + "\n" ;
                if (tempStr.length() > 0)
                    retStr += tempStr ;
            }
            
            //IMPORTANT!!!! - CLOSE THE STREAM!!!!!
            br.close();
        }	catch(IOException e)		{
            String msg = "An error occurred reading the file" + e.getMessage() + "\n" ;
            System.out.println(msg);
        }
        
        return retStr ;
        
    }
    
        /**
         *  Helper method for logging message to the console.
         */
    protected void log(Object msg) {
        
        System.out.println("SettingsDataAccessor: " + msg);
    }
    
    
} // End of class



/*
        public synchronized static String getFile(String theFile) {
                String retStr = "" ;
          try {
        String fileLine ;
                String tempStr ;
 
 
                        // Get the  file specified by InputFile
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(theFile)));
                        //while there are still lines in the file, get-em.
                while((fileLine = br.readLine())!= null) {
                                //add each line to the vector, each line will have a CRLF
                                tempStr = fileLine.trim() + "\n" ;
                                if (tempStr.length() > 0)
                                retStr += tempStr ;
        }
 
      //IMPORTANT!!!! - CLOSE THE STREAM!!!!!
                        br.close();
      }	catch(IOException e)		{
                 String msg = "An error occurred reading the file" + e.getMessage() + "\n" ;
                             System.out.println(msg);
        }
 
                        return retStr ;
 
        }
 
public synchronized void saveSettings(String str) {
                try {
                        writeSettings(str) ;
 
                }	catch (FileNotFoundException exc) {
                        log("Could not find the file \"" + FILE_NAME + "\".");
                        log("Make sure it is in the current directory.") ;
                        log(exc);
 
                }	catch (IOException exc) {
                        log("IO error occurred while reading file: " + FILE_NAME);
                        log(exc);
                }
        }
 
protected synchronized void writeSettings(String str) throws IOException {
                try {
 
                this.checkSettings() ;
                        log("Doing save...");
                        FileWriter myFileWriter = new FileWriter(FILE_NAME, false);
 
                        // create a print writer based on fileWriter and set autoflush to true
                        boolean autoFlush = true;
                        PrintWriter outputToFile = new PrintWriter(myFileWriter, autoFlush);
 
                        outputToFile.println(str) ;
                        outputToFile.close() ;
 
                }	catch (IOException exc) {
                        log("Error occurred during the save.");
                        log(exc);
                }
 
        }
 */

