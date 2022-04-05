package imcode.util.image;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

public class ByteArrayInputStreamHandler extends Thread {
    private static final Logger log = LogManager.getLogger(ByteArrayInputStreamHandler.class);

    private InputStream inputStream;
    private byte[] data;


    public ByteArrayInputStreamHandler(InputStream inputStream) {
        this.inputStream = inputStream;
    }


    @Override
    public void run() {
        try {
            data = IOUtils.toByteArray(inputStream);
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public byte[] getData() {
        return data;
    }
}
