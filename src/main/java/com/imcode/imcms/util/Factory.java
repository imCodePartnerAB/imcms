package com.imcode.imcms.util;

import com.imcode.imcms.api.ContentLoop;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.I18nLanguage;
import imcode.server.document.DocumentReference;
import imcode.server.document.textdocument.*;

import java.util.Collection;

/**
 * Frequently used objects' factory.
 */
public class Factory {

    public static I18nLanguage createLanguage(Integer id, String code, String name) {
        return createLanguage(id, code, name, true);
    }


    public static I18nLanguage createLanguage(Integer id, String code, String name, Boolean enabled) {
        return createLanguage(id, code, name, name, enabled);
    }
    

    public static I18nLanguage createLanguage(Integer id, String code, String name, String nativeName, Boolean enabled) {
        I18nLanguage language = new I18nLanguage();

        language.setId(id);
        language.setCode(code);
        language.setName(name);
        language.setNativeName(nativeName);
        language.setEnabled(enabled);

        return language;
    }


    public static I18nMeta createI18nMeta(Integer docId, I18nLanguage language) {
        I18nMeta i18nMeta = newInstance(I18nMeta.class, docId, language);

        i18nMeta.setHeadline("");
        i18nMeta.setMenuText("");
        i18nMeta.setMenuImageURL("");
        
        return i18nMeta;
    }

    /**
     *
     * @param docId
     * @param docVersionNo
     * @param no
     * @return content loop.
     */
    public static ContentLoop createContentLoop(Integer docId, Integer docVersionNo, Integer no) {
        ContentLoop loop = newInstance(ContentLoop.class, docId, docVersionNo, no);

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
    public static TextDomainObject createText(Integer docId, Integer docVersionNo, Integer no, I18nLanguage language) {
        return createText(docId, docVersionNo, no, language, null);
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
    public static TextDomainObject createText(Integer docId, Integer docVersionNo, Integer no, I18nLanguage language, Integer contentIndex) {
        TextDomainObject text = newInstance(TextDomainObject.class, docId, docVersionNo, language, no);
        text.setContentNo(contentIndex);

        return text;
    }




    public static ImageDomainObject createImage(Integer docId, Integer docVersionNo, I18nLanguage language, Integer no) {
        return createImage(docId, docVersionNo, language, no, null);
    }

    public static ImageDomainObject createImage(Integer docId, Integer docVersionNo, I18nLanguage language, Integer no, Integer contentIndex) {
        ImageDomainObject image = newInstance(ImageDomainObject.class, docId, docVersionNo, language, no);
        image.setContentNo(contentIndex);

        return image;
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
        MenuDomainObject menu = newInstance(MenuDomainObject.class, docId, docVersionNo, no);

        MenuItemDomainObject menuItem = new MenuItemDomainObject();
        menuItem.setSortKey(0);
        menuItem.setTreeSortIndex("");
        menuItem.setTreeSortKey(new TreeSortKeyDomainObject(menuItem.getTreeSortIndex()));
        menuItem.setDocumentReference(docRef);

        menu.addMenuItemUnchecked(menuItem);

        return menu;
    }


    public static TextDomainObject createNextText(TextDocumentDomainObject doc) {
        return createNextText(doc, null);
    }
    
    public static TextDomainObject createNextText(TextDocumentDomainObject doc, Integer contentIndex) {
        return createText(doc.getId(), doc.getVersion().getNo(), getNextItemNo(doc.getTexts().values()), doc.getLanguage(), contentIndex);
    }

    public static ImageDomainObject createNextImage(TextDocumentDomainObject doc) {
        return createNextImage(doc, null);
    }

    public static ImageDomainObject createNextImage(TextDocumentDomainObject doc, Integer contentIndex) {
        return createImage(doc.getId(), doc.getVersion().getNo(), doc.getLanguage(), getNextItemNo(doc.getImages().values()), contentIndex);
    }

    public static MenuDomainObject createNextMenu(TextDocumentDomainObject doc, DocumentReference docRef) {
        return createMenu(doc.getId(), doc.getVersion().getNo(), getNextItemNo(doc.getMenus().values()), docRef);
    }

    public static ContentLoop createNextContentLoop(TextDocumentDomainObject doc) {
        return createContentLoop(doc.getId(), doc.getVersion().getNo(), getNextItemNo(doc.getContentLoops().values()));
    }

    public static Integer getNextItemNo(Collection<? extends DocOrderedItem> items) {
        int no = 0;

        for (DocOrderedItem item: items) {
            no = Math.max(no, item.getNo());
        }

        return no + 1;
    }

    public static <T extends DocItem & DocI18nItem> T newInstance(Class<T> clazz, Integer docId, I18nLanguage language) throws RuntimeException {
        try {
            T t = clazz.newInstance();

            t.setDocId(docId);
            t.setLanguage(language);

            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }    

    public static <T extends DocVersionItem & DocI18nItem & DocOrderedItem> T newInstance(Class<T> clazz, Integer docId, Integer docVersionNo, I18nLanguage language, Integer no) throws RuntimeException {
        T t = newInstance(clazz, docId, docVersionNo, no);

        t.setLanguage(language);

        return t;
    }
    
    public static <T extends DocVersionItem & DocOrderedItem> T newInstance(Class<T> clazz, Integer docId, Integer docVersionNo, Integer no) throws RuntimeException {
        try {
            T t = clazz.newInstance();

            t.setDocId(docId);
            t.setDocVersionNo(docVersionNo);
            t.setNo(no);

            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
