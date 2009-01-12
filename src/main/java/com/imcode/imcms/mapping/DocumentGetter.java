package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;

public interface DocumentGetter {
    
    /** Return a list of published documents <em>in the same order</em> as the documentIds */ 
    List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds);

    /**
     * Returns published version of a document. 
     */
    DocumentDomainObject getDocument(Integer documentId);
    
    /**
     * Returns working version of a document.
     */
    DocumentDomainObject getWorkingDocument(Integer documentId);
    
    /**
     * Returns custom version of a document by its id (meta id).
     */
    DocumentDomainObject getDocument(Long metaId);
    
    /**
     * Returns custom document.
     */
    DocumentDomainObject getDocument(Integer documentId, Integer version);    
}