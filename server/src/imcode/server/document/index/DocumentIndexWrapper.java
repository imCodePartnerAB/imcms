package imcode.server.document.index;

import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.IndexException;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.lucene.search.Query;

public class DocumentIndexWrapper implements DocumentIndex {

    private final DocumentIndex index;

    public DocumentIndexWrapper(DocumentIndex index) {
        this.index = index ;
    }

    public void indexDocument(DocumentDomainObject document) throws IndexException {
        index.indexDocument(document);
    }

    public void rebuild() throws IndexException {
        index.rebuild();
    }

    public void removeDocument(DocumentDomainObject document) throws IndexException {
        index.removeDocument(document);
    }

    public DocumentDomainObject[] search(Query query, UserDomainObject searchingUser) throws IndexException {
        return index.search(query, searchingUser) ;
    }
}
