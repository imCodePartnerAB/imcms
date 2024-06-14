package imcode.util.image;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

public class StringInputStreamHandler extends Thread {
    private static final Logger log = LogManager.getLogger(StringInputStreamHandler.class);

    private InputStream inputStream;
    private String data;

    public StringInputStreamHandler(InputStream input) {
        inputStream = input;
    }

    @Override
    public void run() {
        try {
            data = IOUtils.toString(inputStream, "ISO-8859-1");
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
