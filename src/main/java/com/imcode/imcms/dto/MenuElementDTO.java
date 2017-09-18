package com.imcode.imcms.dto;

import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject.TreeMenuItemDomainObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuElementDTO {

    private Integer id;
    private String title;
    private List<MenuElementDTO> children = new ArrayList<>();

    public MenuElementDTO() {
    }

    public static MenuElementDTO of(TreeMenuItemDomainObject treeMenuItem) {
        final MenuItemDomainObject menuItem = treeMenuItem.getMenuItem();

        final MenuElementDTO result = new MenuElementDTO();
        result.id = menuItem.getId();
        result.title = menuItem.getDocument().getHeadline();
        result.children = treeMenuItem
                .getSubMenuItems()
                .stream()
                .map(MenuElementDTO::of)
                .collect(Collectors.toList());

        return result;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<MenuElementDTO> getChildren() {
        return children;
    }

}
