package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;

public class DocumentGetterWrapper implements DocumentGetter {

    private DocumentGetter documentGetter;

    public DocumentGetterWrapper(DocumentGetter documentGetter) {
        this.documentGetter = documentGetter;
    }

    public List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds) {
        return documentGetter.getDocuments(documentIds);
    }

    public DocumentDomainObject getDocument(Integer documentId) {
        return documentGetter.getDocument(documentId);
    }
}
