package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.persistence.entity.Version;

import java.util.Collection;
import java.util.List;

public interface MenuService {
    List<MenuItemDTO> getMenuItemsOf(int menuIndex, int docId);

    List<MenuItemDTO> getPublicMenuItemsOf(int menuIndex, int docId);

    MenuDTO saveFrom(MenuDTO menuDTO);

    Collection<MenuDTO> findAllByVersion(Version version);

    enum MenuItemsStatus {
        PUBLIC,
        ALL
    }
}
