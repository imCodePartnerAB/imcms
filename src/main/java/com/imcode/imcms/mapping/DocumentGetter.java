package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;

public interface DocumentGetter {
    
    /** 
     * @return list of working documents. 
     */ 
    List<DocumentDomainObject> getDocuments(Collection<Integer> documentIds);
    
    /** 
     * @return list of published documents. 
     */ 
    List<DocumentDomainObject> getPublishedDocuments(Collection<Integer> documentIds);    

    /**
     * Returns latest (working) version of a document. 
     * 
     * @param metaId document's meta id.
     */
    DocumentDomainObject getDocument(Integer documentId);
    
    /**
     * Returns published version of a document. 
     * 
     * @param metaId document's meta id.
     */
    DocumentDomainObject getPublishedDocument(Integer documentId);    
    
    /**
     * Returns working (latest) version of a document.
     * 
     * @param documentId document's id.
     */
    DocumentDomainObject getWorkingDocument(Integer documentId);
        
    /**
     * Returns custom version of a document.
     * 
     * @param documentId document's id.
     */
    DocumentDomainObject getDocument(Integer documentId, Integer version);    
}