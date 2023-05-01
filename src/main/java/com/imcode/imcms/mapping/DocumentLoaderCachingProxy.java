package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.DocumentVersionInfo;
import com.imcode.imcms.domain.component.DocumentsCache;
import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.security.AccessContentType;
import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
@Slf4j
public class DocumentLoaderCachingProxy {

	private final DocumentVersionMapper versionMapper;
	private final DocumentLoader loader;
    private final LanguageService languageService;
	private final CommonContentService commonContentService;
	private final DocumentsCache documentsCache;
	private final int size;
	private final CacheWrapper<Integer, DocumentMeta> metas;
	private final CacheWrapper<Integer, DocumentVersionInfo> versionInfos;
	private final CacheWrapper<DocCacheKey, DocumentDomainObject> workingDocs;
	private final CacheWrapper<DocCacheKey, DocumentDomainObject> defaultDocs;
	private final CacheWrapper<String, Integer> aliasesToIds;
	private final CacheWrapper<Integer, Set<String>> idsToAliases;
	private final CacheWrapper<MenuCacheKey, MenuDTO> menuDTO;
	private final CacheWrapper<MenuCacheKey, List<MenuItemDTO>> visibleMenuItems;
	private final CacheWrapper<MenuCacheKey, List<MenuItemDTO>> publicMenuItems;
	private final CacheWrapper<MenuCacheKey, List<MenuItemDTO>> sortedMenuItems;
	private final CacheWrapper<MenuCacheKey, String> visibleMenuAsHtml;
	private final CacheWrapper<MenuCacheKey, String> publicMenuAsHtml;
	private final CacheManager cacheManager = CacheManager.create();

	private final List<Ehcache> menuCaches;

    public DocumentLoaderCachingProxy(DocumentVersionMapper versionMapper,
                                      DocumentLoader loader,
                                      LanguageService languageService,
                                      CommonContentService commonContentService,
                                      DocumentsCache documentsCache, Config config) {
        this.versionMapper = versionMapper;
        this.loader = loader;
        this.languageService = languageService;
        this.commonContentService = commonContentService;
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
        visibleMenuAsHtml = CacheWrapper.of(cacheConfiguration("visibleMenuAsHtml"));
        publicMenuAsHtml= CacheWrapper.of(cacheConfiguration("publicMenuAsHtml"));

        Stream.of(
                metas, versionInfos, workingDocs, defaultDocs, aliasesToIds, idsToAliases,
                menuDTO, visibleMenuItems, publicMenuItems, sortedMenuItems, visibleMenuAsHtml, publicMenuAsHtml
        )
                .forEach(cacheWrapper -> cacheManager.addCache(cacheWrapper.cache()));

        menuCaches = Arrays.asList(
                menuDTO.cache(),
                visibleMenuItems.cache(),
                publicMenuItems.cache(),
                sortedMenuItems.cache(),
                visibleMenuAsHtml.cache(),
                publicMenuAsHtml.cache()
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
	        Integer docId = commonContentService.getDocIdByAlias(docAlias);

	        if (docId != null) {
		        Optional.ofNullable(idsToAliases.get(docId)).ifPresentOrElse(aliases -> {
			        aliases.add(docAlias);
			        idsToAliases.put(docId, aliases);
		        }, () -> {
			        idsToAliases.put(docId, new HashSet<>(List.of(docAlias)));
		        });
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
            doc.setLanguage(languageService.findByCode(docLanguageCode));

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
            doc.setLanguage(languageService.findByCode(docLanguageCode));

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
        doc.setLanguage(languageService.findByCode(docRef.getLanguageCode()));

        return loader.loadAndInitContent(doc);
    }

    public void removeDocFromCache(int docId) {
        metas.remove(docId);
	    versionInfos.remove(docId);

	    languageService.getAll().forEach(language -> {
		    DocCacheKey key = new DocCacheKey(docId, language.getCode());

		    workingDocs.remove(key);
		    defaultDocs.remove(key);
		    invalidateMenuItemsCacheBy(docId);
	    });

	    Optional<Set<String>> aliasCurrentDoc = Optional.ofNullable(idsToAliases.get(docId));
	    aliasCurrentDoc.ifPresent(aliases -> {
		    idsToAliases.remove(docId);
		    aliases.forEach(aliasesToIds::remove);
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

    public String getVisibleMenuAsHtml(final MenuCacheKey menuCacheKey,
                                       final Supplier<String> menuHtmlSupplier) {
        return visibleMenuAsHtml.getOrPut(menuCacheKey, menuHtmlSupplier);
    }

    public String getPublicMenuAsHtml(final MenuCacheKey menuCacheKey,
                                                 final Supplier<String> menuHtmlSupplier) {
        return publicMenuAsHtml.getOrPut(menuCacheKey, menuHtmlSupplier);
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
    public static class MenuCacheKey implements Serializable {

        private static final long serialVersionUID = 4026829752411299618L;

        private int menuIndex;
        private int docId;
        private String language;
        private String typeSort;
        private List<MenuItemDTO> menuItems;

        private Boolean html;
        private String attributes;
        private String treeKey;
        private String wrap;

        private Boolean admin;
        private Boolean authorized;

        public MenuCacheKey(int menuIndex, int docId, String language, String typeSort) {
            this(menuIndex, docId, language, typeSort, null, null, null, null, null);
        }

        public MenuCacheKey(int menuIndex, int docId, String language, String typeSort, List<MenuItemDTO> menuItems) {
            this(menuIndex, docId, language, typeSort, menuItems, null, null, null, null);
        }

        public MenuCacheKey(int menuIndex, int docId, String language, Boolean html) {
            this(menuIndex, docId, language, null, null, html, null, null, null);
        }

        public MenuCacheKey(int menuIndex, int docId, String language, Boolean html, String attributes, String treeKey, String wrap) {
            this(menuIndex, docId, language, null, null, html, attributes, treeKey, wrap);
        }

        public MenuCacheKey(int menuIndex, int docId, String language,
                            String typeSort, List<MenuItemDTO> menuItems,
                            Boolean html, String attributes, String treeKey, String wrap) {
            initUser(docId);
            this.menuIndex = menuIndex;
            this.docId = docId;
            this.language = language;
            this.typeSort = typeSort;
            this.menuItems = menuItems;
            this.html = html;
            this.attributes = attributes;
            this.treeKey = treeKey;
            this.wrap = wrap;
        }

        public void initUser(int docId) {
            final UserDomainObject user = Imcms.getUser();

            if(user.isDefaultUser()){
                this.admin = false;
                this.authorized = false;
            }else if(user.isSuperAdmin() ||
                    Imcms.getServices().getAccessService().hasUserEditAccess(user, docId, AccessContentType.MENU)){
                this.admin = true;
                this.authorized = true;
            }else {
                this.admin = false;
                this.authorized = true;
            }
        }
    }
}


