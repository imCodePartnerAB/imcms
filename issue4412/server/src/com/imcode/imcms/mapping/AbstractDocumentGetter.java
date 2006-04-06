package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.*;

public class AbstractDocumentGetter implements DocumentGetter {

    public List getDocuments(Collection documentIds) {
        List documents = new ArrayList(documentIds.size()) ;
        for ( Iterator iterator = documentIds.iterator(); iterator.hasNext(); ) {
            Integer documentId = (Integer) iterator.next();
            DocumentDomainObject document = getDocument(documentId);
            if (null != document) {
                documents.add(document) ;
            }
        }
        return documents;
    }

    public DocumentDomainObject getDocument(Integer documentId) {
        List documents = getDocuments(Collections.singletonList(documentId));
        if (documents.isEmpty()) {
            return null ;
        }
        return (DocumentDomainObject) documents.get(0);
    }
}
