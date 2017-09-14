package imcode.server.document;

import com.imcode.imcms.mapping.DocumentGetter;
import imcode.server.Imcms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class GetterDocumentReference extends DocumentReference implements Serializable {

    transient private DocumentGetter documentGetter;

    public GetterDocumentReference(int documentId, DocumentGetter documentGetter) {
        super(documentId);
        this.documentGetter = documentGetter;
    }

    public DocumentDomainObject getDocument() {
        return documentGetter.getDocument(getDocumentId());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        documentGetter = Imcms.getServices().getDocumentMapper();
    }
}