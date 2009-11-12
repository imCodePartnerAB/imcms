package imcode.server.document;

import imcode.server.Imcms;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.imcode.imcms.api.DocumentVersionSelector;
import com.imcode.imcms.mapping.DocumentGetter;
import com.imcode.imcms.mapping.DocumentMapper;

/**
 * Document reference using document getter.
 * 
 * Prototype implementation.
 */
public class GetterDocumentReference extends DocumentReference {
	
    private transient DocumentMapper documentMapper;
    
    private transient DocumentVersionSelector versionSelector;

    public GetterDocumentReference(int documentId, DocumentVersionSelector versionSelector) {
        super(documentId) ;
        this.documentMapper = Imcms.getServices().getDocumentMapper();
        this.versionSelector = versionSelector;
    }

    public DocumentDomainObject getDocument() {
		return versionSelector.getDocument(documentMapper, getDocumentId());
    }
    
    public void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        documentMapper = Imcms.getServices().getDocumentMapper();
        ois.defaultReadObject();
    }
}