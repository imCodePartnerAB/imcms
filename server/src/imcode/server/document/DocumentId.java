package imcode.server.document;

import java.io.Serializable;

public class DocumentId implements Serializable {
    protected int documentId;

    public DocumentId(int documentId) {
        if ( DocumentDomainObject.ID_NEW >= documentId ) {
            throw new IllegalArgumentException( "Bad document id." );
        }
        this.documentId = documentId ;
    }

    public int intValue() {
        return documentId;
    }

    public String toString() {
        return ""+documentId ;
    }

    public boolean equals( Object obj ) {
        return obj instanceof DocumentId && ((DocumentId)obj).documentId == documentId ;
    }

    public int hashCode() {
        return documentId ;
    }
}
