package imcode.server.document.index;

public interface DirectoryIndex extends DocumentIndex {

    boolean isInconsistent() throws IndexException;

    void delete() throws IndexException;
}