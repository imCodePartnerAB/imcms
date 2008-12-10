package imcode.util;

import imcode.server.Imcms;
import org.apache.commons.lang.UnhandledException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

public class CollectingHttpServletResponse extends HttpServletResponseWrapper {

    StringWriter stringWriter = new StringWriter() ;
    PrintWriter printWriter = new PrintWriter( stringWriter ) ;

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ServletOutputStream servletOutputStream = new ServletOutputStream() {
        public void write( int b ) {
            byteArrayOutputStream.write( b );
        }
    };

    private boolean alreadyCalled ;

    public CollectingHttpServletResponse( HttpServletResponse response ) {
        super( response );
    }

    private void checkCalled() throws IOException {
        if ( alreadyCalled ) {
            throw new IOException( "getOutputStream() or getWriter() already called." );
        }
        alreadyCalled = true;
    }

    public PrintWriter getWriter() throws IOException {
        checkCalled();
        return printWriter ;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        checkCalled();
        return servletOutputStream;
    }

    public String toString() {
        try {
            servletOutputStream.flush();
            byteArrayOutputStream.flush();
            printWriter.flush();
            stringWriter.flush();
            if (byteArrayOutputStream.size() > 0) {
                return byteArrayOutputStream.toString(Imcms.DEFAULT_ENCODING ) ;
            } else {
                return stringWriter.toString();
            }
        } catch ( IOException e ) {
            throw new UnhandledException( e );
        }
    }
}
