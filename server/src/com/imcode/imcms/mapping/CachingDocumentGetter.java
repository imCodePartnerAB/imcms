package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;
import imcode.util.ShouldNotBeThrownException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CachingDocumentGetter extends DocumentGetterWrapper {

    private Map<Integer, DocumentDomainObject> cache;

    public CachingDocumentGetter(DocumentGetter documentGetter, Map<Integer, DocumentDomainObject> cache) {
        super(documentGetter);
        this.cache = cache;
    }

    public DocumentDomainObject getDocument(Integer documentId, boolean renewCache) {
        DocumentDomainObject document = renewCache ? null : cache.get(documentId);
        if (null == document) {
            document = super.getDocument(documentId);
            if (null == document) {
                return null;
            }
            cache.put(documentId, document);
        }
        try {
            return (DocumentDomainObject) document.clone();
        } catch (CloneNotSupportedException e) {
            throw new ShouldNotBeThrownException(e);
        }
    }

    public DocumentDomainObject getDocument(Integer documentId) {
        return getDocument(documentId, false);
    }

    public List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds) {
        List<DocumentDomainObject> list = new ArrayList<>();
        for (Integer documentId : documentIds) {
            list.add(getDocument(documentId, false));
        }
        return list;
    }

}
