package imcode.server.document;

import imcode.server.Imcms;

import java.io.ObjectInputStream;
import java.io.IOException;

import com.imcode.imcms.mapping.DocumentGetter;

public class GetterDocumentReference extends DocumentReference {

    private transient DocumentGetter documentGetter;
    private DocumentDomainObject document ;

    public GetterDocumentReference( int documentId, DocumentGetter documentGetter ) {
        super(documentId) ;
        this.documentGetter = documentGetter;
    }

    public DocumentDomainObject getDocument() {
        if (null == document) {
            document = documentGetter.getDocument( new Integer(getDocumentId()) ) ;
        }
        return document;
    }

    public void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        documentGetter = Imcms.getServices().getDocumentMapper().getDocumentGetter() ;
        ois.defaultReadObject();
    }

}
