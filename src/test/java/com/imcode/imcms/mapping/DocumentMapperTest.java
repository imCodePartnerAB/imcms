package com.imcode.imcms.mapping;

import com.imcode.imcms.api.*;
import com.imcode.imcms.util.Factory;

import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentReference;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.textdocument.*;
import imcode.server.user.UserDomainObject;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.*;

public class DocumentMapperTest {

    private DocumentMapper docMapper;

    private UserDomainObject admin;

    private I18nSupport i18nSupport;


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
    

    @Test//(dependsOnMethods = {"createDocumentOfTypeFromParent"})
    public void addMenu() throws Exception {
        TextDocumentDomainObject parentDoc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage();
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
        
        MenuDomainObject menu = Factory.createNextMenu(parentDoc, docRef);
        Integer menuNo = menu.getNo();

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


    @Test(dataProvider = "saveNewDocumentCopyFlag")
    public void saveNewDocumentWithLabels(boolean copyFlag) throws Exception {
        DocumentDomainObject parentDoc = getMainWorkingDocumentInDefaultLanguage();
        DocumentDomainObject doc = docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, parentDoc, admin);

        List<DocumentLabels> labels = new LinkedList<DocumentLabels>();

        for (I18nLanguage lang: i18nSupport.getLanguages()) {
            DocumentLabels l = new DocumentLabels();

            l.setHeadline(":headline in:" + lang.getCode());
            l.setMenuImageURL(":url in:" + lang.getCode());
            l.setMenuText(":menuText in:" + lang.getCode());
        }

        docMapper.saveNewDocument(doc, labels, admin, copyFlag);
    }


    @Test(dataProvider = "saveNewDocumentCopyFlag")
    public void saveNewDocumentWithoutLabels(boolean copyFlag) throws Exception {
        TextDocumentDomainObject parentDoc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage();
        TextDocumentDomainObject newDoc = (TextDocumentDomainObject)docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, parentDoc, admin);

        Integer newDocId =  docMapper.saveNewDocument(newDoc, admin, copyFlag);
    }

    @Test
    public void copyTextDocument() throws Exception {
        TextDocumentDomainObject doc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage();
        TextDocumentDomainObject docCopy = (TextDocumentDomainObject)docMapper.copyDocument(doc, admin);

        assertEquals(doc.getLanguage(), docCopy.getLanguage());
    }

    @Test(dataProvider = "contentInfo")
    public void insertTextDocumentText(Integer contentLoopNo, Integer contentIndex) throws Exception {
        TextDocumentDomainObject doc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage();
        TextDomainObject text = Factory.createNextText(doc);

        text.setContentLoopNo(contentLoopNo);
        text.setContentIndex(contentIndex);        

        docMapper.saveText(doc, text, admin);
    }

    @Test(dataProvider = "contentInfo")
    public void insertTextDocumentImage(Integer contentLoopNo, Integer contentIndex) throws Exception {
        TextDocumentDomainObject doc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage();
        ImageDomainObject image = Factory.createNextImage(doc);

        image.setSource(new NullImageSource());
        image.setContentLoopNo(contentLoopNo);
        image.setContentIndex(contentIndex);

        docMapper.saveImage(doc, image, admin);
    }


    @DataProvider
    public Object[][] saveNewDocumentCopyFlag() {
        return new Object [][] {{true}, {false }};
    }

    /**
     * Return content loop no and content index:
     */
    @DataProvider
    public Object[][] contentInfo() {
        TextDocumentDomainObject doc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage();
        ContentLoop existingContentLoop = doc.getContentLoops().values().iterator().next();
        ContentLoop unsavedContentLoop = Factory.createNextContentLoop(doc);
        
        Integer noContentLoopNo = null;
        Integer noContentIndex = null;

        Integer existingContentLoopNo = existingContentLoop.getNo();
        Integer existingContentIndex = existingContentLoop.getContents().get(0).getIndex();

        Integer unsavedContentLoopNo = unsavedContentLoop.getNo();
        Integer unsavedContentIndex = unsavedContentLoop.getContents().get(0).getIndex();        

        return new Object [][] {
                {noContentLoopNo, noContentIndex},
                {existingContentLoopNo, existingContentIndex},
                {unsavedContentLoopNo, unsavedContentIndex}
        };
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
