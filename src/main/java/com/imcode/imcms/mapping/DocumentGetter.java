package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.List;

public interface DocumentGetter {
    
    /** 
     * @return list of working documents. 
     */ 
    List<DocumentDomainObject> getDocuments(Collection<Integer> metaIds);
    
    /** 
     * @return list of active documents. 
     */ 
    //List<DocumentDomainObject> getActiveDocuments(Collection<Integer> metaIds);

    /**
     * Returns latest (working) version of a document. 
     * 
     * @param metaId document's meta id.
     *
     * //TODO: WHAT TO RETURN???
     */
    DocumentDomainObject getDocument(int metaId);
    
    /**
     * Returns published version of a document. 
     * 
     * @param metaId document's meta id.
     */
    //DocumentDomainObject getDefaultDocument(Integer metaId);
    
    /**
     * Returns working (latest) version of a document.
     * 
     * @param metaId document's id.
     */
    //DocumentDomainObject getWorkingDocument(Integer metaId);
        
    /**
     * Returns custom version of a document.
     * 
     * @param metaId document's id.
     */
    //DocumentDomainObject getDocument(Integer metaId, Integer version);
}