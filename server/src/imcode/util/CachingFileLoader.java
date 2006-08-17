package imcode.util;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.io.CopyUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.CharacterCodingException;
import java.nio.*;

import imcode.server.Imcms;

public class CachingFileLoader {

    private static final int FILE_CACHE_SIZE = 100;
    private static final Logger LOG = Logger.getLogger(CachingFileLoader.class);

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
        if (fallbackCharset.equals(utf8Charset)) {
            fallbackCharset = Charset.forName(Imcms.ISO_8859_1_ENCODING) ;
        }
        return decodeFile(file, utf8Charset, fallbackCharset);
    }

    private String decodeFile(File file, Charset charset, Charset fallbackCharset) throws IOException {
        byte[] inputBytes = readFile(file);
        LOG.trace("Read "+inputBytes.length+" bytes of "+file);
        String result;
        try {
            result = createReportingDecoder(charset).decode(ByteBuffer.wrap(inputBytes)).toString();
            logCharactersDecoded(inputBytes, result, charset, file);
        } catch ( CharacterCodingException e1 ) {
            try {
                LOG.debug("Failed to decode file "+file+" using "+charset+", falling back to "+fallbackCharset+".") ;
                result = createReportingDecoder(fallbackCharset).decode(ByteBuffer.wrap(inputBytes)).toString();
                logCharactersDecoded(inputBytes, result, fallbackCharset, file);
            } catch ( CharacterCodingException e2 ) {
                LOG.warn("Failed to decode file "+file+" using "+charset +" and "+fallbackCharset +", using broken "+charset+" result.", e2);
                result = charset.decode(ByteBuffer.wrap(inputBytes)).toString() ;
            }
        }
        return result;
    }

    private void logCharactersDecoded(byte[] inputBytes, String result, Charset charset, File file) {
        LOG.trace("Decoded "+inputBytes.length+" bytes using "+charset+" to "+result.length()+" characters of "+file) ;
    }

    private CharsetDecoder createReportingDecoder(Charset charset) {
        return charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT) ;
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
