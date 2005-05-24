package imcode.server.document;

public class DocumentReference extends DocumentId {

    private transient DocumentGetter documentGetter;

    public DocumentReference( int documentId, DocumentGetter documentMapper ) {
        super(documentId) ;
        this.documentGetter = documentMapper;
    }

    public DocumentDomainObject getDocument() {
        return documentGetter.getDocument( this ) ;
    }
}
