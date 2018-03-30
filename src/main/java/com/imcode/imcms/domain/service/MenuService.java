package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;

import java.util.List;

public interface MenuService extends VersionedContentService, DeleterByDocumentId {

    List<MenuItemDTO> getMenuItems(int menuIndex, int docId, String language);

    List<MenuItemDTO> getVisibleMenuItems(int menuIndex, int docId, String language);

    List<MenuItemDTO> getPublicMenuItems(int menuIndex, int docId, String language);

    MenuDTO saveFrom(MenuDTO menuDTO);

    enum MenuItemsStatus {
        PUBLIC,
        ALL
    }

}
