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
import com.imcode.imcms.api.Meta;

/**
 * Cache for wrapped DatabaseDocumentGetter.
 */
public class CachingDocumentGetter implements DocumentGetter {

    /** Cached metas. */
    private Map<Integer, Meta> metas;    

	
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

    
    /**
     * Database document getter.    
     */
    private DatabaseDocumentGetter databaseDocumentGetter;
        
    public CachingDocumentGetter(DatabaseDocumentGetter databaseDocumentGetter, int cacheSize) {
        this.databaseDocumentGetter = databaseDocumentGetter;
        
        versionsSupports = new HashMap<Integer, DocumentVersionSupport>();
        workingDocuments = Collections.synchronizedMap(new LRUMap(cacheSize));
        publishedDocuments = Collections.synchronizedMap(new LRUMap(cacheSize));
        metas = Collections.synchronizedMap(new LRUMap(cacheSize));
        
        aliasesBidiMap = new DualHashBidiMap();


    }

    
    /**
     * @return document's meta or null if there is no meta with such id.
     */
    public Meta getMeta(Integer metaId) {
        Meta meta = metas.get(metaId);

        if (meta == null) {
            databaseDocumentGetter.getMeta(metaId);

            if (meta != null) {
                metas.put(metaId, meta);
            }
        }

        return meta;
    }

    
    public DocumentVersionSupport getDocumentVersionSupport(Integer metaId) {
        DocumentVersionSupport versionSupport = versionsSupports.get(metaId);
        
    	if (versionsSupports == null) {
            List<DocumentVersion> versions =  databaseDocumentGetter.getDocumentVersionDao()
                    .getDocumentVersions(metaId);
            
            if (versions.size() > 0) {
                versionSupport = new DocumentVersionSupport(metaId, versions);
                versionsSupports.put(metaId, versionSupport);
            }
    	}
        
    	return versionSupport;
    } 
    

    public DocumentDomainObject getDocument(Integer metaId, Integer versionNumber) {
        Meta meta = getMeta(metaId);

        if (meta == null) {
            return null;
        }
    	
    	return databaseDocumentGetter.getDocument(meta, versionNumber);
    }

    
    
    public DocumentDomainObject getPublishedDocument(Integer metaId) {
        Meta meta = getMeta(metaId);

        if (meta == null) {
            return null;
        }

        DocumentDomainObject document = publishedDocuments.get(metaId);

    	if (document == null) {
	        document = publishedDocuments.get(meta.clone());
	        publishedDocuments.put(metaId, document);
    	}

        return document;
    }
    
    public DocumentDomainObject getWorkingDocument(Integer metaId) {
        Meta meta = getMeta(metaId);

        if (meta == null) {
            return null;
        }

        DocumentDomainObject document = workingDocuments.get(metaId);
        
    	if (document == null) {
	        document = databaseDocumentGetter.getWorkingDocument(meta.clone());
	        workingDocuments.put(metaId, document);
    	}

        return document;
    } 
    
    /**
     * Returns working document.
     */
    public DocumentDomainObject getDocument(Integer metaId) {
    	return getWorkingDocument(metaId);
    }    
            
    public List<DocumentDomainObject> getDocuments(Collection<Integer> metaIds) {
        return databaseDocumentGetter.getDocuments(metaIds) ;
    }
    
    public List<DocumentDomainObject> getPublishedDocuments(Collection<Integer> metaIds) {
        return databaseDocumentGetter.getPublishedDocuments(metaIds) ;
    }    

    
    public void clearCache() {
    	publishedDocuments.clear();
    	workingDocuments.clear();  
    	versionsSupports.clear();
    	aliasesBidiMap.clear();
    }
    
    
    public void removeDocumentFromCache(Integer metaId) {
    	publishedDocuments.remove(metaId);
    	workingDocuments.remove(metaId);
    	versionsSupports.remove(metaId);
    	aliasesBidiMap.remove(metaId);
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
    	Integer metaId = (Integer)aliasesBidiMap.getKey(alias);
    	
    	if (metaId == null) {
    		metaId = databaseDocumentGetter.getMetaDao().getDocumentIdByAlias(alias);
    		
    		if (metaId != null) {
    			aliasesBidiMap.put(metaId, alias);
    		}
    	}
    	
    	return metaId;
    }    	
}