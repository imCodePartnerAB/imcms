package com.imcode.imcms.components;

import com.imcode.imcms.domain.dto.MenuItemDTO;

import java.util.List;

public interface MenuElementHtmlWrapper {

    String getTitleFromSingleTag(String content);

    String getTitleFromContent(String content);

    String getTagDataElement(String content);

    String getWrappedContent(String content, List<String> wrappers, MenuItemDTO itemDTO);
}
