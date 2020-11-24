package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLanguages;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionInfo;
import com.imcode.imcms.domain.component.DocumentsCache;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.PropertyService;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Version;
import imcode.server.Config;
import imcode.server.document.DocumentDomainObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
@Slf4j
public class DocumentLoaderCachingProxy {

    private final DocumentVersionMapper versionMapper;
    private final DocumentLoader loader;
    private final DocumentLanguages documentLanguages;
    private final PropertyService propertyService;
    private final DocumentsCache documentsCache;
    private final int size;
    private final CacheWrapper<Integer, DocumentMeta> metas;
    private final CacheWrapper<Integer, DocumentVersionInfo> versionInfos;
    private final CacheWrapper<DocCacheKey, DocumentDomainObject> workingDocs;
    private final CacheWrapper<DocCacheKey, DocumentDomainObject> defaultDocs;
    private final CacheWrapper<String, Integer> aliasesToIds;
    private final CacheWrapper<Integer, String> idsToAliases;
    private final CacheWrapper<MenuCacheKey, MenuDTO> menuDTO;
    private final CacheWrapper<MenuCacheKey, List<MenuItemDTO>> visibleMenuItems;
    private final CacheWrapper<MenuCacheKey, List<MenuItemDTO>> publicMenuItems;
    private final CacheWrapper<MenuCacheKey, List<MenuItemDTO>> sortedMenuItems;
    private final CacheManager cacheManager = CacheManager.create();

    private final List<Ehcache> menuCaches;

    public DocumentLoaderCachingProxy(DocumentVersionMapper versionMapper,
                                      DocumentLoader loader,
                                      DocumentLanguages documentLanguages,
                                      PropertyService propertyService,
                                      DocumentsCache documentsCache, Config config) {
        this.versionMapper = versionMapper;
        this.loader = loader;
        this.documentLanguages = documentLanguages;
        this.propertyService = propertyService;
        this.documentsCache = documentsCache;
        this.size = config.getDocumentCacheMaxSize();

        metas = CacheWrapper.of(cacheConfiguration("metas"));
        versionInfos = CacheWrapper.of(cacheConfiguration("versionInfos"));
        workingDocs = CacheWrapper.of(cacheConfiguration("workingDocs"));
        defaultDocs = CacheWrapper.of(cacheConfiguration("defaultDocs"));
        aliasesToIds = CacheWrapper.of(cacheConfiguration("aliasesToIds"));
        idsToAliases = CacheWrapper.of(cacheConfiguration("idsToAliases"));
        menuDTO = CacheWrapper.of(cacheConfiguration("menuDTO"));
        visibleMenuItems = CacheWrapper.of(cacheConfiguration("visibleMenuItems"));
        publicMenuItems = CacheWrapper.of(cacheConfiguration("publicMenuItems"));
        sortedMenuItems = CacheWrapper.of(cacheConfiguration("sortedMenuItems"));

        Stream.of(
                metas, versionInfos, workingDocs, defaultDocs, aliasesToIds, idsToAliases,
                menuDTO, visibleMenuItems, publicMenuItems, sortedMenuItems
        )
                .forEach(cacheWrapper -> cacheManager.addCache(cacheWrapper.cache()));

        menuCaches = Arrays.asList(
                menuDTO.cache(),
                visibleMenuItems.cache(),
                publicMenuItems.cache(),
                sortedMenuItems.cache()
        );
    }

