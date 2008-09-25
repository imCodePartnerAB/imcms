package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.util.ShouldNotBeThrownException;

import java.util.Collection;
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
        return super.getDocuments(documentIds) ;
    }
    
}
