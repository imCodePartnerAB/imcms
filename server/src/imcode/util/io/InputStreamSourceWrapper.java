package imcode.util.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamSourceWrapper implements InputStreamSource {
    private InputStreamSource inputStreamSource ;

    public InputStreamSourceWrapper(InputStreamSource inputStreamSource) {
        this.inputStreamSource = inputStreamSource;
    }

    public long getSize() throws IOException {
        return inputStreamSource.getSize();
    }

    public InputStream getInputStream() throws IOException {
        return inputStreamSource.getInputStream();
    }
}
