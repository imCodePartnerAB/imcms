package imcode.util.io;

import java.io.*;

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

    public File getFile() {
        return file;
    }

}
