package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;

public interface DocumentGetter {
    
    /** Return a list of documents <em>in the same order</em> as the documentIds */ 
    List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds);

    /**
     * Returns published document version. 
     */
    DocumentDomainObject getDocument(Integer documentId);
    
    /**
     * Returns published document version. 
     */
    DocumentDomainObject getPublishedDocument(Integer documentId);    

    /**
     * Returns working document version.
     */
    DocumentDomainObject getWorkingDocument(Integer documentId);
    
    /**
     * TODO:
     * Returns custom document version.
     */
    //DocumentDomainObject getDocument(Integer documentId, int version);
    //DocumentDomainObject getDocument(Long metaId);
}