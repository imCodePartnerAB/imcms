package com.imcode.imcms.mapping.mapper;

import com.imcode.imcms.mapping.dto.MenuElementDTO;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject.TreeMenuItemDomainObject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TreeMenuItemDomainObjectToMenuElementDtoMapper implements Function<TreeMenuItemDomainObject, MenuElementDTO> {

    @Override
    public MenuElementDTO apply(TreeMenuItemDomainObject treeMenuItemDomainObject) {
        final MenuItemDomainObject menuItem = treeMenuItemDomainObject.getMenuItem();

        final Integer id = menuItem.getId();
        final String title = menuItem.getDocument().getHeadline();
        final List<MenuElementDTO> children = treeMenuItemDomainObject
                .getSubMenuItems()
                .stream()
                .map(this)
                .collect(Collectors.toList());

        return new MenuElementDTO(id, title, children);
    }

}
