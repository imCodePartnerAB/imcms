package imcode.server.document;

import org.apache.commons.lang.NotImplementedException;

public class DirectDocumentReference extends DocumentReference {

    private DocumentDomainObject document;

    public DirectDocumentReference(DocumentDomainObject document) {
        super(document.getId());
        this.document = document ;
    }

    public DocumentDomainObject getDocument() {
        return document ;
    }
}
