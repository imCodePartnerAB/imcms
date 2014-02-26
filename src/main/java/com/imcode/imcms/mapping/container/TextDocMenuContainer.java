package com.imcode.imcms.mapping.container;

import imcode.server.document.textdocument.MenuDomainObject;

import java.util.Objects;

/**
 * Uniquely identifies menu in a text document.
 */
public class TextDocMenuContainer {

    public static TextDocMenuContainer of(DocVersionRef docVersionRef, int menuNo, MenuDomainObject menu) {
        return new TextDocMenuContainer(docVersionRef, menuNo, menu);
    }

    private final DocVersionRef docVersionRef;
    private final int menuNo;
    private final MenuDomainObject menu;

    public TextDocMenuContainer(DocVersionRef docVersionRef, int menuNo, MenuDomainObject menu) {
        Objects.requireNonNull(docVersionRef);
        Objects.requireNonNull(menu);

        this.docVersionRef = docVersionRef;
        this.menuNo = menuNo;
        this.menu = menu;
    }

    public int getMenuNo() {
        return menuNo;
    }

    public MenuDomainObject getMenu() {
        return menu;
    }

    public DocVersionRef getDocVersionRef() {
        return docVersionRef;
    }

    public int getDocId() {
        return docVersionRef.getDocId();
    }

    public int getDocVersionNo() {
        return docVersionRef.getDocVersionNo();
    }
}

