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
import com.imcode.imcms.api.DocumentVersionInfo;
import com.imcode.imcms.api.Meta;

/**
 * Cache for wrapped DatabaseDocumentGetter.
 * 
 * TODO: rename to DocumentLoaderCachingProxy
 */
public class CachingDocumentGetter {

    /**
     * Cached metas.
     *
     * Map key is a document id.
     */
    private Map<Integer, Meta> metas;    
	
	/**
	 * Documents versions supports.
	 * 
     * Map key is a document id.
	 */
	private Map<Integer, DocumentVersionInfo> versionInfos;
	
	/**
	 * Active documents
	 * 
     * Map key is a document id.
	 */
    private Map<Integer, DocumentDomainObject> activeDocuments;
    
    /** 
     * Working (version 0) documents
     * 
     * Map key is a document id.
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
        
        versionInfos = new HashMap<Integer, DocumentVersionInfo>();
        workingDocuments = Collections.synchronizedMap(new LRUMap(cacheSize));
        activeDocuments = Collections.synchronizedMap(new LRUMap(cacheSize));
        metas = Collections.synchronizedMap(new LRUMap(cacheSize));
        
        aliasesBidiMap = new DualHashBidiMap();


    }

    
    /**
     * @return document's meta or null if there is no meta with such id.
     */
    public Meta getMeta(Integer docId) {
        Meta meta = metas.get(docId);

        if (meta == null) {
            meta = databaseDocumentGetter.getMeta(docId);

            if (meta != null) {
                metas.put(docId, meta);
            }
        }

        return meta;
    }

    
    public DocumentVersionInfo getDocumentVersionInfo(Integer docId) {
        DocumentVersionInfo versionSupport = versionInfos.get(docId);
        
    	if (versionSupport == null) {
            List<DocumentVersion> versions =  databaseDocumentGetter.getDocumentVersionDao()
                    .getAllVersions(docId);
            
            if (versions.size() > 0) {
                versionSupport = new DocumentVersionInfo(docId, versions);
                versionInfos.put(docId, versionSupport);
            }
    	}
        
    	return versionSupport;
    } 
    

    public DocumentDomainObject getDocument(Integer docId, Integer versionNumber) {
        Meta meta = getMeta(docId);

        if (meta == null) {
            return null;
        }
    	
    	return databaseDocumentGetter.getDocument(meta, versionNumber);
    }

    
    
    public DocumentDomainObject getActiveDocument(Integer docId) {
        Meta meta = getMeta(docId);

        if (meta == null) {
            return null;
        }

        DocumentDomainObject document = activeDocuments.get(docId);

    	if (document == null) {
            DocumentVersionInfo info = getDocumentVersionInfo(docId);
	        document = databaseDocumentGetter.getDocument(meta.clone(), info.getActiveVersionNo());

            if (document != null) {
	            activeDocuments.put(docId, document);
            }
    	}

        return document;
    }

    
    /**
     * Returns working document.
     */
    public DocumentDomainObject getDocument(Integer docId) {
        return getWorkingDocument(docId);
    }    
            
    public List<DocumentDomainObject> getDocuments(Collection<Integer> docIds) {
        return databaseDocumentGetter.getDocuments(docIds) ;
    }
    
    public List<DocumentDomainObject> getActiveDocuments(Collection<Integer> docIds) {
        return databaseDocumentGetter.getActiveDocuments(docIds) ;
    }    

    
    public void clearCache() {
    	activeDocuments.clear();
    	workingDocuments.clear();
    	versionInfos.clear();
    	aliasesBidiMap.clear();
    }
    
    
    public void removeDocumentFromCache(Integer docId) {
    	activeDocuments.remove(docId);
    	workingDocuments.remove(docId);
    	versionInfos.remove(docId);
    	aliasesBidiMap.remove(docId);
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
    	Integer docId = (Integer)aliasesBidiMap.getKey(alias);
    	
    	if (docId == null) {
    		docId = databaseDocumentGetter.getMetaDao().getDocumentIdByAlias(alias);
    		
    		if (docId != null) {
    			aliasesBidiMap.put(docId, alias);
    		}
    	}
    	
    	return docId;
    }

    public DocumentDomainObject getWorkingDocument(Integer docId) {
        Meta meta = getMeta(docId);

        if (meta == null) {
            return null;
        }

        DocumentDomainObject document = workingDocuments.get(docId);

    	if (document == null) {
	        document = databaseDocumentGetter.getDocument(meta.clone(), 0);

            if (document != null) {
	            workingDocuments.put(docId, document);
            }
    	}

        return document;
    }
}