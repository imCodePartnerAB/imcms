package imcode.util.io;

import java.io.IOException;
import java.io.InputStream;

public class ExceptionFreeInputStreamSource extends InputStreamSourceWrapper {

    public ExceptionFreeInputStreamSource(InputStreamSource inputStreamSource) {
        super(inputStreamSource);
    }

    public InputStream getInputStream() {
        try {
            return super.getInputStream();
        } catch(IOException ioe) {
            return new EmptyInputStream() ;
        }
    }

    public long getSize() {
        try {
            return super.getSize();
        } catch(IOException ioe) {
            return 0 ;
        }
    }
}
