package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Version;

import java.util.List;

public interface MenuService extends VersionedContentService, DeleterByDocumentId {

    List<MenuItemDTO> getMenuItems(int menuIndex, int docId, String language);

    /**
     * @param disableNested - false/true show nested in menu.
     *                      disableNested (true) - will get all menu items recursive, include children from the menuItem.
     *                      disableNested (false) - will get just menu items.
     */
    List<MenuItemDTO> getVisibleMenuItems(int menuIndex, int docId, String language, boolean disableNested);

    List<MenuItemDTO> getPublicMenuItems(int menuIndex, int docId, String language, boolean disableNested);

    List<Menu> getAll();

    MenuDTO saveFrom(MenuDTO menuDTO);

    void deleteByVersion(Version version);

    enum MenuItemsStatus {
        PUBLIC,
        ALL
    }

}
