package imcode.server.document;

import com.imcode.imcms.mapping.DocumentGetter;
import imcode.server.Imcms;

import java.io.IOException;
import java.io.ObjectInputStream;

public class GetterDocumentReference extends DocumentReference {

    private transient DocumentGetter documentGetter;

    public GetterDocumentReference(int documentId, DocumentGetter documentGetter) {
        super(documentId);
        this.documentGetter = documentGetter;
    }

    public DocumentDomainObject getDocument() {
        return documentGetter.getDocument(getDocumentId());
    }

    public void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        documentGetter = Imcms.getServices().getDocumentMapper().getDocumentGetter();
        ois.defaultReadObject();
    }

}
