package imcode.server.document;

public class DocumentReference extends DocumentId {

    private transient DocumentGetter documentGetter;

    public DocumentReference( int documentId, DocumentGetter documentGetter ) {
        super(documentId) ;
        this.documentGetter = documentGetter;
    }

    public DocumentDomainObject getDocument() {
        return documentGetter.getDocument( this ) ;
    }
}
