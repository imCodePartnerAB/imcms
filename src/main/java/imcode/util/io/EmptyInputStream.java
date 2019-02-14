package imcode.util.io;

import java.io.InputStream;

public class EmptyInputStream extends InputStream {
    public int read() {
        return -1;
    }
}
