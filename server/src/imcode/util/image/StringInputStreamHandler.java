package imcode.util.image;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StringInputStreamHandler extends Thread {
    private static final Log log = LogFactory.getLog(StringInputStreamHandler.class);

    private InputStream inputStream;
    private String data;

    public StringInputStreamHandler(InputStream input) {
        inputStream = input;
    }

    @Override
    public void run() {
        try {
            data = IOUtils.toString(inputStream);
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public String getData() {
        return data;
    }
}
