package imcode.server.document.index;

public interface DirectoryIndex extends DocumentIndex {

    boolean isInconsistent();

    void delete();
}
