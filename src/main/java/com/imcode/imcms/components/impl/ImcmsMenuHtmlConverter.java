package com.imcode.imcms.components.impl;

import com.imcode.imcms.components.MenuElementHtmlWrapper;
import com.imcode.imcms.components.MenuHtmlConverter;
import com.imcode.imcms.components.patterns.ImcmsMenuHtmlPatterns;
import com.imcode.imcms.domain.dto.MenuItemDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.imcode.imcms.components.MenuElementHtmlWrapper.*;

@Component
class ImcmsMenuHtmlConverter implements MenuHtmlConverter {

    private final MenuElementHtmlWrapper menuElementHtmlWrapper;
    private final ImcmsMenuHtmlPatterns menuHtmlPatterns;

    ImcmsMenuHtmlConverter(MenuElementHtmlWrapper menuElementHtmlWrapper) {
        this.menuElementHtmlWrapper = menuElementHtmlWrapper;
        this.menuHtmlPatterns = new ImcmsMenuHtmlPatterns();
    }

    @Override
    public String convertToMenuHtml(int docId, int menuIndex, List<MenuItemDTO> menuItemDTOS,
                                    boolean nested, String attributes, String treeKey, String wrap) {

        List<MenuItemDTO> menuItems;
        final StringBuilder buildContentMenu = new StringBuilder();
        final List<String> listAttr = StringUtils.isBlank(attributes)
                ? Collections.EMPTY_LIST
                : Arrays.stream(attributes.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        for (String attribute : listAttr) {
            switch (attribute.trim()) {
                case ATTRIBUTE_CLASS:
                    addStartBuildUlByClassAttr(buildContentMenu, menuIndex, docId);
                    break;

                case ATTRIBUTE_DATA:
                    addStartBuildUlByDataAttr(buildContentMenu, menuIndex, docId);
                    break;

                case ATTRIBUTE_WCAG:
                    buildContentMenu.append("<ul");
                    break;
            }
        }

        if (listAttr.isEmpty()) {
            buildContentMenu.append(UL_TAG_OPEN);
        } else {
            buildContentMenu.append(">");
        }

        if (nested) {
            menuItems = menuItemDTOS;
        } else {
            menuItems = menuItemDTOS.stream()
                    .flatMap(MenuItemDTO::flattened)
                    .collect(Collectors.toList());
        }

        List<String> wrappers = StringUtils.isBlank(wrap) ? Collections.EMPTY_LIST : Arrays.asList(wrap.split(","));
        Collections.reverse(wrappers);

        for (int i = 0; i < menuItems.size(); i++) {
            final MenuItemDTO currentParentItem = menuItems.get(i);
            final String dataTreeKey = StringUtils.isBlank(treeKey) ? ((i + 1) * 10) + "" : treeKey + "." + ((i + 1) * 10);
            final boolean hasChildren = !currentParentItem.getChildren().isEmpty();

            buildContentMenu.append(getBuiltMainParentMenuItem(docId, currentParentItem, listAttr, dataTreeKey, 1, wrappers));

            if (hasChildren) {
                final String ulData = getBuiltUlWithClassHtml(2, listAttr);
                buildChildrenMenuItemHtml(buildContentMenu.append(ulData), currentParentItem.getChildren(),
                        dataTreeKey, 2, wrappers, listAttr, docId);
                buildContentMenu.append(UL_TAG_CLOSE).append(LI_TAG_CLOSE);
            } else {
                buildContentMenu.append(LI_TAG_CLOSE);//close current parent item
            }
        }
        buildContentMenu.append(UL_TAG_CLOSE);//main ul tag closed

        return buildContentMenu.toString();
    }

    private String getBuiltMainParentMenuItem(int docId, MenuItemDTO parentMenuItem, List<String> attributes,
                                              String treeKey, Integer dataLevel, List<String> wrappers) {
        String itemParentHtmlContent = "";
        final boolean hasChildren = !parentMenuItem.getChildren().isEmpty();
        itemParentHtmlContent = getLiItemHtmlWithAttributes(itemParentHtmlContent, parentMenuItem,
                treeKey, hasChildren, dataLevel, attributes, docId);

        final String htmlDataItem = attributes.isEmpty()
                ? LI_TAG_OPEN
                : itemParentHtmlContent.concat(">");

        return menuElementHtmlWrapper.getWrappedContent(htmlDataItem, wrappers, parentMenuItem, attributes, hasChildren, docId);
    }

    private String getLiItemHtmlWithAttributes(String liItem, MenuItemDTO itemDTO,
                                               String treeKey, boolean hasChildren,
                                               Integer dataLvl, List<String> attributes, int docId) {
        final boolean isCurrentPageActive = itemDTO.getDocumentId() == docId;
        for (String attribute : attributes) {
            switch (attribute.trim()) {
                case ATTRIBUTE_CLASS:
                    liItem += getBuiltLiByClassAttrHtml(liItem, dataLvl, hasChildren, docId, itemDTO, isCurrentPageActive);
                    break;
                case ATTRIBUTE_DATA:
                    liItem += getBuiltLiByDataAttrHtml(liItem, itemDTO, treeKey, hasChildren, dataLvl, docId, isCurrentPageActive);
                    break;
                case ATTRIBUTE_WCAG:
                    if (attributes.size() == 1) { //need check for this pattern! else way will add above;
                        liItem = isCurrentPageActive ? menuHtmlPatterns.getLiTagSelectedPagePattern() : "<li"; // run this , and check !!!
                    }
            }
        }
        return liItem;
    }

    private void buildChildrenMenuItemHtml(StringBuilder contentMenu, List<MenuItemDTO> childrenItems,
                                           String treeKey, Integer dataLvl,
                                           List<String> wrappers, List<String> attributes, int docId) {

        for (int i = 0; i < childrenItems.size(); i++) {
            final MenuItemDTO currentMenuItem = childrenItems.get(i);
            final boolean hasChildren = !currentMenuItem.getChildren().isEmpty();
            final String dataTreeKey = treeKey + "." + ((i + 1) * 10);
            if (hasChildren) {
                String liItem = "";
                liItem = getLiItemHtmlWithAttributes(
                        liItem, currentMenuItem, dataTreeKey, hasChildren, dataLvl, attributes, docId);

                final String resultHtmlItem = attributes.isEmpty()
                        ? LI_TAG_OPEN
                        : liItem.concat(">");


                contentMenu.append(menuElementHtmlWrapper.getWrappedContent(
                        resultHtmlItem, wrappers, currentMenuItem, attributes, hasChildren, docId));
                final String ulData = getBuiltUlWithClassHtml(dataLvl + 1, attributes);
                buildChildrenMenuItemHtml(contentMenu.append(ulData), currentMenuItem.getChildren(),
                        dataTreeKey, dataLvl + 1, wrappers, attributes, docId);
            } else {
                contentMenu.append(getBuildAloneMenuItemHtml(currentMenuItem, dataLvl,
                        dataTreeKey, wrappers, attributes, docId));
            }
        }
        contentMenu.append(UL_TAG_CLOSE).append(LI_TAG_CLOSE);
    }

    private String getBuildAloneMenuItemHtml(MenuItemDTO itemDTO, Integer dataLvl,
                                             String treeKey, List<String> wrappers,
                                             List<String> attributes, int docId) {

        String itemHtmlContent = "";
        itemHtmlContent = getLiItemHtmlWithAttributes(
                itemHtmlContent, itemDTO, treeKey, false, dataLvl, attributes, docId);

        final String resultItemHtml = attributes.isEmpty()
                ? LI_TAG_OPEN
                : itemHtmlContent.concat(">");

        return menuElementHtmlWrapper.getWrappedContent(
                resultItemHtml, wrappers, itemDTO, attributes, false, docId).concat(LI_TAG_CLOSE);
    }

    private String getBuiltUlWithClassHtml(Integer lvl, List<String> attrs) {
        if (attrs.contains(ATTRIBUTE_CLASS)) {
            return String.format(menuHtmlPatterns.getPatternUlClassAttr(), IMCMS_MENU_BRANCH, LVL_ELEMENT + lvl);
        } else {
            return UL_TAG_OPEN;
        }
    }

    private void addStartBuildUlByClassAttr(StringBuilder buildContentMenu, int menuIndex, int docId) {
        if (buildContentMenu.toString().isEmpty()) {
            buildContentMenu.append(String.format(menuHtmlPatterns.getPatternStartUlClassAttr(),
                    IMCMS_MENU_CLASS, IMCMS_MENU_BRANCH,
                    LVL_ELEMENT + 1, IMCMS_MENU_CLASS,
                    menuIndex, docId));
        } else {
            buildContentMenu.append(String.format(menuHtmlPatterns.getPatternStartSimpleUlClassAttr(),
                    IMCMS_MENU_CLASS, IMCMS_MENU_BRANCH,
                    LVL_ELEMENT + 1, IMCMS_MENU_CLASS,
                    menuIndex, docId));
        }
    }

    private void addStartBuildUlByDataAttr(StringBuilder buildContentMenu, int menuIndex, int docId) {
        if (buildContentMenu.toString().isEmpty()) {
            buildContentMenu.append(String.format(menuHtmlPatterns.getPatternStartUlDataAttr(), menuIndex, docId));
        } else {
            buildContentMenu.append(String.format(menuHtmlPatterns.getPatternWithoutTagUlDataAttr(), menuIndex, docId));
        }
    }

    private String getBuiltLiByDataAttrHtml(String contentItem, MenuItemDTO menuItemDTO,
                                            String treeKey, boolean hasChildren,
                                            Integer dataLevel, int docId, boolean isCurrentPageActive) {

        final String type = hasChildren ? BRANCH : LEAF;
        String buildContentItem = "";
        if (contentItem.isEmpty()) {
            buildContentItem = String.format(
                    menuHtmlPatterns.getPatternLiDataAttr(),
                    DATA_META_ID_ATTRIBUTE, menuItemDTO.getDocumentId(),
                    DATA_INDEX_ATTRIBUTE, menuItemDTO.getDataIndex(),
                    DATA_TREEKEY_ATTRIBUTE, treeKey,
                    DATA_LEVEL_ATTRIBUTE, dataLevel,
                    DATA_SUBLEVELS_ATTRIBUTE, hasChildren,
                    DATA_TYPE, type,
                    DATA_ITEM_ACTIVE, isCurrentPageActive);
        } else {
            buildContentItem += (String.format(
                    menuHtmlPatterns.getPatternSimpleLiDataAttr(),
                    DATA_META_ID_ATTRIBUTE, menuItemDTO.getDocumentId(),
                    DATA_INDEX_ATTRIBUTE, menuItemDTO.getDataIndex(),
                    DATA_TREEKEY_ATTRIBUTE, treeKey,
                    DATA_LEVEL_ATTRIBUTE, dataLevel,
                    DATA_SUBLEVELS_ATTRIBUTE, hasChildren,
                    DATA_TYPE, type,
                    DATA_ITEM_ACTIVE, isCurrentPageActive));
        }
        return buildContentItem;
    }

    private String getBuiltLiByClassAttrHtml(String contentItem, int subLvl,
                                             boolean hasChildren, int docId, MenuItemDTO itemDTO, boolean isCurrentPageActive) {
        final String className = hasChildren ? BRANCH : LEAF;
        final String prefixItemActive = isCurrentPageActive ? ITEM_ACTIVE : "";
        String buildContentItem = "";
        if (contentItem.isEmpty()) {
            buildContentItem = (String.format(menuHtmlPatterns.getPatternLiClassAttr(),
                    IMCMS_MENU_ITEM, LVL_ELEMENT + subLvl, className, prefixItemActive));
        } else {
            buildContentItem += (String.format(menuHtmlPatterns.getPatternSimpleClassAttr(),
                    IMCMS_MENU_ITEM, LVL_ELEMENT + subLvl, className, prefixItemActive));
        }

        return buildContentItem;
    }
}
