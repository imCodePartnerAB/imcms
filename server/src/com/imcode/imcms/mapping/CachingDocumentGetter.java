package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;
import imcode.util.ShouldNotBeThrownException;

import java.util.*;

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
            return (DocumentDomainObject) document.clone() ;
        } catch ( CloneNotSupportedException e ) {
            throw new ShouldNotBeThrownException(e);
        }
    }

    public List getDocuments(Collection documentIds) {
        return super.getDocuments(documentIds) ;
    }
    
}
