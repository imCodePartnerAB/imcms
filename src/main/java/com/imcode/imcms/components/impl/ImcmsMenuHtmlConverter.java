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
        final String ulData = attributesHasData(attributes)
                ? "<ul data-menu-index=\"%d\" data-doc-id=\"%d\">"
                : UL_TAG_OPEN;

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
            final MenuItemDTO currentParentItem = menuItems.get(i);
            String dataTreeKey = StringUtils.isBlank(treeKey) ? ((i + 1) * 10) + "" : treeKey + "." + ((i + 1) * 10);

            buildContentMenu.append(getBuiltMainParentMenuItem(currentParentItem, attributes, dataTreeKey, 1, wrappers));

            if (!currentParentItem.getChildren().isEmpty()) {
                buildChildrenContentMenuItem(buildContentMenu.append(UL_TAG_OPEN), currentParentItem.getChildren(),
                        dataTreeKey, 2, wrappers, attributes);
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

    private boolean attributesHasData(String attributes) {
        return attributes.contains("data");
    }

    private String getBuiltMainParentMenuItem(MenuItemDTO parentMenuItem, String attributes,
                                              String treeKey, Integer dataLevel, List<String> wrappers) {
        StringBuilder parentElementContent = new StringBuilder();
        final boolean hasChildren = !parentMenuItem.getChildren().isEmpty();
        String htmlContentItemElement;
        if (attributesHasData(attributes)) {
            htmlContentItemElement = String.format(
                    "<li %s=\"%d\" %s=\"%d\" %s=\"%s\" %s=\"%d\" %s=\"%s\">%s",
                    DATA_META_ID_ATTRIBUTE, parentMenuItem.getDocumentId(),
                    DATA_INDEX_ATTRIBUTE, parentMenuItem.getDataIndex(),
                    DATA_TREEKEY_ATTRIBUTE, treeKey,
                    DATA_LEVEL_ATTRIBUTE, dataLevel,
                    DATA_SUBLEVELS_ATTRIBUTE, hasChildren,
                    parentMenuItem.getTitle());

            final String wrappedContent = wrapElement(parentMenuItem, wrappers, htmlContentItemElement);
            addWrapElementToParentContentHtml(parentElementContent, hasChildren, wrappedContent);
        } else {
            htmlContentItemElement = String.format("<li>%s", parentMenuItem.getTitle());
            final String wrappedContent = wrapElement(parentMenuItem, wrappers, htmlContentItemElement);
            addWrapElementToParentContentHtml(parentElementContent, hasChildren, wrappedContent);
        }

        return parentElementContent.toString();
    }

    private String wrapElement(MenuItemDTO parentItem, List<String> wrappers, String htmlContentItemElement) {
        if (!wrappers.isEmpty()) {
            return menuElementHtmlWrapper.getWrappedContent(htmlContentItemElement, wrappers, parentItem);
        }
        return htmlContentItemElement;
    }

    private void addWrapElementToParentContentHtml(StringBuilder parentElementContent,
                                                   boolean hasChildren, String wrappedElement) {
        if (hasChildren) {
            parentElementContent.append(wrappedElement);
        } else {
            parentElementContent.append(wrappedElement).append(LI_TAG_CLOSE);
        }
    }

    private void buildChildrenContentMenuItem(StringBuilder contentMenu, List<MenuItemDTO> childrenItems,
                                              String treeKey, Integer dataLvl, List<String> wrappers, String attributes) {

        for (int i = 0; i < childrenItems.size(); i++) {
            final MenuItemDTO currentMenuItem = childrenItems.get(i);
            if (!currentMenuItem.getChildren().isEmpty()) {
                String htmlContentMenuItem = String.format(
                        "<li %s=\"%d\" %s=\"%d\" %s=\"%s\" %s=\"%d\" %s=\"%s\">%s",
                        DATA_META_ID_ATTRIBUTE, currentMenuItem.getDocumentId(),
                        DATA_INDEX_ATTRIBUTE, currentMenuItem.getDataIndex(),
                        DATA_TREEKEY_ATTRIBUTE, treeKey + "." + ((i + 1) * 10),
                        DATA_LEVEL_ATTRIBUTE, dataLvl,
                        DATA_SUBLEVELS_ATTRIBUTE, !currentMenuItem.getChildren().isEmpty(),
                        currentMenuItem.getTitle());

                if (!wrappers.isEmpty()) {
                    htmlContentMenuItem = menuElementHtmlWrapper.getWrappedContent(
                            htmlContentMenuItem, wrappers, currentMenuItem
                    );
                }
                contentMenu.append(htmlContentMenuItem);

                buildChildrenContentMenuItem(contentMenu.append(UL_TAG_OPEN), currentMenuItem.getChildren(),
                        treeKey + "." + ((i + 1) * 10), dataLvl + 1, wrappers, attributes);
            } else {
                contentMenu.append(getBuildContentMenuItem(currentMenuItem, dataLvl,
                        treeKey + "." + ((i + 1) * 10), wrappers, attributes));
            }
        }
        contentMenu.append(UL_TAG_CLOSE).append(LI_TAG_CLOSE);
    }

    private String getBuildContentMenuItem(MenuItemDTO itemDTO, Integer dataLvl,
                                           String treeKey, List<String> wrappers, String attributes) {
        String contentMenuItem = String.format(
                "<li %s=\"%d\" %s=\"%d\" %s=\"%s\" %s=\"%d\" %s=\"%s\">%s</li>",
                DATA_META_ID_ATTRIBUTE, itemDTO.getDocumentId(),
                DATA_INDEX_ATTRIBUTE, itemDTO.getDataIndex(),
                DATA_TREEKEY_ATTRIBUTE, treeKey,
                DATA_LEVEL_ATTRIBUTE, dataLvl,
                DATA_SUBLEVELS_ATTRIBUTE, !itemDTO.getChildren().isEmpty(),
                itemDTO.getTitle()).concat("\n");

        if (!wrappers.isEmpty()) {
            contentMenuItem = menuElementHtmlWrapper.getWrappedContent(contentMenuItem, wrappers, itemDTO);
        }

        return contentMenuItem;
    }
}
