package imcode.server.document;

public class DocumentReference  {

    private DocumentMapper documentMapper;
    private int documentId;

    public DocumentReference( int documentId, DocumentMapper documentMapper ) {
        if (DocumentDomainObject.ID_NEW >= documentId) {
            throw new IllegalArgumentException( "Bad document id." ) ;
        }
        this.documentId = documentId ;
        this.documentMapper = documentMapper;
    }

    public DocumentDomainObject getDocument() {
        return documentMapper.getDocument( documentId );
    }

    public int getDocumentId() {
        return documentId;
    }
}
