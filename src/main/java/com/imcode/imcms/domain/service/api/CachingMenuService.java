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
    public List<MenuItemDTO> getMenuItems(int docId, int menuIndex, String language, boolean nested) {
        return documentLoaderCachingProxy.getMenuItems(
                getKey(menuIndex, docId, language, nested),
                () -> defaultMenuService.getMenuItems(docId, menuIndex, language, nested)
        );
    }

    @Override
    public List<MenuItemDTO> getVisibleMenuItems(int docId, int menuIndex, String language, boolean nested) {
        return documentLoaderCachingProxy.getVisibleMenuItems(
                getKey(menuIndex, docId, language, nested),
                () -> defaultMenuService.getVisibleMenuItems(docId, menuIndex, language, nested)
        );
    }

    @Override
    public List<MenuItemDTO> getPublicMenuItems(int docId, int menuIndex, String language, boolean nested) {
        return documentLoaderCachingProxy.getPublicMenuItems(
                getKey(menuIndex, docId, language, nested),
                () -> defaultMenuService.getPublicMenuItems(docId, menuIndex, language, nested)
        );
    }

    @Override
    public List<Menu> getAll() {
        return defaultMenuService.getAll();
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

    private DocumentLoaderCachingProxy.MenuCacheKey getKey(final int menuIndex, final int docId, final String language, final boolean disableNested) {
        return new DocumentLoaderCachingProxy.MenuCacheKey(menuIndex, docId, language, disableNested);
    }
}
