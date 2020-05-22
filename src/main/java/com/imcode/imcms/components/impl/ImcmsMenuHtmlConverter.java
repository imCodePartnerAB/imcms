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
                : Arrays.asList(attributes.split(","));

        for (String attribute : listAttr) {
            switch (attribute.trim()) {
                case ATTRIBUTE_CLASS:
                    addStartBuildUlByClassAttr(buildContentMenu, menuIndex, docId);
                    break;

                case ATTRIBUTE_DATA:
                    addStartBuildUlByDataAttr(buildContentMenu, menuIndex, docId);
                    break;
            }
        }

        if (buildContentMenu.toString().isEmpty()) {
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
            String dataTreeKey = StringUtils.isBlank(treeKey) ? ((i + 1) * 10) + "" : treeKey + "." + ((i + 1) * 10);

            buildContentMenu.append(getBuiltMainParentMenuItem(currentParentItem, listAttr, dataTreeKey, 1, wrappers));

            if (!currentParentItem.getChildren().isEmpty()) {
                buildChildrenContentMenuItem(buildContentMenu.append(UL_TAG_OPEN), currentParentItem.getChildren(),
                        dataTreeKey, 2, wrappers, listAttr);
                buildContentMenu.append(UL_TAG_CLOSE).append(LI_TAG_CLOSE);
            }
        }
        buildContentMenu.append(UL_TAG_CLOSE);//main ul tag closed

        return buildContentMenu.toString();
    }

    @Override
    public String convertToMenuHtml(int docId, int menuIndex) {
        return null;
    }

    private String getBuiltMainParentMenuItem(MenuItemDTO parentMenuItem, List<String> attributes,
                                              String treeKey, Integer dataLevel, List<String> wrappers) {
        String itemParentHtmlContent = "";
        final boolean hasChildren = !parentMenuItem.getChildren().isEmpty();
        for (String attribute : attributes) {
            switch (attribute.trim()) {
                case ATTRIBUTE_CLASS:
                    itemParentHtmlContent += addBuildLiByClassAttr(itemParentHtmlContent, dataLevel, hasChildren);
                    break;
                case ATTRIBUTE_DATA:
                    itemParentHtmlContent += addBuildLiByDataAttr(itemParentHtmlContent, parentMenuItem, treeKey, hasChildren, dataLevel);
                    break;
            }
        }

        final String htmlDataItem = attributes.isEmpty()
                ? String.format("<li>%s", parentMenuItem.getTitle())
                : itemParentHtmlContent.concat(">");

        return wrapElement(parentMenuItem, wrappers, htmlDataItem);
    }

    private String wrapElement(MenuItemDTO parentItem, List<String> wrappers, String htmlContentItemElement) {//todo improve wrapper if empty!
        if (!wrappers.isEmpty()) {
            return menuElementHtmlWrapper.getWrappedContent(htmlContentItemElement, wrappers, parentItem);
        }
        return htmlContentItemElement.concat(parentItem.getTitle());
    }

    private void buildChildrenContentMenuItem(StringBuilder contentMenu, List<MenuItemDTO> childrenItems,
                                              String treeKey, Integer dataLvl,
                                              List<String> wrappers, List<String> attributes) {

        for (int i = 0; i < childrenItems.size(); i++) {
            final MenuItemDTO currentMenuItem = childrenItems.get(i);
            boolean hasChildren = !currentMenuItem.getChildren().isEmpty();
            String liItem = "";
            for (String attribute : attributes) {
                switch (attribute.trim()) {
                    case ATTRIBUTE_CLASS:
                        liItem += addBuildLiByClassAttr(liItem, dataLvl, hasChildren);
                        break;
                    case ATTRIBUTE_DATA:
                        liItem += addBuildLiByDataAttr(liItem,
                                currentMenuItem, treeKey + "." + ((i + 1) * 10),
                                hasChildren, dataLvl);
                        break;
                }
            }

            final String resultHtmlItem = attributes.isEmpty()
                    ? String.format("<li>%s", currentMenuItem.getTitle())
                    : liItem.concat(">");

            if (hasChildren) {
                contentMenu.append(wrapElement(currentMenuItem, wrappers, resultHtmlItem));
                buildChildrenContentMenuItem(contentMenu.append(UL_TAG_OPEN), currentMenuItem.getChildren(),
                        treeKey + "." + ((i + 1) * 10), dataLvl + 1, wrappers, attributes);
            } else {
                contentMenu.append(getBuildContentAloneMenuItem(currentMenuItem, dataLvl,
                        treeKey + "." + ((i + 1) * 10), wrappers, attributes));
            }
        }
        contentMenu.append(UL_TAG_CLOSE).append(LI_TAG_CLOSE);
    }

    private String getBuildContentAloneMenuItem(MenuItemDTO itemDTO, Integer dataLvl,
                                                String treeKey, List<String> wrappers, List<String> attributes) {

        String itemHtmlContent = "";
        for (String attribute : attributes) { //move to alone method
            switch (attribute.trim()) {
                case ATTRIBUTE_CLASS:
                    itemHtmlContent += addBuildLiByClassAttr(itemHtmlContent, dataLvl, false);
                    break;
                case ATTRIBUTE_DATA:
                    itemHtmlContent += addBuildLiByDataAttr(itemHtmlContent, itemDTO, treeKey, false, dataLvl);
                    break;
            }
        }

        final String resultItemHtml = attributes.isEmpty()
                ? String.format("<li>%s", itemDTO.getTitle())
                : itemHtmlContent.concat(">");

        return wrapElement(itemDTO, wrappers, resultItemHtml).concat(LI_TAG_CLOSE);
    }

    private String getBuiltUlHtml() {
        return null;
    }

    private void addStartBuildUlByClassAttr(StringBuilder buildContentMenu, int menuIndex, int docId) {
        if (buildContentMenu.toString().isEmpty()) {
            buildContentMenu.append(String.format("<ul class=\"%s %s %s %s--%d-%d\"",
                    IMCMS_MENU_CLASS, IMCMS_MENU_BRANCH,
                    LVL_ELEMENT + 1, IMCMS_MENU_CLASS,
                    menuIndex, docId));
        } else {
            buildContentMenu.append(String.format(" class=\"%s %s %s %s--%d-%d\"",
                    IMCMS_MENU_CLASS, IMCMS_MENU_BRANCH,
                    LVL_ELEMENT + 1, IMCMS_MENU_CLASS,
                    menuIndex, docId));
        }
    }

    private void addStartBuildUlByDataAttr(StringBuilder buildContentMenu, int menuIndex, int docId) {
        if (buildContentMenu.toString().isEmpty()) {
            buildContentMenu.append(String.format("<ul data-menu-index=\"%d\" data-doc-id=\"%d\"", menuIndex, docId));
        } else {
            buildContentMenu.append(String.format(" data-menu-index=\"%d\" data-doc-id=\"%d\"", menuIndex, docId));
        }
    }

    private String addBuildLiByDataAttr(String contentItem, MenuItemDTO menuItemDTO,
                                        String treeKey, boolean hasChildren, Integer dataLevel) {

        String buildContentItem = "";
        if (contentItem.isEmpty()) {
            buildContentItem = String.format(
                    menuHtmlPatterns.getPatternLiDataAttr(),
                    DATA_META_ID_ATTRIBUTE, menuItemDTO.getDocumentId(),
                    DATA_INDEX_ATTRIBUTE, menuItemDTO.getDataIndex(),
                    DATA_TREEKEY_ATTRIBUTE, treeKey,
                    DATA_LEVEL_ATTRIBUTE, dataLevel,
                    DATA_SUBLEVELS_ATTRIBUTE, hasChildren);
        } else {
            buildContentItem += (String.format(
                    menuHtmlPatterns.getPatternSimpleLiDataAttr(),
                    DATA_META_ID_ATTRIBUTE, menuItemDTO.getDocumentId(),
                    DATA_INDEX_ATTRIBUTE, menuItemDTO.getDataIndex(),
                    DATA_TREEKEY_ATTRIBUTE, treeKey,
                    DATA_LEVEL_ATTRIBUTE, dataLevel,
                    DATA_SUBLEVELS_ATTRIBUTE, hasChildren));
        }
        return buildContentItem;
    }

    private String addBuildLiByClassAttr(String contentItem, int subLvl, boolean hasChildren) {
        final String className = hasChildren ? BRANCH : LEAF;
        String buildContentItem = "";
        if (contentItem.isEmpty()) {
            buildContentItem = (String.format(menuHtmlPatterns.getPatternLiClassAttr(),
                    IMCMS_MENU_ITEM, LVL_ELEMENT + subLvl, className));
        } else {
            buildContentItem += (String.format(menuHtmlPatterns.getPatternSimpleClassAttr(),
                    IMCMS_MENU_ITEM, LVL_ELEMENT + subLvl, className));
        }

        return buildContentItem;
    }
}
