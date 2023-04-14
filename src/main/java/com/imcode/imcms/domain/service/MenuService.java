package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.MenuDTO;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import com.imcode.imcms.persistence.entity.Menu;
import com.imcode.imcms.persistence.entity.Version;

import java.util.List;

public interface MenuService extends VersionedContentService, DeleterByDocumentId, MenuAsHtmlService {

    MenuDTO getMenuDTO(int docId, int menuIndex, String language, String typeSort);

    // TODO: Cover by tests
    List<MenuItemDTO> getSortedMenuItems(MenuDTO menuDTO, String langCode);

    List<MenuItemDTO> getVisibleMenuItems(int docId, int menuIndex, String language);

    List<MenuItemDTO> getVisibleMenuItems(int docId, int menuIndex, int versionNo, String language);

    List<MenuItemDTO> getPublicMenuItems(int docId, int menuIndex, String language);

    List<Menu> getAll();

    List<Menu> getByDocId(Integer docId);

    MenuDTO saveFrom(MenuDTO menuDTO);

    void deleteByVersion(Version version);

    enum MenuItemsStatus {
        PUBLIC,
        ALL
    }

}
