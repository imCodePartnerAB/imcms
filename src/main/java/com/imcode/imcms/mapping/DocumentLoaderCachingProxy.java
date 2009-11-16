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
import com.imcode.imcms.api.I18nLanguage;

/**
 * Caches documents returned by wrapped DocumentLoader.
 */
public class DocumentLoaderCachingProxy {

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
    private Map<I18nLanguage, Map<Integer, DocumentDomainObject>> workingDocuments;
    
    /**
     * Aliases cache.
     * 
     * Represented as bidirectional map.
     * 
     * Cache key is a document id - Integer.
     * Cache value is document alias - String.
     */
    private BidiMap aliasesBidiMap;

    
    /** Wrapped document loader. */
    private DocumentLoader documentLoader;

    private int cacheSize;
    

    public DocumentLoaderCachingProxy(DocumentLoader documentLoader, int cacheSize) {
        this.documentLoader = documentLoader;
        
        versionInfos = new HashMap<Integer, DocumentVersionInfo>();
        //workingDocuments = Collections.synchronizedMap(new LRUMap(cacheSize));
        workingDocuments = new HashMap<I18nLanguage, Map<Integer, DocumentDomainObject>>();
        activeDocuments = Collections.synchronizedMap(new LRUMap(cacheSize));
        metas = Collections.synchronizedMap(new LRUMap(cacheSize));
        this.cacheSize = cacheSize;
        
        aliasesBidiMap = new DualHashBidiMap();
    }


    /**
     * @return document's meta or null if there is no meta with such id.
     */
    public Meta getMeta(Integer docId) {
        Meta meta = metas.get(docId);

        if (meta == null) {
            meta = documentLoader.getMeta(docId);

            if (meta != null) {
                metas.put(docId, meta);
            }
        }

        return meta;
    }

    
    public DocumentVersionInfo getDocumentVersionInfo(Integer docId) {
        DocumentVersionInfo versionSupport = versionInfos.get(docId);
        
    	if (versionSupport == null) {
            List<DocumentVersion> versions =  documentLoader.getDocumentVersionDao()
                    .getAllVersions(docId);
            
            if (versions.size() > 0) {
                versionSupport = new DocumentVersionInfo(docId, versions);
                versionInfos.put(docId, versionSupport);
            }
    	}
        
    	return versionSupport;
    } 

    
     /*
    public DocumentDomainObject getActiveDocument(Integer docId) {
        Meta meta = getMeta(docId);

        if (meta == null) {
            return null;
        }

        DocumentDomainObject document = activeDocuments.get(docId);

    	if (document == null) {
            DocumentVersionInfo info = getDocumentVersionInfo(docId);
	        document = documentLoader.loadDocument(meta.clone(), info.getActiveVersionNo());

            if (document != null) {
	            activeDocuments.put(docId, document);
            }
    	}

        return document;
    }
    */
    
            
    public List<DocumentDomainObject> getDocuments(Collection<Integer> docIds, I18nLanguage language) {
        return documentLoader.loadDocuments(docIds, language) ;
    }

    /*
    public List<DocumentDomainObject> getActiveDocuments(Collection<Integer> docIds) {
        return documentLoader.getActiveDocuments(docIds) ;
    }
       */

    
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
     * Returns document id by an alias.
     *  
     * @param alias document's alias.
     * 
     * @return document id.
     */
    public Integer getDocId(String alias) {
    	Integer docId = (Integer)aliasesBidiMap.getKey(alias);
    	
    	if (docId == null) {
    		docId = documentLoader.getMetaDao().getDocumentIdByAlias(alias);
    		
    		if (docId != null) {
    			aliasesBidiMap.put(docId, alias);
    		}
    	}
    	
    	return docId;
    }
    
    /*
    public DocumentDomainObject getDocument(Integer docId, Integer docVersionNo, Integer languageId) {
        Meta meta = getMeta(docId);

        if (meta == null) {
            return null;
        }

        DocumentVersionInfo versionInfo = getDocumentVersionInfo(docId);
        DocumentVersion version = versionInfo.getVersion(docVersionNo);

        DocumentDomainObject document = workingDocuments.get(docId);

    	if (document == null) {
	        document = documentLoader.loadDocument(meta.clone(), docVersionNo, languageId);

            if (document != null) {
	            workingDocuments.put(docId, document);
            }
    	}

        return document;
    }
    */


    public DocumentDomainObject getWorkingDocument(Integer docId, I18nLanguage language) {
        Map<Integer, DocumentDomainObject> documents = getDocuments(workingDocuments, language);

        DocumentDomainObject document = documents.get(docId);

    	if (document == null) {
            Meta meta = getMeta(docId);

            if (meta == null) {
                return null;
            }
    
            DocumentVersionInfo versionInfo = getDocumentVersionInfo(docId);
            DocumentVersion version = versionInfo.getWorkingVersion();

	        document = documentLoader.loadDocument(meta.clone(), version, language);

            if (document != null) {
	            documents.put(docId, document);
            }
    	}        
        
        return document;
    }


    private Map<Integer, DocumentDomainObject> getDocuments(Map<I18nLanguage, Map<Integer, DocumentDomainObject>> allDocuments, I18nLanguage language) {
        Map<Integer, DocumentDomainObject> documents = allDocuments.get(language);

        if (documents == null) {
            documents = Collections.synchronizedMap(new LRUMap(cacheSize));
            allDocuments.put(language, documents);
        }

        return documents;
    }
}