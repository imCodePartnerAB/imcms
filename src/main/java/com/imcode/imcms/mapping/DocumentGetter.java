package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;

public interface DocumentGetter {
    
    /** Return a list of published documents <em>in the same order</em> as the documentIds */ 
    List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds);

    /**
     * Returns published version of a document. 
     * 
     * @param metaId document's meta id.
     */
    DocumentDomainObject getDocument(Integer documentId);       
    
    /**
     * Returns working version of a document.
     * 
     * @param documentId document's id.
     */
    DocumentDomainObject getWorkingDocument(Integer documentId);
        
    /**
     * Returns custom document.
     * 
     * @param documentId document's id.
     */
    DocumentDomainObject getDocument(Integer documentId, Integer version);    
}