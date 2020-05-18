package com.imcode.imcms.components.impl;

import com.imcode.imcms.components.MenuHtmlConverter;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class ImcmsMenuHtmlConverter implements MenuHtmlConverter {


    @Override
    public String convertToMenuHtml(int docId, int menuIndex, List<MenuItemDTO> menuItemDTOS,
                                    boolean nested, String attributes, String treeKey, String wrap) {
        return null;
    }

    @Override
    public String convertToMenuHtml(int docId, int menuIndex) {
        return null;
    }
}
