package imcode.util.io;

import java.io.IOException;
import java.io.InputStream;

public class EmptyInputStreamSource implements InputStreamSource {
    public InputStream getInputStream( ) throws IOException {
        return new EmptyInputStream( );
    }

    public long getSize( ) throws IOException {
        return 0;
    }
}
