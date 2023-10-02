package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.IdDeleterMenuService;
import com.imcode.imcms.mapping.DocumentLoaderCachingProxy;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MenuRepository;
import imcode.server.Imcms;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("menuService")
public class CachingMenuService extends AbstractVersionedContentService<Menu, MenuRepository>
        implements IdDeleterMenuService {

    private final IdDeleterMenuService defaultMenuService;
    private final DocumentLoaderCachingProxy documentLoaderCachingProxy;

    CachingMenuService(IdDeleterMenuService defaultMenuService,
                       DocumentLoaderCachingProxy documentLoaderCachingProxy,
                       MenuRepository repository) {
        super(repository);

        this.defaultMenuService = defaultMenuService;
        this.documentLoaderCachingProxy = documentLoaderCachingProxy;
    }

    @Override
    public MenuDTO getMenuDTO(int docId, int menuIndex, String language, String typeSort) {
        return documentLoaderCachingProxy.getMenuItems(
                getKey(menuIndex, docId, language, typeSort),
                () -> defaultMenuService.getMenuDTO(docId, menuIndex, language, typeSort)
        );
    }

    @Override
    public List<MenuItemDTO> getSortedMenuItems(MenuDTO menuDTO, String langCode) {
        return documentLoaderCachingProxy.getSortedMenuItems(
                getKey(menuDTO.getMenuIndex(),
                        menuDTO.getDocId(),
                        langCode,
                        menuDTO.getTypeSort(),
                        menuDTO.getMenuItems()),
                () -> defaultMenuService.getSortedMenuItems(menuDTO, langCode)
        );
    }

    @Override
    public List<MenuItemDTO> getVisibleMenuItems(int docId, int menuIndex, String language) {
        return documentLoaderCachingProxy.getVisibleMenuItems(
                getKey(menuIndex, docId, language),
                () -> defaultMenuService.getVisibleMenuItems(docId, menuIndex, language)
        );
    }

    @Override
    public List<MenuItemDTO> getVisibleMenuItems(int docId, int menuIndex, int versionNo, String language) {
        return defaultMenuService.getVisibleMenuItems(docId, menuIndex, versionNo, language);
    }

    @Override
    public List<MenuItemDTO> getPreviewMenuItems(int docId, int menuIndex, String language) {
        return defaultMenuService.getPreviewMenuItems(docId, menuIndex, language);
    }

    @Override
    public List<MenuItemDTO> getPreviewMenuItems(int docId, int menuIndex, int versionNo, String language) {
        return defaultMenuService.getPreviewMenuItems(docId, menuIndex, versionNo, language);
    }

    @Override
    public List<MenuItemDTO> getPublicMenuItems(int docId, int menuIndex, String language) {
        if (Imcms.getUser().isDefaultUser()) {
            return documentLoaderCachingProxy.getPublicMenuItems(
                    getKey(menuIndex, docId, language),
                    () -> defaultMenuService.getPublicMenuItems(docId, menuIndex, language)
            );
        } else {
            return defaultMenuService.getPublicMenuItems(docId, menuIndex, language);
        }
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex, String language,
                                       String attributes, String treeKey, String wrap) {
        return documentLoaderCachingProxy.getVisibleMenuAsHtml(
                getKey(menuIndex, docId, language, true, attributes, treeKey, wrap),
                () -> defaultMenuService.getVisibleMenuAsHtml(docId, menuIndex, language, attributes, treeKey, wrap));
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex, int versionNo, String language, String attributes, String treeKey, String wrap) {
        return defaultMenuService.getVisibleMenuAsHtml(docId, menuIndex, versionNo, language, attributes, treeKey, wrap);
    }

    @Override
    public String getPreviewMenuAsHtml(int docId, int menuIndex, String language, String attributes, String treeKey, String wrap) {
        return defaultMenuService.getPreviewMenuAsHtml(docId, menuIndex, language, attributes, treeKey, wrap);
    }

    @Override
    public String getPreviewMenuAsHtml(int docId, int menuIndex, int versionNo, String language, String attributes, String treeKey, String wrap) {
        return defaultMenuService.getPreviewMenuAsHtml(docId, menuIndex, versionNo, language, attributes, treeKey, wrap);
    }

    @Override
    public String getPublicMenuAsHtml(int docId, int menuIndex, String language,
                                      String attributes, String treeKey, String wrap) {
        if (Imcms.getUser().isDefaultUser()) {
            return documentLoaderCachingProxy.getPublicMenuAsHtml(
                    getKey(menuIndex, docId, language, true, attributes, treeKey, wrap),
                    () -> defaultMenuService.getPublicMenuAsHtml(docId, menuIndex, language, attributes, treeKey, wrap));
        } else {
            return defaultMenuService.getPublicMenuAsHtml(docId, menuIndex, language, attributes, treeKey, wrap);
        }
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex) {
        return documentLoaderCachingProxy.getVisibleMenuAsHtml(
                getKey(menuIndex, docId, null, true),
                () -> defaultMenuService.getVisibleMenuAsHtml(docId, menuIndex));
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex, int versionNo) {
        return defaultMenuService.getVisibleMenuAsHtml(docId, menuIndex, versionNo);
    }

    @Override
    public String getPublicMenuAsHtml(int docId, int menuIndex) {
        if (Imcms.getUser().isDefaultUser()) {
            return documentLoaderCachingProxy.getPublicMenuAsHtml(
                    getKey(menuIndex, docId, null, true),
                    () -> defaultMenuService.getPublicMenuAsHtml(docId, menuIndex));
        } else {
            return defaultMenuService.getPublicMenuAsHtml(docId, menuIndex);
        }
    }

    @Override
    public List<Menu> getAll() {
        return defaultMenuService.getAll();
    }

    @Override
    public List<Menu> getByDocId(Integer docId) {
        documentLoaderCachingProxy.removeDocFromCache(docId);
        return defaultMenuService.getByDocId(docId);
    }

    @Override
    public MenuDTO saveFrom(MenuDTO menuDTO) {
        documentLoaderCachingProxy.invalidateMenuItemsCacheBy(menuDTO);
        return defaultMenuService.saveFrom(menuDTO);
    }

    @Override
    public void setAsWorkingVersion(Version version) {
        documentLoaderCachingProxy.invalidateMenuItemsCacheBy(version);
        defaultMenuService.setAsWorkingVersion(version);
    }

    @Override
    public void deleteByVersion(Version version) {
        documentLoaderCachingProxy.invalidateMenuItemsCacheBy(version);
        defaultMenuService.deleteByVersion(version);
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        documentLoaderCachingProxy.invalidateMenuItemsCacheBy(docIdToDelete);
        defaultMenuService.deleteByDocId(docIdToDelete);
    }

    @Override
    public Menu removeId(Menu menu, Version version) {
        documentLoaderCachingProxy.invalidateMenuItemsCacheBy(menu);
        return defaultMenuService.removeId(menu, version);
    }

    private DocumentLoaderCachingProxy.MenuCacheKey getKey(final int menuIndex,
                                                           final int docId,
                                                           final String language) {
        return getKey(menuIndex, docId, language, false);
    }

    private DocumentLoaderCachingProxy.MenuCacheKey getKey(final int menuIndex,
                                                           final int docId,
                                                           final String language,
                                                           final boolean html) {
        return new DocumentLoaderCachingProxy.MenuCacheKey(menuIndex, docId, language, html);
    }

    private DocumentLoaderCachingProxy.MenuCacheKey getKey(final int menuIndex,
                                                           final int docId,
                                                           final String language,
                                                           final String typeSort) {
        return new DocumentLoaderCachingProxy.MenuCacheKey(menuIndex, docId, language, typeSort);
    }

    private DocumentLoaderCachingProxy.MenuCacheKey getKey(final int menuIndex,
                                                           final int docId,
                                                           final String language,
                                                           final String typeSort,
                                                           final List<MenuItemDTO> menuItems) {
        return new DocumentLoaderCachingProxy.MenuCacheKey(menuIndex, docId, language, typeSort, menuItems);
    }

    private DocumentLoaderCachingProxy.MenuCacheKey getKey(final int menuIndex,
                                                           final int docId,
                                                           final String language,
                                                           final boolean html,
                                                           final String attributes,
                                                           final String treeKey,
                                                           final String wrap) {
        return new DocumentLoaderCachingProxy.MenuCacheKey(menuIndex, docId, language, html, attributes, treeKey, wrap);
    }
}
