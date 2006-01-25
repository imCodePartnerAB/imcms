package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.*;

public class BatchDocumentGetter extends DocumentGetterWrapper {

    private final Set documentIds;
    private Map documentsMap ;

    BatchDocumentGetter(Set documentIds, DocumentGetter documentGetter) {
        super(documentGetter);
        this.documentIds = documentIds;
    }

    public DocumentDomainObject getDocument(Integer documentId) {
        if (null == documentsMap || !documentIds.contains(documentId)) {
            documentIds.add(documentId) ;
            documentsMap = new HashMap();
            List documents = super.getDocuments(documentIds);
            for ( Iterator iterator = documents.iterator(); iterator.hasNext(); ) {
                DocumentDomainObject document = (DocumentDomainObject) iterator.next();
                documentsMap.put(new Integer(document.getId()), document) ;
            }
        }
        return (DocumentDomainObject) documentsMap.get(documentId) ;
    }
}
