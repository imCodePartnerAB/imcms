package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;

/**
 * TODO: ??? add cache for custom versions ???
 */
public class CachingDocumentGetter extends DocumentGetterWrapper {

	/**
	 * Published documents cache.
	 * 
     * Cache key is document id.
	 */
    private Map<Integer, DocumentDomainObject> publishedDocumentsCache;
    
    /** 
     * Working documents cache.
     * 
     * Cache key is document id.
     */
    private Map<Integer, DocumentDomainObject> workingDocumentsCache;
    
    
    public CachingDocumentGetter(DocumentGetter documentGetter, int cacheSize) {
        super(documentGetter);
        this.publishedDocumentsCache = Collections.synchronizedMap(new LRUMap(cacheSize));
        this.workingDocumentsCache = Collections.synchronizedMap(new LRUMap(cacheSize));
    }
    
    @Override
    public DocumentDomainObject getDocument(Integer documentId) {
        DocumentDomainObject document = publishedDocumentsCache.get(documentId) ;
        
        if (null == document) {
        	// AOP?
            document = super.getDocument(documentId) ;
            
            if (document != null) {            	
            	publishedDocumentsCache.put(documentId, document) ;
            }
        }
                
        return document;
    }
    
    
    @Override
    public DocumentDomainObject getWorkingDocument(Integer documentId) {
        DocumentDomainObject document = workingDocumentsCache.get(documentId) ;
        
        if (null == document) {
        	// AOP?
            document = super.getDocument(documentId) ;
            
            if (document != null) {            	
            	workingDocumentsCache.put(documentId, document) ;
            }
        }
                
        return document;
    } 
    
    
    public void clearCache() {
    	publishedDocumentsCache.clear();
    	workingDocumentsCache.clear();    	
    }
    
    
    public void removeDocumentFromCache(Integer documentId) {
    	removePublishedDocumentFromCache(documentId);
    	removeWorkingDocumentFromCache(documentId);
    } 
    
    
    public DocumentDomainObject removePublishedDocumentFromCache(
    		Integer documentId) {
    	return publishedDocumentsCache.remove(documentId);    	
    }

    
    public DocumentDomainObject removeWorkingDocumentFromCache(
    		Integer documentId) {
    	return workingDocumentsCache.remove(documentId);    	
    }    
    
    
            
    /*
    public List getDocuments(Collection documentIds) {
        return super.getDocuments(documentIds) ;
    } 
    */   
}