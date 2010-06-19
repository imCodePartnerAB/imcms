package imcode.util.image;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ByteArrayInputStreamHandler extends Thread {
    private static final Log log = LogFactory.getLog(ByteArrayInputStreamHandler.class);

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
