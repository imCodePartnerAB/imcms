package imcode.server.document;

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
