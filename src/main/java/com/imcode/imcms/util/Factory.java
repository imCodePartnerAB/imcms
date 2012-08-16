package com.imcode.imcms.util;

import com.imcode.imcms.api.ContentLoop;
import com.imcode.imcms.api.I18nLanguage;
import imcode.server.document.DocumentReference;
import imcode.server.document.textdocument.*;

import java.util.Collection;

/**
 * Frequently used objects' factory.
 */
public class Factory {

//    /**
//     *
//     * @param docId
//     * @param docVersionNo
//     * @param no
//     * @return content loop.
//     */
//    public static ContentLoop createContentLoop(Integer docId, Integer docVersionNo, Integer no) {
//        ContentLoop loop = newInstance(ContentLoop.class, docId, docVersionNo, no);
//
//        return loop;
//    }
//
//

//
//
//
//
//
//
//    public static MenuDomainObject createMenu(DocRef docRef, int no, DocumentReference documentReference) {
//        MenuDomainObject menu = new MenuDomainObject();
//        menu.setNo(no);
//        menu.setDocRef(docRef);
//
//        MenuItemDomainObject menuItem = new MenuItemDomainObject();
//        menuItem.setSortKey(0);
//        menuItem.setTreeSortIndex("");
//        menuItem.setTreeSortKey(new TreeSortKeyDomainObject(menuItem.getTreeSortIndex()));
//        menuItem.setDocumentReference(documentReference);
//
//        menu.addMenuItemUnchecked(menuItem);
//
//        return menu;
//    }


//    public static TextDomainObject createNextText(TextDocumentDomainObject doc) {
//        return createNextText(doc, null);
//    }
//
//    public static TextDomainObject createNextText(TextDocumentDomainObject doc, Integer contentIndex) {
//        return createText(doc.getId(), doc.getVersion().getNo(), getNextItemNo(doc.getTexts().values()), doc.getLanguage(), contentIndex, null);
//    }

//    public static ImageDomainObject createNextImage(TextDocumentDomainObject doc) {
//        return createNextImage(doc, null);
//    }
//
//    public static ImageDomainObject createNextImage(TextDocumentDomainObject doc, ContentRef contentRef) {
//        return createImage(doc.getId(), doc.getVersion().getNo(), doc.getLanguage(), getNextItemNo(doc.getImages().values()), contentRef);
//    }
//
//    public static MenuDomainObject createNextMenu(TextDocumentDomainObject doc, DocumentReference docRef) {
//        return createMenu(new DocRef(doc.getIdValue(), doc.getVersion().getNo()), getNextItemNo(doc.getMenus().values()), docRef);
//    }
//
//    public static ContentLoop createNextContentLoop(TextDocumentDomainObject doc) {
//        return createContentLoop(doc.getId(), doc.getVersion().getNo(), getNextItemNo(doc.getContentLoops().values()));
//    }
//
//    public static Integer getNextItemNo(Collection<? extends DocOrderedItem> items) {
//        int no = 0;
//
//        for (DocOrderedItem item: items) {
//            no = Math.max(no, item.getNo());
//        }
//
//        return no + 1;
//    }
//
//    public static <T extends DocItem & DocI18nItem> T newInstance(Class<T> clazz, Integer docId, I18nLanguage language) throws RuntimeException {
//        try {
//            T t = clazz.newInstance();
//
//            t.setDocId(docId);
//            t.setLanguage(language);
//
//            return t;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static <T extends DocVersionItem & DocI18nItem & DocOrderedItem> T newInstance(Class<T> clazz, Integer docId, Integer docVersionNo, I18nLanguage language, Integer no) throws RuntimeException {
//        T t = newInstance(clazz, docId, docVersionNo, no);
//
//        t.setLanguage(language);
//
//        return t;
//    }
//
//    public static <T extends DocVersionItem & DocOrderedItem> T newInstance(Class<T> clazz, Integer docId, Integer docVersionNo, Integer no) throws RuntimeException {
//        try {
//            T t = clazz.newInstance();
//
//            t.setDocId(docId);
//            t.setDocVersionNo(docVersionNo);
//            t.setNo(no);
//
//            return t;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
