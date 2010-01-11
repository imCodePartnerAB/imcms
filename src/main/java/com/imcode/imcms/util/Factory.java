package com.imcode.imcms.util;

import com.imcode.imcms.api.Content;
import com.imcode.imcms.api.ContentLoop;
import com.imcode.imcms.api.I18nLanguage;
import imcode.server.document.DocumentReference;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDomainObject;
import imcode.server.document.textdocument.TreeSortKeyDomainObject;

/**
 * Frequently used objects factory.
 */
public class Factory {

    /**
     *
     * @param docId
     * @param docVersionNo
     * @param no
     * @return content loop with single content.
     */
    public static ContentLoop createLoop(Integer docId, Integer docVersionNo, Integer no) {
        ContentLoop loop = new ContentLoop();
        loop.setDocId(docId);
        loop.setDocVersionNo(docVersionNo);
        loop.setNo(no);

        Content content = new Content();

        content.setIndex(0);
        content.setOrderIndex(0);
        content.setEnabled(true);

        loop.getContents().add(content);

        return loop;
    }


    /**
     *
     * @param docId
     * @param docVersionNo
     * @param language
     * @param no
     * @return
     */
    public static TextDomainObject createText(Integer docId, Integer docVersionNo, I18nLanguage language, Integer no) {
        return createText(docId, docVersionNo, language, no, null);
    }

    
    /**
     *
     * @param docId
     * @param docVersionNo
     * @param language
     * @param no
     * @param contentIndex
     * @return
     */
    public static TextDomainObject createText(Integer docId, Integer docVersionNo, I18nLanguage language, Integer no, Integer contentIndex) {
        TextDomainObject text = new TextDomainObject();

        text.setDocId(docId);
        text.setDocVersionNo(docVersionNo);
        text.setLanguage(language);
        text.setNo(no);
        text.setContentIndex(contentIndex);

        return text;
    }

    /**
     * 
     * @param docId
     * @param docVersionNo
     * @param no
     * @param docRef
     * @return
     */
    public static MenuDomainObject createMenu(Integer docId, Integer docVersionNo, Integer no, DocumentReference docRef) {
        MenuDomainObject menu = new MenuDomainObject();
        menu.setDocId(docId);
        menu.setDocVersionNo(docVersionNo);
        menu.setNo(no);

        MenuItemDomainObject menuItem = new MenuItemDomainObject();
        menuItem.setSortKey(0);
        menuItem.setTreeSortIndex("");
        menuItem.setTreeSortKey(new TreeSortKeyDomainObject(menuItem.getTreeSortIndex()));
        menuItem.setDocumentReference(docRef);

        menu.addMenuItemUnchecked(menuItem);

        return menu;
    }
}
