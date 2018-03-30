package com.imcode.imcms.mapping.container;

import com.imcode.imcms.domain.dto.MenuDTO;

import java.util.Objects;

/**
 * Uniquely identifies menu in a text document.
 */
public class MenuContainer extends TextDocVersionedContainer {

    private final MenuDTO menu;

    public MenuContainer(VersionRef versionRef, MenuDTO menu) {
        super(versionRef);

        this.menu = Objects.requireNonNull(menu);
    }

    public static MenuContainer of(VersionRef versionRef, MenuDTO menu) {
        return new MenuContainer(versionRef, menu);
    }

    public MenuDTO getMenu() {
        return menu;
    }
}