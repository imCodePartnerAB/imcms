package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;

import java.util.List;

public interface MenuService extends VersionedContentService<MenuDTO>, DeleterByDocumentId {
    List<MenuItemDTO> getMenuItemsOf(int menuIndex, int docId);

    List<MenuItemDTO> getPublicMenuItemsOf(int menuIndex, int docId);

    MenuDTO saveFrom(MenuDTO menuDTO);

    enum MenuItemsStatus {
        PUBLIC,
        ALL
    }

}
