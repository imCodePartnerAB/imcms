package com.imcode.imcms.dto;

import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject.TreeMenuItemDomainObject;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class MenuElementDTO implements Serializable {

    private final Integer id;
    private final String title;
    private final List<MenuElementDTO> children;

    private MenuElementDTO(Integer id, String title, List<MenuElementDTO> children) {
        this.id = id;
        this.title = title;
        this.children = children;
    }

    public static MenuElementDTO of(TreeMenuItemDomainObject treeMenuItem) {
        final MenuItemDomainObject menuItem = treeMenuItem.getMenuItem();

        final Integer id = menuItem.getId();
        final String title = menuItem.getDocument().getHeadline();
        final List<MenuElementDTO> children = treeMenuItem
                .getSubMenuItems()
                .stream()
                .map(MenuElementDTO::of)
                .collect(Collectors.toList());

        return new MenuElementDTO(id, title, children);
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
