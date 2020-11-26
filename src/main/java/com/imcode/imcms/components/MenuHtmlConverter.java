package com.imcode.imcms.components;

import com.imcode.imcms.domain.dto.MenuItemDTO;

import java.util.List;

public interface MenuHtmlConverter {

    String convertToMenuHtml(int docId, int menuIndex, List<MenuItemDTO> menuItemDTOS,
                             String attributes, String treeKey, String wrap);
}
