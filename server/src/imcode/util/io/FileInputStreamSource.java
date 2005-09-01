package imcode.util.io;

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

    public File getFile() {
        return file;
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final FileInputStreamSource that = (FileInputStreamSource) o;

        return file.equals(that.file);
    }

    public int hashCode() {
        return file.hashCode();
    }

}
