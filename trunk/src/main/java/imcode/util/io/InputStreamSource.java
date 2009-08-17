package imcode.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public interface InputStreamSource extends Serializable {

    InputStream getInputStream() throws IOException;

    long getSize() throws IOException ;

}