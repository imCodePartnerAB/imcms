package com.imcode.imcms.components;

import com.imcode.imcms.domain.dto.MenuItemDTO;

import java.util.List;

public interface MenuElementHtmlWrapper {

    String DATA_META_ID_ATTRIBUTE = "data-meta-id";
    String DATA_INDEX_ATTRIBUTE = "data-index";
    String DATA_TREEKEY_ATTRIBUTE = "data-treekey";
    String DATA_LEVEL_ATTRIBUTE = "data-level";
    String DATA_SUBLEVELS_ATTRIBUTE = "data-sublvls";
    String CLASS_ATTRIBUTE = "class";
    String UL_TAG_CLOSE = "</ul>";
    String LI_TAG_CLOSE = "</li>";
    String UL_TAG_OPEN = "<ul>";
    String LINK_A_TAG = "a";

    String getTitleFromSingleTag(String content);

    String getTitleFromContent(String content);

    String getTagDataElement(String content);

    String getWrappedContent(String content, List<String> wrappers, MenuItemDTO itemDTO);
}
