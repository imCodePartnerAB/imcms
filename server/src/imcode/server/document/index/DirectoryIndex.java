package imcode.server.document.index;

import java.io.Closeable;

public interface DirectoryIndex extends DocumentIndex, Closeable {

    boolean isInconsistent();

    void delete();

    void close();
}
