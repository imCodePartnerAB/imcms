package imcode.server.document;

import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.mapping.DocumentGetter;

public class GetterDocumentReference extends DocumentReference {

    private DocumentGetter documentGetter;

    public GetterDocumentReference(int documentId, DocumentGetter documentGetter) {
        super(documentId);
        this.documentGetter = documentGetter;
    }

    public DocumentDomainObject getDocument() {
        return documentGetter.getDefaultDocument(getDocumentId());
    }
}