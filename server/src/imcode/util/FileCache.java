package imcode.util;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Collections;
import java.util.Map;

public class FileCache {

    private final int fileCacheSize = 100;
    private Map fileCache = Collections.synchronizedMap( new LRUMap( fileCacheSize ) );

    /** Fetch a file from the cache, if it hasn't changed on disk. */
    public String getCachedFileString( File file ) throws IOException {
        String cachedContents = getCachedFileStringIfRecent( file );
        if ( null == cachedContents ) {
            cachedContents = IOUtils.toString( new BufferedReader( new FileReader( file ) ) );
            cacheFile( file, cachedContents );
        }
        return cachedContents;
    }

    public void cacheFile( File file, String contents ) {
        fileCache.put( file, new Object[] {contents, new Long( System.currentTimeMillis() )} );
    }

    public String getCachedFileStringIfRecent( File file ) {
        Object[] file_and_date = (Object[])fileCache.get( file );
        if ( file_and_date != null && file.lastModified() <= ( (Long)file_and_date[1] ).longValue() ) {
            return (String)file_and_date[0];
        }
        return null;
    }

}