    private CacheConfiguration cacheConfiguration(String name) {
        CacheConfiguration cc = new CacheConfiguration();

        cc.setMaxEntriesLocalHeap(size);
        cc.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.DISTRIBUTED));
        cc.setEternal(true);
        cc.setName(DocumentLoaderCachingProxy.class.getCanonicalName() + "." + name);

        return cc;
    }

    /**
     * @return doc's meta or null if doc does not exists
     */
    public DocumentMeta getMeta(final int docId) {
        return metas.getOrPut(docId, () -> loader.loadMeta(docId));
    }

    /**
     * @return doc's version info or null if doc does not exists
     */
    public DocumentVersionInfo getDocVersionInfo(final int docId) {
        return versionInfos.getOrPut(docId, () -> versionMapper.getInfo(docId));
    }

    /**
     * @return doc's id or null if doc does not exists or alias is not set
     */
    public Integer getDocIdByAlias(final String docAlias) {
        return aliasesToIds.getOrPut(docAlias, () -> {
            Integer docId = propertyService.getDocIdByAlias(docAlias);

            if (docId != null) {
                idsToAliases.put(docId, docAlias);
            }

            return docId;
        });
    }

    /**
     * @return working doc or null if doc does not exists
     */
    @SuppressWarnings("unchecked")
    public <T extends DocumentDomainObject> T getWorkingDoc(final int docId, final String docLanguageCode) {
        return (T) workingDocs.getOrPut(new DocCacheKey(docId, docLanguageCode), () -> {
            DocumentMeta meta = getMeta(docId);

            if (meta == null) {
                return null;
            }

            DocumentVersionInfo versionInfo = getDocVersionInfo(docId);
            DocumentVersion version = versionInfo.getWorkingVersion();
            DocumentDomainObject doc = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentTypeId());

            doc.setMeta(meta.clone());
            doc.setVersionNo(version.getNo());
            doc.setLanguage(documentLanguages.getByCode(docLanguageCode));

            return loader.loadAndInitContent(doc);
        });
    }

    /**
     * @return default doc or null if doc does not exists
     */
    @SuppressWarnings("unchecked")
    public <T extends DocumentDomainObject> T getDefaultDoc(final int docId, final String docLanguageCode) {
        return (T) defaultDocs.getOrPut(new DocCacheKey(docId, docLanguageCode), () -> {
            DocumentMeta meta = getMeta(docId);

            if (meta == null) {
                return null;
            }

            DocumentVersionInfo versionInfo = getDocVersionInfo(docId);
            DocumentVersion version = versionInfo.getDefaultVersion();
            DocumentDomainObject doc = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentTypeId());

            doc.setMeta(meta.clone());
            doc.setVersionNo(version.getNo());
            doc.setLanguage(documentLanguages.getByCode(docLanguageCode));

            return loader.loadAndInitContent(doc);
        });
    }

    /**
     * @return custom doc or null if doc does not exists
     */
    public <T extends DocumentDomainObject> T getCustomDoc(DocRef docRef) {
        DocumentMeta meta = getMeta(docRef.getId());

        if (meta == null) {
            return null;
        }

        DocumentVersionInfo versionInfo = getDocVersionInfo(docRef.getId());
        DocumentVersion version = versionInfo.getVersion(docRef.getVersionNo());
        T doc = DocumentDomainObject.fromDocumentTypeId(meta.getDocumentTypeId());

        doc.setMeta(meta.clone());
        doc.setVersionNo(version.getNo());
        doc.setLanguage(documentLanguages.getByCode(docRef.getLanguageCode()));

        return loader.loadAndInitContent(doc);
    }

    public void removeDocFromCache(int docId) {
        metas.remove(docId);
        versionInfos.remove(docId);

        documentLanguages.getCodes().forEach(code -> {
            DocCacheKey key = new DocCacheKey(docId, code);

            workingDocs.remove(key);
            defaultDocs.remove(key);
            invalidateMenuItemsCacheBy(docId);
        });

        Optional<String> aliasCurrentDoc = Optional.ofNullable(idsToAliases.get(docId));
        aliasCurrentDoc.ifPresent(alias -> {
            idsToAliases.remove(docId);
            aliasesToIds.remove(alias);
        });

        documentsCache.invalidateDoc(docId, aliasCurrentDoc.orElse(null));
    }

    public MenuDTO getMenuItems(final MenuCacheKey menuCacheKey,
                                final Supplier<MenuDTO> menuDtoSupplier) {
        return menuDTO.getOrPut(menuCacheKey, menuDtoSupplier);
    }


    public List<MenuItemDTO> getSortedMenuItems(final MenuCacheKey menuCacheKey,
                                                final Supplier<List<MenuItemDTO>> menuDtoSupplier) {

        return sortedMenuItems.getOrPut(menuCacheKey, menuDtoSupplier);
    }

    public List<MenuItemDTO> getVisibleMenuItems(final MenuCacheKey menuCacheKey,
                                                 final Supplier<List<MenuItemDTO>> menuDtoSupplier) {
        return visibleMenuItems.getOrPut(menuCacheKey, menuDtoSupplier);
    }

    public List<MenuItemDTO> getPublicMenuItems(final MenuCacheKey menuCacheKey,
                                                final Supplier<List<MenuItemDTO>> menuDtoSupplier) {

        return publicMenuItems.getOrPut(menuCacheKey, menuDtoSupplier);
    }

    public void invalidateMenuItemsCacheBy(final Menu menu) {
        menuCaches.forEach(cache -> clearCache(cache, menu.getVersion().getDocId(), menu.getNo()));
    }

    public void invalidateMenuItemsCacheBy(final Integer docId) {
        menuCaches.forEach(cache -> clearCache(cache, docId));
    }

    public void invalidateMenuItemsCacheBy(final Version version) {
        menuCaches.forEach(cache -> clearCache(cache, version.getDocId()));
    }

    public void invalidateMenuItemsCacheBy(final MenuDTO menuDTO) {
        menuCaches.forEach(cache -> clearCache(cache, menuDTO.getDocId(), menuDTO.getMenuIndex()));
    }

    public void invalidateMenuItemsCacheBy(final Integer docId, final Integer menuIndex) {
        menuCaches.forEach(cache -> clearCache(cache, docId, menuIndex));
    }

    private void clearCache(final Ehcache cache, final int docId, final int menuIndex) {
        clearCache(cache, key -> key.getDocId() == docId && key.getMenuIndex() == menuIndex);
    }

    private void clearCache(final Ehcache cache, final int docId) {
        clearCache(cache, key -> key.getDocId() == docId);
    }

    @SuppressWarnings("unchecked")
    private void clearCache(final Ehcache cache, final Predicate<MenuCacheKey> menuCacheKeyPredicate) {
        ((List<MenuCacheKey>) cache.getKeys())
                .stream()
                .filter(menuCacheKeyPredicate)
                .forEach(cache::remove);
    }

    @Data
    @AllArgsConstructor
    private static class DocCacheKey implements Serializable {

        private static final long serialVersionUID = 8072998402739750407L;

        private final int docId;
        private final String languageCode;
    }

    @Data
    @AllArgsConstructor
    public static class MenuCacheKey implements Serializable {

        private static final long serialVersionUID = 4026829752411299618L;

        private int menuIndex;
        private int docId;
        private String language;
        private boolean nested;
        private String typeSort;
        private List<MenuItemDTO> menuItems;


        public MenuCacheKey(int menuIndex, int docId, String language, boolean nested, String typeSort) {
            this.menuIndex = menuIndex;
            this.docId = docId;
            this.language = language;
            this.nested = nested;
            this.typeSort = typeSort;
        }

        public MenuCacheKey(int menuIndex, int docId, String language, boolean nested) {
            this.menuIndex = menuIndex;
            this.docId = docId;
            this.language = language;
            this.nested = nested;
        }
    }
}


