package imcode.util.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamSourceWrapper implements InputStreamSource {

    private final InputStreamSource inputStreamSource ;

    public InputStreamSourceWrapper(InputStreamSource inputStreamSource) {
        this.inputStreamSource = inputStreamSource;
    }

    public long getSize() throws IOException {
        return inputStreamSource.getSize();
    }

    public InputStream getInputStream() throws IOException {
        return inputStreamSource.getInputStream();
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || !(o instanceof InputStreamSource) ) {
            return false;
        }
        InputStreamSource otherInputStreamSource = (InputStreamSource) o ;
        while (o instanceof InputStreamSourceWrapper) {
            otherInputStreamSource = ((InputStreamSourceWrapper)o).getInputStreamSource() ;
        }

        return inputStreamSource.equals(otherInputStreamSource);
    }

    public int hashCode() {
        return inputStreamSource.hashCode();
    }

    public InputStreamSource getInputStreamSource() {
        return inputStreamSource;
    }
}
