package imcode.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;

public interface InputStreamSource extends Serializable {

    public InputStream getInputStream() throws IOException;

}