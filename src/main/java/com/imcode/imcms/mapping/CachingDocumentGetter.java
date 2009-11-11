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
 */
public class CachingDocumentGetter implements DocumentGetter {

    /**
     * Cached metas.
     *
     * Map key is a meta id.
     */
    private Map<Integer, Meta> metas;    
	
	/**
	 * Documents versions supports.
	 * 
     * Map key is a document's meta id.
	 */
	private Map<Integer, DocumentVersionInfo> versionInfos;
	
	/**
	 * Active documents
	 * 
     * Map key is a document's meta id.
	 */
    private Map<Integer, DocumentDomainObject> activeDocuments;
    
    /** 
     * Working (version 0) documents
     * 
     * Map key is a document's meta id.
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
    public Meta getMeta(Integer metaId) {
        Meta meta = metas.get(metaId);

        if (meta == null) {
            meta = databaseDocumentGetter.getMeta(metaId);

            if (meta != null) {
                metas.put(metaId, meta);
            }
        }

        return meta;
    }

    
    public DocumentVersionInfo getDocumentVersionInfo(Integer metaId) {
        DocumentVersionInfo versionSupport = versionInfos.get(metaId);
        
    	if (versionSupport == null) {
            List<DocumentVersion> versions =  databaseDocumentGetter.getDocumentVersionDao()
                    .getAllVersions(metaId);
            
            if (versions.size() > 0) {
                versionSupport = new DocumentVersionInfo(metaId, versions);
                versionInfos.put(metaId, versionSupport);
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

    
    
    public DocumentDomainObject getActiveDocument(Integer metaId) {
        Meta meta = getMeta(metaId);

        if (meta == null) {
            return null;
        }

        DocumentDomainObject document = activeDocuments.get(metaId);

    	if (document == null) {
            DocumentVersionInfo info = getDocumentVersionInfo(metaId);
	        document = databaseDocumentGetter.getDocument(meta.clone(), info.getActiveVersionNumber());

            if (document != null) {
	            activeDocuments.put(metaId, document);
            }
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
    
    public List<DocumentDomainObject> getActiveDocuments(Collection<Integer> metaIds) {
        return databaseDocumentGetter.getActiveDocuments(metaIds) ;
    }    

    
    public void clearCache() {
    	activeDocuments.clear();
    	workingDocuments.clear();
    	versionInfos.clear();
    	aliasesBidiMap.clear();
    }
    
    
    public void removeDocumentFromCache(Integer metaId) {
    	activeDocuments.remove(metaId);
    	workingDocuments.remove(metaId);
    	versionInfos.remove(metaId);
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

    public DocumentDomainObject getWorkingDocument(Integer metaId) {
        Meta meta = getMeta(metaId);

        if (meta == null) {
            return null;
        }

        DocumentDomainObject document = workingDocuments.get(metaId);

    	if (document == null) {
	        document = databaseDocumentGetter.getDocument(meta.clone(), 0);

            if (document != null) {
	            workingDocuments.put(metaId, document);
            }
    	}

        return document;
    }
}