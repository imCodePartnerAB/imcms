package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Version;

import java.util.List;

public interface MenuService extends VersionedContentService, DeleterByDocumentId {

    List<MenuItemDTO> getMenuItems(int menuIndex, int docId, String language);

    List<MenuItemDTO> getVisibleMenuItems(int menuIndex, int docId, String language);

    List<MenuItemDTO> getPublicMenuItems(int menuIndex, int docId, String language);

    List<Menu> getAll();

    MenuDTO saveFrom(MenuDTO menuDTO);

    void deleteByVersion(Version version);

    enum MenuItemsStatus {
        PUBLIC,
        ALL
    }

}
