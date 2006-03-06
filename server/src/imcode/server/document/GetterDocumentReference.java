package imcode.server.document;

import imcode.server.Imcms;

import java.io.ObjectInputStream;
import java.io.IOException;

import com.imcode.imcms.mapping.DocumentGetter;

public class GetterDocumentReference extends DocumentReference {

    private transient DocumentGetter documentGetter;

    public GetterDocumentReference( int documentId, DocumentGetter documentGetter ) {
        super(documentId) ;
        this.documentGetter = documentGetter;
    }

    public DocumentDomainObject getDocument() {
        return documentGetter.getDocument( new Integer(getDocumentId()) ) ;
    }

    public void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        documentGetter = Imcms.getServices().getDocumentMapper().getDocumentGetter() ;
        ois.defaultReadObject();
    }

}
