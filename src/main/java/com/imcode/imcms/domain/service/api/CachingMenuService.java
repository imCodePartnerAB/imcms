package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.domain.service.AbstractVersionedContentService;
import com.imcode.imcms.domain.service.IdDeleterMenuService;
import com.imcode.imcms.mapping.DocumentLoaderCachingProxy;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MenuRepository;
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
    public List<MenuItemDTO> getPublicMenuItems(int docId, int menuIndex, String language) {
        return documentLoaderCachingProxy.getPublicMenuItems(
                getKey(menuIndex, docId, language),
                () -> defaultMenuService.getPublicMenuItems(docId, menuIndex, language)
        );
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex, String language,
                                       String attributes, String treeKey, String wrap) {
        return defaultMenuService.getVisibleMenuAsHtml(docId, menuIndex, language, attributes, treeKey, wrap);
    }

    @Override
    public String getPublicMenuAsHtml(int docId, int menuIndex, String language,
                                      String attributes, String treeKey, String wrap) {
        return defaultMenuService.getPublicMenuAsHtml(docId, menuIndex, language, attributes, treeKey, wrap);
    }

    @Override
    public String getVisibleMenuAsHtml(int docId, int menuIndex) {
        return defaultMenuService.getVisibleMenuAsHtml(docId, menuIndex);
    }

    @Override
    public String getPublicMenuAsHtml(int docId, int menuIndex) {
        return defaultMenuService.getPublicMenuAsHtml(docId, menuIndex);
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
        return new DocumentLoaderCachingProxy.MenuCacheKey(menuIndex, docId, language);
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
}
