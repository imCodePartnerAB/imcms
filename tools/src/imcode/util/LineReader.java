package imcode.util;

import java.io.Reader;
import java.io.IOException;

/**
 * @author kreiger
 */
public class LineReader {

    private Reader reader ;
    private int linesRead = 0 ;

    public LineReader(Reader reader) {
        this.reader = reader ;
    }

    public int getLinesRead() {
        return linesRead;
    }

    private char lastChar;

    public synchronized String readLine() throws IOException {
        StringBuffer line = new StringBuffer();
        int c;
        if ( 0 != lastChar ) {
            lastChar = 0;
            c = lastChar;
        } else {
            c = reader.read();
        }
        boolean lastWasCR = false;
        for ( ; -1 != c; c = reader.read() ) {
            if ( lastWasCR ) {
                lastWasCR = false;
                if ( -1 != c && '\n' != c ) {
                    lastChar = (char)c;
                    linesRead++ ;
                    return line.toString();
                }
            }
            line.append( (char)c );
            if ( '\r' == c ) {
                lastWasCR = true;
                continue;
            } else if ( '\n' == c ) {
                break;
            }
        }
        if ( line.length() > 0 ) {
            linesRead++ ;
            return line.toString();
        } else {
            return null;
        }
    }

}
