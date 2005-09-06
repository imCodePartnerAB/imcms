package imcode.server.document;

import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.IndexException;
import imcode.server.user.UserDomainObject;
import org.apache.lucene.search.Query;

public class MockDocumentIndex implements DocumentIndex {

    public void indexDocument( DocumentDomainObject document ) throws IndexException {
    }

    public void removeDocument( DocumentDomainObject document ) throws IndexException {
    }

    public DocumentDomainObject[] search( Query query, UserDomainObject searchingUser ) throws IndexException {
        return new DocumentDomainObject[0];
    }

    public void rebuild() {
    }
}
