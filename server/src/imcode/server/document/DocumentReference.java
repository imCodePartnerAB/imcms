package imcode.server.document;

import java.util.Date;

public class DocumentReference  {

    private DocumentMapper documentMapper;
    private DocumentDomainObject document ;
    private Date time;

    public DocumentReference( DocumentDomainObject document, DocumentMapper documentMapper ) {
        if ( DocumentDomainObject.ID_NEW >= document.getId() ) {
            throw new IllegalArgumentException( "Bad document id." );
        }
        this.documentMapper = documentMapper;
        setDocument( document );
    }

    private void setDocument( DocumentDomainObject document ) {
        this.document = document ;
        time = new Date();
    }

    public DocumentDomainObject getDocument() {
        if ( documentMapper.hasNewerDocument( document.getId(), time ) ) {
            setDocument( documentMapper.getDocument( document.getId() ) );
        }
        return document ;
    }

    Date getTime() {
        return time;
    }
}
