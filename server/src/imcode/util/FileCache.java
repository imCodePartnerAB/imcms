package imcode.util ;

import java.io.* ;
import org.apache.oro.util.* ;
import imcode.util.log.* ;

public class FileCache {

    private final int m_FileCacheSize = 50 ;
    private CacheLRU fileCache = new CacheLRU(m_FileCacheSize) ;

    Log log = Log.getLog("server") ;

    /**
       Fetch a file from the cache, if it hasn't changed on disc.
    */
    /*
    synchronized private String getCachedFileString(String filename) throws IOException {
	return getCachedFileString(new File(filename)) ;
    }
    */

    protected StringBuffer loadFile(File file) {
	StringBuffer tempbuffer = new StringBuffer() ;
	try {
	    char[] charbuffer = new char[16384] ;
	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"8859_1"));
	    // Load the file
	    int chars_read = 0 ;
	    while (-1 < (chars_read = br.read(charbuffer))) {
		tempbuffer.append(charbuffer,0,chars_read) ;
	    }
	    br.close();
	} catch (IOException ex) {
	    log.log(Log.ERROR, "File not found during parsing.", ex) ;
	    tempbuffer.append(ex.getMessage()) ;
	}
	return tempbuffer ;
    }


    /**
       Fetch a file from the cache, if it hasn't changed on disc.
    */
    public synchronized String getCachedFileString(File file) throws IOException {
	    
	if (m_FileCacheSize > 0) {
	    Object[] file_and_date = (Object[])(fileCache.getElement(file)) ; // Get the cached file, if any.
	    if (file_and_date == null || file.lastModified() > ((Long)file_and_date[1]).longValue() ) {
		// No (new) file found?
		String temp = loadFile(file).toString() ; // Load it.
		fileCache.addElement(file, new Object[] {temp,new Long(System.currentTimeMillis())}) ;  // Cache it.
		return temp ;
	    }
	    return (String)file_and_date[0] ;
	} else {
	    return loadFile(file).toString() ;
	}
    }
}
