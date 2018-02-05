package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.persistence.entity.Version;

import java.util.List;
import java.util.Set;

public interface MenuService extends VersionedContentService, DeleterByDocumentId {
    List<MenuItemDTO> getMenuItemsOf(int menuIndex, int docId, String language);

    List<MenuItemDTO> getPublicMenuItemsOf(int menuIndex, int docId, String language);

    MenuDTO saveFrom(MenuDTO menuDTO);

    Set<MenuDTO> getByVersion(Version version);

    enum MenuItemsStatus {
        PUBLIC,
        ALL
    }

}
