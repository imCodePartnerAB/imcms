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

        if (attributes.isEmpty()) {
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
                buildChildrenMenuItemHtml(buildContentMenu.append(getBuiltUlWithClassHtml(2, listAttr)), currentParentItem.getChildren(),
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
        itemParentHtmlContent = getLiItemHtmlWithAttributes(itemParentHtmlContent, parentMenuItem,
                treeKey, hasChildren, dataLevel, attributes, null, true);

        final String htmlDataItem = attributes.isEmpty()
                ? String.format("<li>%s", parentMenuItem.getTitle())
                : itemParentHtmlContent.concat(">");

        return menuElementHtmlWrapper.getWrappedContent(htmlDataItem, wrappers, parentMenuItem);
    }

    private String getLiItemHtmlWithAttributes(String liItem, MenuItemDTO itemDTO,
                                               String treeKey, boolean hasChildren,
                                               Integer dataLvl, List<String> attributes,
                                               Integer indexCount, boolean useCurrentTreeKey) {
        final String dataTreeKey = useCurrentTreeKey ? treeKey : treeKey + "." + ((indexCount + 1) * 10);
        for (String attribute : attributes) {
            switch (attribute.trim()) {
                case ATTRIBUTE_CLASS:
                    liItem += getBuiltLiByClassAttrHtml(liItem, dataLvl, hasChildren);
                    break;
                case ATTRIBUTE_DATA:
                    liItem += getBuiltLiByDataAttrHtml(liItem, itemDTO, dataTreeKey, hasChildren, dataLvl);
                    break;
            }
        }
        return liItem;
    }

    private void buildChildrenMenuItemHtml(StringBuilder contentMenu, List<MenuItemDTO> childrenItems,
                                           String treeKey, Integer dataLvl,
                                           List<String> wrappers, List<String> attributes) {

        for (int i = 0; i < childrenItems.size(); i++) {
            final MenuItemDTO currentMenuItem = childrenItems.get(i);
            boolean hasChildren = !currentMenuItem.getChildren().isEmpty();
            String liItem = "";
            liItem = getLiItemHtmlWithAttributes(
                    liItem, currentMenuItem, treeKey, hasChildren, dataLvl, attributes, i, true);

            final String resultHtmlItem = attributes.isEmpty()
                    ? String.format("<li>%s", currentMenuItem.getTitle())
                    : liItem.concat(">");

            if (hasChildren) {
                contentMenu.append(menuElementHtmlWrapper.getWrappedContent(resultHtmlItem, wrappers, currentMenuItem));
                buildChildrenMenuItemHtml(contentMenu.append(getBuiltUlWithClassHtml(dataLvl + 1, attributes)), currentMenuItem.getChildren(),
                        treeKey + "." + ((i + 1) * 10), dataLvl + 1, wrappers, attributes);
            } else {
                contentMenu.append(getBuildAloneMenuItemHtml(currentMenuItem, dataLvl,
                        treeKey, wrappers, attributes, i));
            }
        }
        contentMenu.append(UL_TAG_CLOSE).append(LI_TAG_CLOSE);
    }

    private String getBuildAloneMenuItemHtml(MenuItemDTO itemDTO, Integer dataLvl,
                                             String treeKey, List<String> wrappers,
                                             List<String> attributes, Integer indexCount) {

        String itemHtmlContent = "";
        itemHtmlContent = getLiItemHtmlWithAttributes(
                itemHtmlContent, itemDTO, treeKey, false, dataLvl, attributes, indexCount, false);

        final String resultItemHtml = attributes.isEmpty()
                ? String.format("<li>%s", itemDTO.getTitle())
                : itemHtmlContent.concat(">");

        return menuElementHtmlWrapper.getWrappedContent(resultItemHtml, wrappers, itemDTO).concat(LI_TAG_CLOSE);
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

    private String getBuiltLiByClassAttrHtml(String contentItem, int subLvl, boolean hasChildren) {
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
