package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.collections.map.LRUMap;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionSupport;

/**
 * Cache for wrapped DatabaseDocumentGetter.
 */
public class CachingDocumentGetter implements DocumentGetter {
	
	/**
	 * Documents versions supports.
	 * 
     * Cache key is a document id. 
	 */
	private Map<Integer, DocumentVersionSupport> versionsSupports;
	
	/**
	 * Published documents cache.
	 * 
     * Cache key is a document id.
	 */
    private Map<Integer, DocumentDomainObject> publishedDocuments;
    
    /** 
     * Working documents cache.
     * 
     * Cache key is a document id.
     */
    private Map<Integer, DocumentDomainObject> workingDocuments;    
    
    /**
     * Aliases cache.
     * 
     * Represented as bidirectional map.
     * 
     * Cache key is a document id - Integer.
     * Cache value is document alias - String.
     */
    private BidiMap aliasesBidiMap; 
    
    private Map<Integer, Map<Integer, DocumentDomainObject>> customDocuments;
    
    /**
     * Database document getter.    
     */
    private DatabaseDocumentGetter databaseDocumentGetter;
        
    public CachingDocumentGetter(DatabaseDocumentGetter databaseDocumentGetter, int cacheSize) {
        this.databaseDocumentGetter = databaseDocumentGetter;
        
        versionsSupports = new HashMap<Integer, DocumentVersionSupport>();
        workingDocuments = Collections.synchronizedMap(new LRUMap(cacheSize));
        publishedDocuments = Collections.synchronizedMap(new LRUMap(cacheSize));
        customDocuments = Collections.synchronizedMap(new LRUMap(cacheSize));
        
        aliasesBidiMap = new DualHashBidiMap();
    }
    
    public DocumentVersionSupport getDocumentVersionSupport(Integer documentId) {
    	if (versionsSupports.containsKey(documentId)) {
    		return versionsSupports.get(documentId);
    	} 

    	DocumentVersionSupport versionSupport = null;
   		List<DocumentVersion> versions =  databaseDocumentGetter.getMetaDao()
    			.getDocumentVersions(documentId);
    		
   		if (versions.size() > 0) {
   			versionSupport = new DocumentVersionSupport(documentId, versions);    		
    	}
   		
   		versionsSupports.put(documentId, versionSupport);
   		    	
    	return versionSupport;
    } 
    

    public DocumentDomainObject getDocument(Integer documentId, Integer versionNumber) {
    	DocumentVersionSupport versionSupport = getDocumentVersionSupport(documentId);
    	
    	if (versionSupport == null) {
    		return null;
    	}
    		
    	DocumentVersion version = versionSupport.getVersion(versionNumber);
    	
    	if (version == null) {
    		return null;
    	}
    	
    	switch (version.getTag()) {
		case WORKING:
			return getWorkingDocument(documentId);

		case PUBLISHED:
			return getPublishedDocument(documentId);
			
		default:
			return getCustomDocument(documentId, versionNumber);
    	}
    }
    
    
    private DocumentDomainObject getCustomDocument(Integer documentId, Integer versionNumber) {
		Map<Integer, DocumentDomainObject> documents = customDocuments.get(documentId);
		DocumentDomainObject document;
		
		if (documents == null) {
			documents = new HashMap<Integer, DocumentDomainObject>();
			customDocuments.put(documentId, documents);
			
			document = databaseDocumentGetter.getDocument(documentId, versionNumber);
			documents.put(versionNumber, document);
		} else {
			if (documents.containsKey(versionNumber)) {
				document = documents.get(versionNumber);
			} else {
				document = databaseDocumentGetter.getDocument(documentId, versionNumber);				
				documents.put(versionNumber, document);
			}
		}
		
		return document;    	
    }
    
    
    public DocumentDomainObject getPublishedDocument(Integer documentId) {
    	if (publishedDocuments.containsKey(documentId)) {
    		return publishedDocuments.get(documentId);
    	} else {
	        DocumentDomainObject document = databaseDocumentGetter.getPublishedDocument(documentId);
        	publishedDocuments.put(documentId, document) ;
        	
        	return document;
    	}
    }
    
    public DocumentDomainObject getWorkingDocument(Integer documentId) {
    	if (workingDocuments.containsKey(documentId)) {
    		return workingDocuments.get(documentId);
    	} else {
	        DocumentDomainObject document = databaseDocumentGetter.getWorkingDocument(documentId);
	        workingDocuments.put(documentId, document) ;
        	
        	return document;
    	}
    } 
    
    /**
     * Returns latest (working) document.
     */
    public DocumentDomainObject getDocument(Integer documentId) {
    	return getWorkingDocument(documentId);
    }    
            
    public List getDocuments(Collection documentIds) {
        return databaseDocumentGetter.getDocuments(documentIds) ;
    }	

    
    public void clearCache() {
    	publishedDocuments.clear();
    	workingDocuments.clear();  
    	versionsSupports.clear();
    	aliasesBidiMap.clear();
    	customDocuments.clear();
    }
    
    
    public void removeDocumentFromCache(Integer documentId) {
    	publishedDocuments.remove(documentId);
    	workingDocuments.remove(documentId);
    	versionsSupports.remove(documentId);
    	aliasesBidiMap.remove(documentId);
    	customDocuments.remove(documentId);
    } 
    
    /**
     * Returns document id by alias.
     * Caches returned value.
     * 
     * @param alias document's alias.
     * 
     * @return document id.
     */
    public Integer getDocumentIdByAlias(String alias) {
    	Integer documentId = (Integer)aliasesBidiMap.getKey(alias);
    	
    	if (documentId == null) {
    		documentId = databaseDocumentGetter.getMetaDao().getDocumentIdByAlias(alias);
    		
    		if (documentId != null) {
    			aliasesBidiMap.put(documentId, alias);
    		}
    	}
    	
    	return documentId;
    }    	
}