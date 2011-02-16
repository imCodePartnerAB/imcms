package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;
import imcode.util.ShouldNotBeThrownException;
import java.util.ArrayList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachingDocumentGetter extends DocumentGetterWrapper {

    private Map cache;

    public CachingDocumentGetter(DocumentGetter documentGetter, Map cache) {
        super(documentGetter);
        this.cache = cache ;
    }

    public DocumentDomainObject getDocument(Integer documentId) {
        DocumentDomainObject document = (DocumentDomainObject) cache.get(documentId) ;
        if (null == document) {
            document = super.getDocument(documentId) ;
            
            if (null == document) {
                return null ;
            }
            
            cache.put(documentId, document) ;
        }
        
        
        try {
            document = (DocumentDomainObject) document.clone() ;
        } catch ( CloneNotSupportedException e ) {
            throw new ShouldNotBeThrownException(e);
        }
        
        return document;
    }

    public List getDocuments(Collection documentIds) {
        Map<Integer, DocumentDomainObject> docsMap = new HashMap<Integer, DocumentDomainObject>();
        List<Integer> missingIds = new ArrayList<Integer>();

        for (Integer docId : (Collection<Integer>) documentIds) {
            DocumentDomainObject doc = (DocumentDomainObject) cache.get(docId);

            if (doc == null) {
                missingIds.add(docId);
            } else {
                try {
                    doc = (DocumentDomainObject) doc.clone();
                } catch (CloneNotSupportedException ex) {
                    throw new ShouldNotBeThrownException(ex);
                }

                docsMap.put(docId, doc);
            }
        }

        boolean allMissing = docsMap.isEmpty();
        List<DocumentDomainObject> missingDocs = super.getDocuments(missingIds);

        for (DocumentDomainObject doc : missingDocs) {
            cache.put(doc.getId(), doc);

            if (!allMissing) {
                docsMap.put(doc.getId(), doc);
            }
        }
        
        if (allMissing) {
            return missingDocs;
        }

        List<DocumentDomainObject> docs = new ArrayList(documentIds.size());

        for (Integer docId : (Collection<Integer>) documentIds) {
            DocumentDomainObject doc = docsMap.get(docId);

            if (doc != null) {
                docs.add(doc);
            }
        }

        return docs;
    }
    
}
