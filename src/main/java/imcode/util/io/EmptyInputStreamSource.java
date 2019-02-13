package imcode.util.io;

import java.io.InputStream;

public class EmptyInputStreamSource implements InputStreamSource {
    public InputStream getInputStream() {
        return new EmptyInputStream();
    }

    public long getSize() {
        return 0;
    }
}
