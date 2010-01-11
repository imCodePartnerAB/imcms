package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentLabels;
import com.imcode.imcms.api.DocumentRequestInfo;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.util.Factory;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentReference;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.textdocument.MenuDomainObject;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

public class DocumentMapperTest {

    private DocumentMapper docMapper;

    private UserDomainObject admin;

    private I18nSupport i18nSupport;

    public static interface ReduceFn<T, E> {
        T invoke(T t, E e);      
    }

    public static <T, E> T reduce(ReduceFn<T, E> reduceFn, T t, Iterable<E> elements) {
        for (E e: elements) {
            t = reduceFn.invoke(t, e);
        }

        return t;
    }


    @BeforeClass
    public void setUpClass() {
        i18nSupport = Imcms.getI18nSupport();
        docMapper = Imcms.getServices().getDocumentMapper();
        admin = Imcms.getServices().verifyUser("admin", "admin");
    }


    @Test
    public DocumentDomainObject getMainWorkingDocumentInDefaultLanguage() {
        DocumentDomainObject doc = docMapper.getCustomDocument(1001, 0, i18nSupport.getDefaultLanguage());
        assertNotNull(doc);
        return doc;
    }

    @Test
    public void createDocumentOfTypeFromParent() throws Exception {
        DocumentDomainObject parentDoc = getMainWorkingDocumentInDefaultLanguage();
        DocumentDomainObject doc = docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, parentDoc, admin);

        List<DocumentLabels> labels = new LinkedList<DocumentLabels>();

        for (I18nLanguage lang: i18nSupport.getLanguages()) {
            DocumentLabels l = new DocumentLabels();

            l.setHeadline(":headline in:" + lang.getCode());
            l.setMenuImageURL(":url in:" + lang.getCode());
            l.setMenuText(":menuText in:" + lang.getCode());
        }

        docMapper.saveNewDocument(doc, labels, admin, true);
    }


    @Test(dependsOnMethods = {"createDocumentOfTypeFromParent"})
    public void updateMenuInsertDoc() throws Exception {
        TextDocumentDomainObject parentDoc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage();
        Integer menuNo = 1 + reduce(
                new ReduceFn<Integer, Integer>() {
                    public Integer invoke(Integer v1, Integer v2) { return Math.max(v1, v2); }
                },

                0,

                parentDoc.getMenus().keySet());
        
        DocumentDomainObject menuItemDoc = docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, parentDoc, admin);
        List<DocumentLabels> labels = new LinkedList<DocumentLabels>();

        for (I18nLanguage lang: i18nSupport.getLanguages()) {
            DocumentLabels l = new DocumentLabels();

            l.setHeadline(":headline in:" + lang.getCode());
            l.setMenuImageURL(":url in:" + lang.getCode());
            l.setMenuText(":menuText in:" + lang.getCode());
        }
        
        Integer menuItemDocId =  docMapper.saveNewDocument(menuItemDoc, labels, admin, true);

        DocumentReference docRef = docMapper.getDocumentReference(menuItemDoc);
        
        MenuDomainObject menu = Factory.createMenu(parentDoc.getId(), parentDoc.getVersion().getNo(), menuNo, docRef);

        docMapper.saveDocumentMenu(parentDoc, menu, admin);

        parentDoc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage();


        DocumentRequestInfo docRequestInfo = new DocumentRequestInfo();
        docRequestInfo.setUser(admin);
        docRequestInfo.setDocVersionMode(DocumentRequestInfo.DocVersionMode.WORKING);
        docRequestInfo.setLanguage(i18nSupport.getDefaultLanguage());

        Imcms.setRequestInfo(docRequestInfo);

        menu = parentDoc.getMenu(menuNo);

        assertNotNull(menu);
        MenuItemDomainObject[] menuItems = menu.getMenuItems();

        assertEquals(1, menuItems.length);
        assertEquals(menuItemDocId.intValue(), menuItems[0].getDocumentReference().getDocumentId());
    }
}


//;(deftest test-change-menu
//;  (testing "add menu"
//;    (let [text-doc (rt/get-working-doc *text-doc-id* *lang*)
//;          new-text-doc (.createDocumentOfTypeFromParent *doc-mapper* 2 text-doc *user*)
//;          menus (.getMenus text-doc)
//;          menu-no (inc (apply max 0 (keys menus)))
//;          menu (.setMenu text-doc menu-no (MenuDomainObject.))
//;
//;      (if (seq menus)
//;
//;          new-text-doc (.copyDocument *doc-mapper* text-doc *superadmin*)]
//;      (is new-text-doc))))
