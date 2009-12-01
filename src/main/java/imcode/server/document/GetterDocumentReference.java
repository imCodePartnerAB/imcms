package imcode.server.document;

import imcode.server.Imcms;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.imcode.imcms.mapping.DocumentMapper;

public class GetterDocumentReference extends DocumentReference {
	
    private transient DocumentMapper documentMapper;

    public GetterDocumentReference(int documentId) {
        super(documentId) ;
        this.documentMapper = Imcms.getServices().getDocumentMapper();
    }

    public DocumentDomainObject getDocument() {
		return documentMapper.getDocument(getDocumentId());
    }
    
    public void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        documentMapper = Imcms.getServices().getDocumentMapper();
        ois.defaultReadObject();
    }
}