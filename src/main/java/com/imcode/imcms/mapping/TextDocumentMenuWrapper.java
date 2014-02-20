package com.imcode.imcms.mapping;

import imcode.server.document.textdocument.MenuDomainObject;

/**
 * Uniquely identifies menu in a text document.
 */
public class TextDocumentMenuWrapper {

    public static TextDocumentMenuWrapper of(DocVersionRef docVersionRef, int menuNo, MenuDomainObject menu) {
        return new TextDocumentMenuWrapper(docVersionRef, menuNo, menu);
    }

    private final DocVersionRef docVersionRef;
    private final int menuNo;
    private final MenuDomainObject menu;

    public TextDocumentMenuWrapper(DocVersionRef docVersionRef, int menuNo, MenuDomainObject menu) {
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

