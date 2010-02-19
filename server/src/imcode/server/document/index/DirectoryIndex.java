package imcode.server.document.index;

import org.apache.lucene.store.Directory;

public interface DirectoryIndex extends DocumentIndex {

    boolean isInconsistent();

    void delete();

    Directory getDirectory();
}