package com.imcode.imcms.mapping.container;

import imcode.server.document.textdocument.MenuDomainObject;

import java.util.Objects;

/**
 * Uniquely identifies menu in a text document.
 */
public class TextDocMenuContainer extends TextDocVersionedContainer {

    private final int menuNo;
    private final MenuDomainObject menu;
    public TextDocMenuContainer(VersionRef versionRef, int menuNo, MenuDomainObject menu) {
        super(versionRef);
        this.menu = Objects.requireNonNull(menu);
        this.menuNo = menuNo;
    }

    public static TextDocMenuContainer of(VersionRef versionRef, int menuNo, MenuDomainObject menu) {
        return new TextDocMenuContainer(versionRef, menuNo, menu);
    }

    public int getMenuNo() {
        return menuNo;
    }

    public MenuDomainObject getMenu() {
        return menu;
    }
}
