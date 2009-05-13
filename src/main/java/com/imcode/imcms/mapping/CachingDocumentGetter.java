package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections.map.LRUMap;

/**
 * TODO: ??? add limited cache for custom versions ???
 * TODO: ??? add cache for intercepted version ???
 */
public class CachingDocumentGetter extends DocumentGetterWrapper {

	/**
	 * Published documents cache.
	 * 
     * Cache key is document id.
	 */
    private Map<Integer, DocumentDomainObject> publishedDocumentsCache;
    
    /**
     * Aliases cache.
     */
    private Map<String, Integer> aliases;
    
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
        this.aliases = Collections.synchronizedMap(new LRUMap(cacheSize));
    }
    
    @Override
    public DocumentDomainObject getPublishedDocument(Integer documentId) {
        DocumentDomainObject document = publishedDocumentsCache.get(documentId) ;
        
        if (null == document) {
        	// AOP?
            document = super.getPublishedDocument(documentId) ;
            
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
            document = super.getWorkingDocument(documentId) ;
            
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
    
    public void setDocumentAliase(Integer documentId, String alias) {
    	aliases.put(alias, documentId);
    }
    
    public Integer getDocumentId(String alias) {
    	return aliases.get(alias);
    }    
}