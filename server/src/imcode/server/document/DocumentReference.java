package imcode.server.document;

import java.util.Date;

public class DocumentReference  {

    private DocumentMapper documentMapper;
    private DocumentDomainObject document ;
    private Date time;
    private int documentId;

    public DocumentReference( int documentId, DocumentMapper documentMapper ) {
        if ( DocumentDomainObject.ID_NEW >= documentId ) {
            throw new IllegalArgumentException( "Bad document id." );
        }
        this.documentId = documentId ;
        this.documentMapper = documentMapper;
    }

    public DocumentReference( DocumentDomainObject document, DocumentMapper documentMapper ) {
        this(document.getId(), documentMapper);
        setDocument( document );
    }

    private void setDocument( DocumentDomainObject document ) {
        this.document = document ;
        time = new Date();
    }

    public DocumentDomainObject getDocument() {
        if ( null == document || null == time || documentMapper.hasNewerDocument( documentId, time ) ) {
            initDocument();
        }
        return document ;
    }

    private void initDocument() {
        setDocument( documentMapper.getDocument( documentId ) );
    }

    Date getTime() {
        if (null == time) {
            initDocument();
        }
        return time ;
    }
}
