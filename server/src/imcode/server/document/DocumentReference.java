package imcode.server.document;

import imcode.server.Imcms;

import java.io.Serializable;

public class DocumentReference implements Serializable {

    private transient DocumentMapper documentMapper;
    private int documentId;

    public DocumentReference( int documentId, DocumentMapper documentMapper ) {
        if ( DocumentDomainObject.ID_NEW >= documentId ) {
            throw new IllegalArgumentException( "Bad document id." );
        }
        this.documentId = documentId ;
        this.documentMapper = documentMapper;
    }

    public DocumentDomainObject getDocument() {
        if (null == documentMapper) {
            documentMapper = Imcms.getServices().getDocumentMapper() ;
        }
        return documentMapper.getDocument( documentId ) ;
    }

    public boolean equals( Object obj ) {
        return obj instanceof DocumentReference && ((DocumentReference)obj).documentId == documentId ;
    }

    public int hashCode() {
        return documentId ;
    }

    public int getDocumentId() {
        return documentId;
    }
}
