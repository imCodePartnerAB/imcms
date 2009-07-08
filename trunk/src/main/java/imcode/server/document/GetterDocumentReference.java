package imcode.server.document;

import imcode.server.Imcms;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.imcode.imcms.api.DocumentVersionSelector;
import com.imcode.imcms.mapping.DocumentGetter;

/**
 * Document reference using document getter.
 * 
 * Prototype implementation.
 */
public class GetterDocumentReference extends DocumentReference {
	
    private transient DocumentGetter documentGetter;
    
    private transient DocumentVersionSelector versionSelector;

    public GetterDocumentReference(int documentId, DocumentGetter documentGetter, DocumentVersionSelector versionSelector) {
        super(documentId) ;
        this.documentGetter = documentGetter;
        this.versionSelector = versionSelector;
    }

    public DocumentDomainObject getDocument() {
		return versionSelector.getDocument(documentGetter, getDocumentId());
    }
    
    public void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        documentGetter = Imcms.getServices().getDocumentMapper().getDocumentGetter() ;
        ois.defaultReadObject();
    }
}