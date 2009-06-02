package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.collections.map.LRUMap;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionSupport;
import com.imcode.imcms.dao.MetaDao;

/**
 * Cache for wrapped DatabaseDocumentGetter.
 */
public class CachingDocumentGetter extends DocumentGetterWrapper {
	
	/**
	 * Documents versions supports.
	 * 
     * Cache key is a document id. 
	 */
	private Map<Integer, DocumentVersionSupport> versionsSupports;
	
	/**
	 * Latest documents cache.
	 * 
     * Cache key is a document id. 
	 */
	private Map<Integer, DocumentDomainObject> latestDocuments;

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
    
    /**
     * 
     */
    private MetaDao metaDao;
    
    public CachingDocumentGetter(DocumentGetter documentGetter, MetaDao metaDao, int cacheSize) {
        super(documentGetter);
        this.metaDao = metaDao;
        
        versionsSupports = new HashMap<Integer, DocumentVersionSupport>();
        latestDocuments = Collections.synchronizedMap(new LRUMap(cacheSize));
        workingDocuments = Collections.synchronizedMap(new LRUMap(cacheSize));
        publishedDocuments = Collections.synchronizedMap(new LRUMap(cacheSize));
        
        aliasesBidiMap = new DualHashBidiMap();
    }
    
    public DocumentVersionSupport getDocumentVersionSupport(Integer documentId) {
    	DocumentVersionSupport versionSupport = versionsSupports.get(documentId);
    	
    	if (versionSupport == null) {
    		List<DocumentVersion> versions =  metaDao.getDocumentVersions(documentId);
    		
    		if (versions.size() > 0) {
    			versionSupport = new DocumentVersionSupport(documentId, versions);    		
        		versionsSupports.put(documentId, versionSupport);
    		}    		
    	}
    	
    	return versionSupport;
    } 
    
    @Override
    public DocumentDomainObject getDocument(Integer documentId) {
    	//return getPublishedDocument(documentId);
    	return getLatestDocumentVersion(documentId);
    }
    
    @Override
    public DocumentDomainObject getPublishedDocument(Integer documentId) {
        DocumentDomainObject document = publishedDocuments.get(documentId) ;
        
        if (null == document) {
        	// AOP?
            document = super.getPublishedDocument(documentId) ;
            
            if (document != null) {            	
            	publishedDocuments.put(documentId, document) ;
            }
        }
                
        return document;
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
			return super.getDocument(documentId, versionNumber);
		}
    }
    
    
    @Override
    public DocumentDomainObject getWorkingDocument(Integer documentId) {
        DocumentDomainObject document = workingDocuments.get(documentId) ;
        
        if (null == document) {
        	// AOP?
            document = super.getWorkingDocument(documentId) ;
            
            if (document != null) {            	
            	workingDocuments.put(documentId, document) ;
            }
        }
                
        return document;
    } 
    
    /**
     * Returns latest document.
     */
    public DocumentDomainObject getLatestDocumentVersion(Integer documentId) {
    	DocumentDomainObject document = latestDocuments.get(documentId);
    	
        if (null == document) {
        	// AOP?
            document = super.getLatestDocumentVersion(documentId) ;
            
            if (document != null) {            	
            	latestDocuments.put(documentId, document) ;
            }
        }
                
        return document;    	
    }
    
    public void clearCache() {
    	publishedDocuments.clear();
    	workingDocuments.clear();  
    	latestDocuments.clear();
    	versionsSupports.clear();
    	aliasesBidiMap.clear();
    }
    
    
    public void removeDocumentFromCache(Integer documentId) {
    	publishedDocuments.remove(documentId);
    	workingDocuments.remove(documentId);
    	latestDocuments.remove(documentId);
    	versionsSupports.remove(documentId);
    	aliasesBidiMap.remove(documentId);
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
    		documentId = metaDao.getDocumentIdByAlias(alias);
    		
    		if (documentId != null) {
    			aliasesBidiMap.put(documentId, alias);
    		}
    	}
    	
    	return documentId;
    }
    	

	public MetaDao getMetaDao() {
		return metaDao;
	}

	public void setMetaDao(MetaDao metaDao) {
		this.metaDao = metaDao;
	}    
}