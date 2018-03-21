package imcode.server.document.index;

import com.imcode.imcms.api.SearchResult;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;

import java.util.List;

public class DocumentIndexWrapper implements DocumentIndex {

    private final DocumentIndex index;

    public DocumentIndexWrapper(DocumentIndex index) {
        this.index = index;
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

    public List<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser) throws IndexException {
        return index.search(query, searchingUser);
    }

    @Override
    public SearchResult<DocumentDomainObject> search(DocumentQuery query, UserDomainObject searchingUser, int startPosition, int maxResults) throws IndexException {
        return index.search(query, searchingUser, startPosition, maxResults);
    }
}
