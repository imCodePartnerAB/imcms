package com.imcode.imcms.mapping;

import imcode.server.document.DocumentDomainObject;

import java.util.*;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.collections.map.LRUMap;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionInfo;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.api.I18nLanguage;

/**
 * Caches documents returned by DocumentLoader.
 */
public class DocumentLoaderCachingProxy {

    /**
     * Cached metas.
     *
     * Map key is a document id.
     */
    private Map<Integer, Meta> metas;
	
	/**
	 * Documents versions infos.
	 * 
     * Map key is a document id.
	 */
	private Map<Integer, DocumentVersionInfo> versionInfos;    
	
	/**
	 * Default documents.
     * 
     * Inner map key is a document id.
     */
    private Map<I18nLanguage, Map<Integer, DocumentDomainObject>> defaultDocuments;
    
    /** 
     * Working documents.
     * 
     * Inner map key is a document id.
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

    private DocumentLoader documentLoader;

    private int cacheSize;


    public DocumentLoaderCachingProxy(DocumentLoader documentLoader, int cacheSize) {
        this.documentLoader = documentLoader;
        
        versionInfos = new HashMap<Integer, DocumentVersionInfo>();
        workingDocuments = new HashMap<I18nLanguage, Map<Integer, DocumentDomainObject>>();
        defaultDocuments = Collections.synchronizedMap(new LRUMap(cacheSize));
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
        DocumentVersionInfo versionInfo = versionInfos.get(docId);
        
    	if (versionInfo == null) {
            List<DocumentVersion> versions =  documentLoader.getDocumentVersionDao()
                    .getAllVersions(docId);
            
            if (versions.size() > 0) {
                DocumentVersion workingVersion = versions.get(0);
                DocumentVersion activeVersion = documentLoader.getDocumentVersionDao().getDefaultVersion(docId);

                if (activeVersion == null) {
                    activeVersion = workingVersion;    
                }

                versionInfo = new DocumentVersionInfo(docId, versions, workingVersion, activeVersion);
                
                versionInfos.put(docId, versionInfo);
            }
    	}
        
    	return versionInfo;
    } 
    
            
    public List<DocumentDomainObject> getWorkingDocuments(Collection<Integer> docIds, I18nLanguage language) {
        List<DocumentDomainObject> docs = new LinkedList<DocumentDomainObject>();

        for (Integer docId: docIds) {
            DocumentDomainObject doc = getWorkingDocument(docId, language);

            if (doc != null) {
                docs.add(doc);
            }
        }

        return docs;
    }

    /*
    public List<DocumentDomainObject> getActiveDocuments(Collection<Integer> docIds) {
        return documentLoader.getActiveDocuments(docIds) ;
    }
       */

    
    public void clearCache() {
    	defaultDocuments.clear();
    	workingDocuments.clear();
    	versionInfos.clear();
    	aliasesBidiMap.clear();
    }
    
    
    public void removeDocumentFromCache(Integer docId) {
        for (Map<Integer, DocumentDomainObject> docs: defaultDocuments.values()) {
            docs.remove(docId);
        }

        for (Map<Integer, DocumentDomainObject> docs: workingDocuments.values()) {
            docs.remove(docId);    
        }

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

    /**
     * Returns custom document.
     * 
     * @param docId
     * @param docVersionNo
     * @param language
     * @return
     */
    public DocumentDomainObject getCustomDocument(Integer docId, Integer docVersionNo, I18nLanguage language) {
        Meta meta = getMeta(docId);

        if (meta == null) {
            return null;
        }

        DocumentVersionInfo versionInfo = getDocumentVersionInfo(docId);
        DocumentVersion version = versionInfo.getVersion(docVersionNo);

        if (version == null) {
            return null;
        }

        return documentLoader.loadDocument(meta.clone(), version.clone(), language.clone());
    }
    

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

	        document = documentLoader.loadDocument(meta.clone(), version.clone(), language.clone());

            if (document != null) {
	            documents.put(docId, document);
            }
    	}        
        
        return document;
    }


    public DocumentDomainObject getDefaultDocument(Integer docId, I18nLanguage language) {
        Map<Integer, DocumentDomainObject> documents = getDocuments(defaultDocuments, language);

        DocumentDomainObject document = documents.get(docId);

    	if (document == null) {
            Meta meta = getMeta(docId);

            if (meta == null) {
                return null;
            }

            DocumentVersionInfo versionInfo = getDocumentVersionInfo(docId);
            DocumentVersion version = versionInfo.getDefaultVersion();

	        document = documentLoader.loadDocument(meta.clone(), version.clone(), language.clone());

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

    public Map<I18nLanguage, Map<Integer, DocumentDomainObject>> getDefaultDocuments() {
        return defaultDocuments;
    }


    public Map<I18nLanguage, Map<Integer, DocumentDomainObject>> getWorkingDocuments() {
        return workingDocuments;
    }
}