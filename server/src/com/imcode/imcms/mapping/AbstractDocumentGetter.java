package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AbstractDocumentGetter implements DocumentGetter {

    public List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds) {
        List<DocumentDomainObject> documents = new ArrayList<>(documentIds.size());
        for (Integer documentId : documentIds) {
            DocumentDomainObject document = getDocument(documentId);
            if (null != document) {
                documents.add(document);
            }
        }
        return documents;
    }

    public DocumentDomainObject getDocument(Integer documentId) {
        List<DocumentDomainObject> documents = getDocuments(Collections.singletonList(documentId));
        if (documents.isEmpty()) {
            return null;
        }
        return documents.get(0);
    }
}
