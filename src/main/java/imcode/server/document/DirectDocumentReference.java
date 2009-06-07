package imcode.server.document;

import org.apache.commons.lang.NotImplementedException;

import com.imcode.imcms.api.DocumentVersionSelector;

public class DirectDocumentReference extends DocumentReference {

    private DocumentDomainObject document;

    public DirectDocumentReference(DocumentDomainObject document) {
        super(document.getId());
        this.document = document ;
    }

    public DocumentDomainObject getDocument() {
        return document ;
    }
    
    public DocumentDomainObject getDocument(DocumentVersionSelector versionSelector) {
    	throw new NotImplementedException("Not implemented");
    }
}
