package imcode.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileInputStreamSource implements InputStreamSource {

    private final File file;

    public FileInputStreamSource( File file ) {
        this.file = file;
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream( file );
    }

    public long getSize() throws IOException {
        return file.length() ;
    }
}
