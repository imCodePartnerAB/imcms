package imcode.server.document;

import imcode.server.Imcms;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.commons.lang.NotImplementedException;

import com.imcode.imcms.api.DocumentVersionSelector;
import com.imcode.imcms.mapping.DocumentGetter;

/**
 * Document reference using document getter.
 * 
 * Prototype implementation.
 */
public class GetterDocumentReference extends DocumentReference {
	
    private transient DocumentGetter documentGetter;

    public GetterDocumentReference(int documentId, DocumentGetter documentGetter) {
        super(documentId) ;
        this.documentGetter = documentGetter;
    }

    public DocumentDomainObject getDocument() {
		return documentGetter.getDocument(getDocumentId());
    }
    
    public DocumentDomainObject getDocument(DocumentVersionSelector versionSelector) {
    	return versionSelector.getDocument(documentGetter, getDocumentId());
    }    

    public void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        documentGetter = Imcms.getServices().getDocumentMapper().getDocumentGetter() ;
        ois.defaultReadObject();
    }
}