package imcode.util;

import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.ByteBuffer;

public class FallbackDecoder {

    private Charset charset;
    private Charset fallbackCharset;

    private final static Logger LOG = Logger.getLogger(FallbackDecoder.class);
    
    public FallbackDecoder(Charset charset, Charset fallbackCharset) {
        this.charset = charset;
        this.fallbackCharset = fallbackCharset;
    }

    public String decodeBytes(byte[] inputBytes,
                              String sourceName) {
        if ( 0 == inputBytes.length ) {
            return "" ;
        }
        String result;
        try {
            result = createReportingDecoder(charset).decode(ByteBuffer.wrap(inputBytes)).toString();
        } catch ( CharacterCodingException e1 ) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("Failed to decode " + sourceName + " using " + charset + ", falling back to "
                          + fallbackCharset + ".");
            }
            try {
                result = createReportingDecoder(fallbackCharset).decode(ByteBuffer.wrap(inputBytes)).toString();
            } catch ( CharacterCodingException e2 ) {
                LOG.warn("Failed to decode "+sourceName +" using "+charset +" and "+fallbackCharset +", using broken "+charset+" result.", e2);
                result = charset.decode(ByteBuffer.wrap(inputBytes)).toString() ;
            }
        }
        return result;
    }

    private static CharsetDecoder createReportingDecoder(Charset charset) {
        return charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT) ;
    }

    public Charset getFallbackCharset() {
        return fallbackCharset;
    }
}
