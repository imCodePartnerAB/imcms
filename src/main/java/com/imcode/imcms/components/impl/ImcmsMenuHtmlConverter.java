package com.imcode.imcms.components.impl;

import com.imcode.imcms.components.MenuElementHtmlWrapper;
import com.imcode.imcms.components.MenuHtmlConverter;
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

    ImcmsMenuHtmlConverter(MenuElementHtmlWrapper menuElementHtmlWrapper) {
        this.menuElementHtmlWrapper = menuElementHtmlWrapper;
    }

    @Override
    public String convertToMenuHtml(int docId, int menuIndex, List<MenuItemDTO> menuItemDTOS,
                                    boolean nested, String attributes, String treeKey, String wrap) {

        List<MenuItemDTO> menuItems;
        String ulData = "<ul data-menu-index=\"%d\" data-doc-id=\"%d\">";
        StringBuilder buildContentMenu = new StringBuilder();
        buildContentMenu.append(ulData);

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
            String dataTreeKey = StringUtils.isBlank(treeKey) ? ((i + 1) * 10) + "" : treeKey + "." + ((i + 1) * 10);

            buildContentMenu.append(getBuiltMainParentMenuItem(menuItems.get(i), attributes, dataTreeKey, 1, wrappers));

            if (!menuItems.get(i).getChildren().isEmpty()) {
                buildChildrenContentMenuItem(buildContentMenu.append(UL_TAG_OPEN), menuItems.get(i).getChildren(),
                        dataTreeKey, 2, wrappers);
                buildContentMenu.append(UL_TAG_CLOSE).append(LI_TAG_CLOSE);
            }
        }
        buildContentMenu.append(UL_TAG_CLOSE);//main ul tag closed

        return String.format(buildContentMenu.toString(), menuIndex, docId);
    }

    @Override
    public String convertToMenuHtml(int docId, int menuIndex) {
        return null;
    }

    private String getBuiltMainParentMenuItem(MenuItemDTO parentMenuItem, String attributes,
                                              String treeKey, Integer dataLevel, List<String> wrappers) {
        StringBuilder content = new StringBuilder();
        final boolean hasChildren = !parentMenuItem.getChildren().isEmpty();
        if (attributes.contains("data")) {
            String htmlContentItemElement = String.format(
                    "<li %s=\"%d\" %s=\"%d\" %s=\"%s\" %s=\"%d\" %s=\"%s\">%s",
                    DATA_META_ID_ATTRIBUTE, parentMenuItem.getDocumentId(),
                    DATA_INDEX_ATTRIBUTE, parentMenuItem.getDataIndex(),
                    DATA_TREEKEY_ATTRIBUTE, treeKey,
                    DATA_LEVEL_ATTRIBUTE, dataLevel,
                    DATA_SUBLEVELS_ATTRIBUTE, hasChildren,
                    parentMenuItem.getTitle());


            htmlContentItemElement = menuElementHtmlWrapper.getWrappedContent(htmlContentItemElement, wrappers, parentMenuItem);

            if (hasChildren) {
                content.append(htmlContentItemElement);
            } else {
                content.append(htmlContentItemElement).append(LI_TAG_CLOSE);
            }
        }

        return content.toString();
    }

    private void buildChildrenContentMenuItem(StringBuilder contentMenu, List<MenuItemDTO> childrenItems,
                                              String treeKey, Integer dataLvl, List<String> wrappers) {

        StringBuilder contentMenuItems = new StringBuilder();
        for (int i = 0; i < childrenItems.size(); i++) {
            if (!childrenItems.get(i).getChildren().isEmpty()) {

                String htmlContentMenuItem = String.format(
                        "<li %s=\"%d\" %s=\"%d\" %s=\"%s\" %s=\"%d\" %s=\"%s\">%s",
                        DATA_META_ID_ATTRIBUTE, childrenItems.get(i).getDocumentId(),
                        DATA_INDEX_ATTRIBUTE, childrenItems.get(i).getDataIndex(),
                        DATA_TREEKEY_ATTRIBUTE, treeKey + "." + ((i + 1) * 10),
                        DATA_LEVEL_ATTRIBUTE, dataLvl,
                        DATA_SUBLEVELS_ATTRIBUTE, !childrenItems.get(i).getChildren().isEmpty(),
                        childrenItems.get(i).getDocumentId());

                htmlContentMenuItem = menuElementHtmlWrapper.getWrappedContent(htmlContentMenuItem, wrappers, childrenItems.get(i));
                contentMenu.append(htmlContentMenuItem);

                buildChildrenContentMenuItem(contentMenu.append(UL_TAG_OPEN), childrenItems.get(i).getChildren(),
                        treeKey + "." + ((i + 1) * 10), dataLvl + 1, wrappers);
            } else {
                contentMenuItems.append(getBuildContentMenuItem(childrenItems.get(i), dataLvl,
                        treeKey + "." + ((i + 1) * 10), wrappers));
            }
        }
        contentMenuItems.append(UL_TAG_CLOSE).append(LI_TAG_CLOSE);
        contentMenu.append(contentMenuItems);
    }

    private String getBuildContentMenuItem(MenuItemDTO itemDTO, Integer dataLvl, String treeKey, List<String> wrappers) {
        String contentMenuItem = String.format(
                "<li %s=\"%d\" %s=\"%d\" %s=\"%s\" %s=\"%d\" %s=\"%s\">%s</li>",
                DATA_META_ID_ATTRIBUTE, itemDTO.getDocumentId(),
                DATA_INDEX_ATTRIBUTE, itemDTO.getDataIndex(),
                DATA_TREEKEY_ATTRIBUTE, treeKey,
                DATA_LEVEL_ATTRIBUTE, dataLvl,
                DATA_SUBLEVELS_ATTRIBUTE, !itemDTO.getChildren().isEmpty(),
                itemDTO.getDocumentId()).concat("\n");

        contentMenuItem = menuElementHtmlWrapper.getWrappedContent(contentMenuItem, wrappers, itemDTO);

        return contentMenuItem;
    }
}
