package imcode.external.diverse ;
import java.util.*;
import java.io.*;
import java.lang.* ;

public class FileManager{
    
/**
 * Deletes a file from hd
 **/
    
    public boolean fileDelete(String thePath, String theFile) {
        return this.fileDelete(thePath + theFile) ;
    }
    
/**
 * Deletes a file from hd
 **/
    
    public boolean fileDelete(String theFile) {
        boolean ok = false ;
        try {
            File fileObj = new File(theFile) ;
            if(fileObj.exists())
                ok = fileObj.delete() ;
            else {
                // log("File not found! " + theFile) ;
            }
            
        } catch (NullPointerException e) {
            log( "The file couldnt be deleted " + e.getMessage()) ;
        }
        catch (Exception e) {
            log("The file couldnt be deleted " + e.getMessage()) ;
        }
        
        return ok ;
    }	// end filedelete
    
        /**
         * Checks out if a file exists on hd
         **/
    
    public boolean fileExists(String thePath, String theFile) {
        return this.fileExists(thePath + theFile) ;
    }
    
        /**
         * Checks out if a file exists on hd
         **/
    
    public boolean fileExists(String theFile) {
        boolean ok = false ;
        try {
            File fileObj = new File( theFile) ;
            if(fileObj.exists())
                ok = true ;
            else {
                // log("FileExists. File not found! " + theFile) ;
            }
            
        } catch (NullPointerException e) {
            log("The file couldnt be found " + e.getMessage()) ;
        }
        catch (Exception e) {
            log("The file couldnt be found " + e.getMessage()) ;
        }
        
        return ok ;
    }	// end fileExists
    
    
/**
 * Creates a file on hd
 **/
    
    public boolean createFile(String thePath, String newFile) {
        boolean ok = false ;
        try {
            File fileObj = new File(thePath + newFile) ;
            // Lets create a new file
            ok = fileObj.createNewFile() ;
            if(ok != true)
                log("The file could not be created!" + thePath + newFile) ;
        } catch (NullPointerException e) {
            log("The file couldnt be deleted " + e.getMessage()) ;
        }
        catch (Exception e) {
            log("The file couldnt be deleted " + e.getMessage()) ;
        }
        
        return ok ;
    }	// end filedelete
    
    
/**
 * Copies a directory with textfiles. Returns 0 if the operation was ok. Otherwise
 * the errorcode.
 **/
    
    public int copyDirectory(String srcPath, String targPath) {
        boolean okFlag = false ;
        boolean fileNotFoundFlag = false ;
        String tmpFile = "" ;
        
        try {
            File fileObj = new File(srcPath) ;
            File targetObj = new File(targPath) ;
            
            // Lets check if the directory exists, if not, then create the target directory
            
            if( !targetObj.exists() ) {
                okFlag = targetObj.mkdir() ;
                if(okFlag == false) {
                    log("The target directory could not be created") ;
                    //return -10 ;
                }
            }
            String[] fileList = fileObj.list() ;
            
            // Lets loop through the fileList and copy the files
            for(int i = 0 ; i < fileList.length ; i++) {
                tmpFile = fileList[i] ;
                //log("TmpFile: " + tmpFile) ;
                okFlag = writeFileCopy2(srcPath + tmpFile, targPath + tmpFile) ;
                // log("Såhär gick det") ;
            }
            
        } catch (NullPointerException e) {
            log("No such path exists: " + srcPath) ;
            return -1 ;
            
        }	catch (FileNotFoundException exc) {
            log("Could not find the file \"" + srcPath + tmpFile + "\".");
            log("Make sure it is in the current directory.") ;
            log("" + exc);
            return -2 ;
            
        }	catch (IOException exc) {
            log("IO error occurred while reading file: " + targPath + tmpFile);
            log("" + exc);
            return -3 ;
        }
        return 0 ;
    }
    
    
    
/**
 * Copies a textfile
 **/
    
    public boolean copyFile(String srcPath, String srcFile, String targPath, String targFile) {
        boolean okFlag = false ;
        boolean fileNotFoundFlag = false ;
        
        try {
            if( this.fileExists(srcPath + srcFile))
                okFlag = writeFileCopy2(srcPath + srcFile, targPath + targFile) ;
            
        }	catch (FileNotFoundException exc) {
            log("Could not find the file \"" + srcPath + srcFile + "\".");
            log("Make sure it is in the current directory.") ;
            log("" + exc);
            return false ;
            
        }	catch (IOException exc) {
            log("IO error occurred while reading file: " + targPath + targFile);
            log("" + exc);
            return false ;
        }
        return okFlag ;
    }
    
/**
 * Helper method for copying a textfile
 **/
    
    public boolean writeFileCopy(String srcFile, String targetFile)
    throws IOException {
        
        boolean ok = false ;
        try	{
            
            // Lets open the valuefile
            log("Copying File: " + srcFile + " to " +  targetFile + "...") ;
            BufferedReader inputFromFile = new BufferedReader(new FileReader(srcFile));
            
            // create a file writer for the target file and set append to true
            //	boolean append = true;
            FileWriter myFileWriter = new FileWriter(targetFile, false);
            
            // create a print writer based on fileWriter and set autoflush to true
            boolean autoFlush = true;
            PrintWriter outputToFile = new PrintWriter(myFileWriter, autoFlush);
            
            String aLine;
            while ((aLine = inputFromFile.readLine()) != null ) {
                outputToFile.println(aLine) ;
            }
            
            inputFromFile.close();
            outputToFile.close() ;
            
        }	catch (IOException exc) {
            log("Error occurred during the save.");
            log("" + exc);
            ok = false ;
        }
        
        ok = true ;
        return ok ;
    } // writeFileCopy
    
    
/**
 * Helper method for copying a file. Copies both a textfile and raw files like
 * bitmaps, programs etc
 **/
    
    public boolean writeFileCopy2(String srcFile, String targetFile)
    throws IOException {
        
        boolean ok = false ;
        try	{
            
            // Lets open the valuefile
            // log("Copying File: " + srcFile + " to " +  targetFile + "...") ;
            FileInputStream inStream = new FileInputStream(srcFile) ;
            
            // create a file writer for the target file and set append to true	boolean append = true;
            FileOutputStream outStream = new FileOutputStream(targetFile, false) ;
            byte inArr[] = new byte[inStream.available()] ;
            
            inStream.read(inArr) ;
            outStream.write(inArr) ;
            inStream.close() ;
            outStream.close() ;
            
        }	catch (IOException exc) {
            log("Error occurred during the save.");
            log("" + exc);
            ok = false ;
        }
        
        ok = true ;
        return ok ;
    } // writeFileCopy
    
/**
 * Helper method for logging messages to the screen
 **/
    
    public void log(String msg){
        System.out.println("FileManager:" + msg ) ;
    }
    
    
} // end FileManager




