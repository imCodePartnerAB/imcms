package com.imcode.imcms.components;

import com.imcode.imcms.domain.dto.MenuItemDTO;

import java.util.List;

public interface MenuElementHtmlWrapper {

    String DATA_META_ID_ATTRIBUTE = "data-meta-id";
    String DATA_INDEX_ATTRIBUTE = "data-index";
    String DATA_TREEKEY_ATTRIBUTE = "data-treekey";
    String DATA_LEVEL_ATTRIBUTE = "data-level";
    String DATA_SUBLEVELS_ATTRIBUTE = "data-sublvls";
    String DATA_ITEM_ACTIVE = "data-item-active";
    String DATA_TYPE = "data-type";
    String ATTRIBUTE_CLASS = "class";
    String BRANCH = "branch";
    String ITEM_ACTIVE = "item-active";
    String LEAF = "leaf";
    String IMCMS_MENU_CLASS = "imcms-menu";
    String IMCMS_MENU_BRANCH = "imcms-menu-" + BRANCH;
    String IMCMS_MENU_ITEM = "imcms-menu-item";
    String LVL_ELEMENT = "lvl";
    String ATTRIBUTE_DATA = "data";
    String ATTRIBUTE_WCAG = "wcag";
    String UL_TAG_CLOSE = "</ul>";
    String LI_TAG_CLOSE = "</li>";
    String LI_TAG_OPEN = "<li>";
    String UL_TAG_OPEN = "<ul>";
    String LINK_A_TAG = "a";

    String getTitleFromSingleTag(String content);

    String getTitleFromContent(String content);

    String getTagDataElement(String content);

    String getWrappedContent(String content, List<String> wrappers,
                             MenuItemDTO itemDTO, List<String> attributes,
                             boolean hasChildren, int docId);
}
