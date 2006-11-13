package imcode.util;

import imcode.server.Imcms;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.io.CopyUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

public class CachingFileLoader {

    private static final int FILE_CACHE_SIZE = 100;

    private Map fileCache = Collections.synchronizedMap( new LRUMap( FILE_CACHE_SIZE ) );

    /** Fetch a file from the cache, if it hasn't changed on disk. */
    public String getCachedFileString( File file ) throws IOException {
        String cachedContents = getCachedFileStringIfRecent( file );
        if ( null == cachedContents ) {
            cachedContents = decodeFile(file);
            cacheFile( file, cachedContents );
        }
        return cachedContents;
    }

    private String decodeFile(File file) throws IOException {
        Charset utf8Charset = Charset.forName(Imcms.UTF_8_ENCODING);
        Charset fallbackCharset = Charset.defaultCharset();
        if (fallbackCharset.equals(utf8Charset) || fallbackCharset.equals(Charset.forName(Imcms.ASCII_ENCODING))) {
            fallbackCharset = Charset.forName(Imcms.ISO_8859_1_ENCODING) ;
        }
        return decodeFile(file, utf8Charset, fallbackCharset);
    }

    private String decodeFile(File file, Charset charset, Charset fallbackCharset) throws IOException {
        return new FallbackDecoder(charset, fallbackCharset).decodeBytes(readFile(file), "file "+file.toString());
    }

    private byte[] readFile(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        CopyUtils.copy(fileInputStream, os);
        return os.toByteArray();
    }

    private void cacheFile( File file, String contents ) {
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
