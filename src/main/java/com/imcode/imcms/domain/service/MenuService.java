package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Version;

import java.util.List;

public interface MenuService extends VersionedContentService, DeleterByDocumentId {

    List<MenuItemDTO> getMenuItems(int docId, int menuIndex, String language);

    /**
     * @param disableNested - false/true show nested in menu.
     * disableNested (true) - Will return all the items in the menu, no matter what level. All sub levels are empty.
     * ********************************************************************************
     * disableNested (false) - will get just the menu items of the first level. Nested items are in sub levels.
     *
     */
    List<MenuItemDTO> getVisibleMenuItems(int docId, int menuIndex, String language, boolean disableNested);

    List<MenuItemDTO> getPublicMenuItems(int docId, int menuIndex, String language, boolean disableNested);

    List<Menu> getAll();

    MenuDTO saveFrom(MenuDTO menuDTO);

    void deleteByVersion(Version version);

    enum MenuItemsStatus {
        PUBLIC,
        ALL
    }

}
