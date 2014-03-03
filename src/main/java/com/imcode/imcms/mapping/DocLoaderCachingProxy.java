package com.imcode.imcms.mapping;

import com.google.common.base.Supplier;
import com.imcode.imcms.api.DocumentLanguageSupport;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionInfo;
import com.imcode.imcms.mapping.jpa.doc.DocVersion;
import imcode.server.document.DocumentDomainObject;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.CacheManager;

import com.imcode.imcms.mapping.container.DocRef;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DocLoaderCachingProxy {

    private static class DocCacheKey {
        private final int docId;
        private final String languageCode;
        private final int hashCode;

        public DocCacheKey(int docId, String languageCode) {
            this.docId = docId;
            this.languageCode = languageCode;
            this.hashCode = Objects.hash(docId, languageCode);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj instanceof DocCacheKey && equals((DocCacheKey) obj));
        }

        private boolean equals(DocCacheKey that) {
            return docId == that.docId && Objects.equals(languageCode, that.languageCode);
        }

        @Override
        public String toString() {
            return com.google.common.base.Objects.toStringHelper(this)
                    .add("docId", docId)
                    .add("languageCode", languageCode)
                    .toString();
        }

        public int getDocId() {
            return docId;
        }

        public String getLanguageCode() {
            return languageCode;
        }
    }

    //fixme - inject
    @Inject
    EntityConverter entityConverter;

    private final DocumentLoader docLoader;
    private final DocumentLanguageSupport docLanguageSupport;
    private final int size;

    private final CacheWrapper<Integer, DocumentMeta> metas;
    private final CacheWrapper<Integer, DocumentVersionInfo> versionInfos;
    private final CacheWrapper<DocCacheKey, DocumentDomainObject> workingDocs;
    private final CacheWrapper<DocCacheKey, DocumentDomainObject> defaultDocs;
    private final CacheWrapper<String, Integer> aliasesToIds;
    private final CacheWrapper<Integer, String> idsToAliases;

    private final CacheManager cacheManager = CacheManager.create();

    public DocLoaderCachingProxy(DocumentLoader docLoader, DocumentLanguageSupport docLanguageSupport, int size) {
        this.docLoader = docLoader;
        this.docLanguageSupport = docLanguageSupport;
        this.size = size;

        metas = CacheWrapper.of(cacheConfiguration("meats"));
        versionInfos = CacheWrapper.of(cacheConfiguration("versionInfos"));
        workingDocs = CacheWrapper.of(cacheConfiguration("workingDocs"));
        defaultDocs = CacheWrapper.of(cacheConfiguration("defaultDocs"));
        aliasesToIds = CacheWrapper.of(cacheConfiguration("aliasesToIds"));
        idsToAliases = CacheWrapper.of(cacheConfiguration("idsToAliases"));

        for (CacheWrapper<?, ?> cacheWrapper : Arrays.asList(metas, versionInfos, workingDocs, defaultDocs, aliasesToIds, idsToAliases)) {
            cacheManager.addCache(cacheWrapper.cache());
        }
    }


    private CacheConfiguration cacheConfiguration(String name) {
        CacheConfiguration cc = new CacheConfiguration();

        cc.setMaxEntriesLocalHeap(size);
        cc.setOverflowToDisk(false);
        cc.setEternal(true);
        cc.setName(DocLoaderCachingProxy.class.getCanonicalName() + "." + name);

        return cc;
    }

    public CacheManager cacheManager() {
        return cacheManager;
    }

    public CacheWrapper<Integer, DocumentMeta> metas() {
        return metas;
    }

    public CacheWrapper<Integer, DocumentVersionInfo> versionInfos() {
        return versionInfos;
    }

    public CacheWrapper<DocCacheKey, DocumentDomainObject> workingDocs() {
        return workingDocs;
    }

    public CacheWrapper<DocCacheKey, DocumentDomainObject> defaultDocs() {
        return defaultDocs;
    }

    public CacheWrapper<String, Integer> aliasesToIds() {
        return aliasesToIds;
    }

    public CacheWrapper<Integer, String> idsToAliases() {
        return idsToAliases;
    }

    /**
     * @return doc's meta or null if doc does not exists
     */
    public DocumentMeta getMeta(final int docId) {
        return metas.getOrPut(docId, new Supplier<DocumentMeta>() {
            public DocumentMeta get() {
                return docLoader.loadMeta(docId);
            }
        });
    }

    /**
     * @return doc's version info or null if doc does not exists
     */
    public DocumentVersionInfo getDocVersionInfo(final int docId) {
        return versionInfos.getOrPut(docId, new Supplier<DocumentVersionInfo>() {
            public DocumentVersionInfo get() {
                List<DocVersion> jpaVersions = docLoader.getDocVersionRepository().findByDocId(docId);

                if (jpaVersions.isEmpty()) {
                    return null;
                }

                DocVersion jpaWorkingVersion = jpaVersions.get(0);
                DocVersion jpaDefaultVersion = docLoader.getDocVersionRepository().findDefault(docId);

                List<DocumentVersion> versions = new LinkedList<>();
                for (DocVersion jpaVersion : jpaVersions) {
                    versions.add(entityConverter.fromEntity(jpaVersion));
                }

                return new DocumentVersionInfo(docId, versions, entityConverter.fromEntity(jpaWorkingVersion),
                        entityConverter.fromEntity(jpaDefaultVersion));
            }
        });
    }

    /**
     * @return doc's id or null if doc does not exists or alias is not set
     */
    public Integer getDocId(final String docAlias) {
        return aliasesToIds.getOrPut(docAlias, new Supplier<Integer>() {
            public Integer get() {
                Integer docId = docLoader.getDocRepository().getDocIdByAlias(docAlias);

                if (docId != null) {
                    idsToAliases.put(docId, docAlias);
                }

                return docId;
            }
        });
    }

    /**
     * @return working doc or null if doc does not exists
     */
    public <T extends DocumentDomainObject> T getWorkingDoc(final int docId, final String docLanguageCode) {
        @SuppressWarnings("unchecked")
        T doc = (T) workingDocs.getOrPut(new DocCacheKey(docId, docLanguageCode), new Supplier<DocumentDomainObject>() {
            public DocumentDomainObject get() {
                DocumentMeta meta = getMeta(docId);

                if (meta == null) {
                    return null;
                }

                DocumentVersionInfo versionInfo = getDocVersionInfo(docId);
                DocumentVersion version = versionInfo.getWorkingVersion();
                DocumentDomainObject doc = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentType());

                doc.setMeta(meta.clone());
                doc.setVersionNo(version.getNo());
                doc.setLanguage(docLanguageSupport.getByCode(docLanguageCode));

                return docLoader.loadAndInitContent(doc);
            }
        });

        return doc;
    }


    /**
     * @return default doc or null if doc does not exists
     */
    public <T extends DocumentDomainObject> T getDefaultDoc(final int docId, final String docLanguageCode) {
        @SuppressWarnings("unchecked")
        T doc = (T) workingDocs.getOrPut(new DocCacheKey(docId, docLanguageCode), new Supplier<DocumentDomainObject>() {
            public DocumentDomainObject get() {
                DocumentMeta meta = getMeta(docId);

                if (meta == null) {
                    return null;
                }

                DocumentVersionInfo versionInfo = getDocVersionInfo(docId);
                DocumentVersion version = versionInfo.getDefaultVersion();
                DocumentDomainObject doc = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentType());

                doc.setMeta(meta.clone());
                doc.setVersionNo(version.getNo());
                doc.setLanguage(docLanguageSupport.getByCode(docLanguageCode));

                return docLoader.loadAndInitContent(doc);
            }
        });

        return doc;
    }

    /**
     * @return custom doc or null if doc does not exists
     */
    public <T extends DocumentDomainObject> T getCustomDoc(DocRef docRef) {
        DocumentMeta meta = getMeta(docRef.getDocId());

        if (meta == null) {
            return null;
        }

        DocumentVersionInfo versionInfo = getDocVersionInfo(docRef.getDocId());
        DocumentVersion version = versionInfo.getDefaultVersion();
        T doc = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentType());

        doc.setMeta(meta.clone());
        doc.setVersionNo(version.getNo());
        doc.setLanguage(docLanguageSupport.getByCode(docRef.getDocLanguageCode()));

        return docLoader.loadAndInitContent(doc);
    }


    public void removeDocFromCache(int docId) {
        metas.remove(docId);
        versionInfos.remove(docId);

        for (String code : docLanguageSupport.getCodes()) {
            DocCacheKey key = new DocCacheKey(docId, code);

            workingDocs.remove(key);
            defaultDocs.remove(key);
        }

        String alias = idsToAliases.get(docId);

        if (alias != null) {
            idsToAliases.remove(docId);
            aliasesToIds.remove(alias);
        }
    }
}


