package imcode.external.diverse ;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager{

    /**
 * Copies a directory with textfiles. Returns 0 if the operation was ok. Otherwise
 * the errorcode.
 **/
    
    public int copyDirectory(File srcPath, File targPath) {
        boolean okFlag = false ;
        String tmpFile = "" ;
        
        try {
            File fileObj = srcPath ;
            File targetObj = targPath ;
            
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
                okFlag = writeFileCopy2(new File(srcPath , tmpFile).toString(), new File(targPath , tmpFile).toString()) ;
                // log("Såhär gick det") ;
            }
            
        } catch (NullPointerException e) {
            log("No such path exists: " + srcPath) ;
            return -1 ;
        }
        return 0 ;
    }

    /**
 * Helper method for copying a file. Copies both a textfile and raw files like
 * bitmaps, programs etc
 **/

    private boolean writeFileCopy2(String srcFile, String targetFile) {
        
        boolean ok = false ;
        try	{
            
            // Lets open the valuefile
            // log("Copying File: " + srcFile + " to " +  targetFile + "...") ;
            FileInputStream inStream = new FileInputStream(srcFile) ;
            
            // create a file writer for the target file and set append to true	boolean append = true;
            FileOutputStream outStream = new FileOutputStream(targetFile, false) ;
            byte[] inArr = new byte[inStream.available()] ;
            
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

private void log(String msg){
        System.out.println("FileManager:" + msg ) ;
    }
    
    
} // end FileManager




