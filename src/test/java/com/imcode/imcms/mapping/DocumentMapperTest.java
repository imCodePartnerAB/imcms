package com.imcode.imcms.mapping;

import com.imcode.imcms.api.*;
import com.imcode.imcms.util.Factory;

import imcode.server.Imcms;
import imcode.server.document.*;
import imcode.server.document.textdocument.*;
import imcode.server.user.UserDomainObject;

import imcode.util.io.FileInputStreamSource;
import imcode.util.io.InputStreamSource;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;
import static org.testng.Assert.assertSame;

@Test(groups = "runtime")
public class DocumentMapperTest {

    private DocumentMapper docMapper;

    private UserDomainObject admin;

    private UserDomainObject user;

    private I18nSupport i18nSupport;


    @BeforeClass
    public void setUpClass() {
        Imcms.start();
        i18nSupport = Imcms.getI18nSupport();
        docMapper = Imcms.getServices().getDocumentMapper();
        admin = Imcms.getServices().verifyUser("admin", "admin");
        user = Imcms.getServices().verifyUser("user", "user");
    }

    
    @Test(enabled = true)
    public void saveNewTextDocument() throws Exception {
        saveNewTextDocumentFn();
    }


    @Test(enabled = true)
    public void saveNewUrlDocument() throws Exception {
        saveNewUrlDocumentFn();
    }


    @Test(enabled = true)
    public void saveNewHtmlDocument() throws Exception {
        saveNewHtmlDocumentFn();
    }


    @Test(enabled = true)
    public void saveNewFileDocument() throws Exception {
        saveNewFileDocumentFn();
    }
    

    public TextDocumentDomainObject saveNewTextDocumentFn() throws Exception {
        DocumentDomainObject parentDoc = getMainWorkingDocumentInDefaultLanguage(true);
        TextDocumentDomainObject newDoc = (TextDocumentDomainObject)docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, parentDoc, admin);

        return docMapper.saveNewDocument(newDoc, admin);
    }

    
    public UrlDocumentDomainObject saveNewUrlDocumentFn() throws Exception {
        DocumentDomainObject parentDoc = getMainWorkingDocumentInDefaultLanguage(true);
        UrlDocumentDomainObject newDoc = (UrlDocumentDomainObject)docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.URL_ID, parentDoc, admin);

        return docMapper.saveNewDocument(newDoc, admin);
    }


    public HtmlDocumentDomainObject saveNewHtmlDocumentFn() throws Exception {
        DocumentDomainObject parentDoc = getMainWorkingDocumentInDefaultLanguage(true);
        HtmlDocumentDomainObject newDoc = (HtmlDocumentDomainObject)docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.HTML_ID, parentDoc, admin);

        return docMapper.saveNewDocument(newDoc, admin);
    }    


    public FileDocumentDomainObject saveNewFileDocumentFn() throws Exception {
        DocumentDomainObject parentDoc = getMainWorkingDocumentInDefaultLanguage(true);
        FileDocumentDomainObject newDoc = (FileDocumentDomainObject)docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.FILE_ID, parentDoc, admin);
        FileDocumentDomainObject.FileDocumentFile file = new FileDocumentDomainObject.FileDocumentFile();


        file.setFilename("file-doc-file.txt");
        file.setMimeType("text");
        file.setCreatedAsImage(false);

        file.setInputStreamSource(new InputStreamSource() {
            ByteArrayInputStream bin = new ByteArrayInputStream("test content".getBytes());

            public InputStream getInputStream() throws IOException {
                return bin;
            }

            public long getSize() throws IOException {
                return bin.available();
            }
        });


        newDoc.addFile("testFile", file);
        
        return docMapper.saveNewDocument(newDoc, admin);
    }    
    

    @Test(enabled = true)
    public void saveTextDocument() throws Exception {
        DocumentDomainObject doc = saveNewTextDocumentFn();

        docMapper.saveDocument(doc, admin);
    }


    //@Test(enabled = true, expectedExceptions = NoPermissionToEditDocumentException.class)
    @Test
    public void saveTextDocumentNoPermissions() throws Exception {
        DocumentDomainObject doc = saveNewTextDocumentFn();

        docMapper.saveDocument(doc, user);
    }


    @Test(enabled = true)
    public void saveHtmlDocument() throws Exception {
        DocumentDomainObject doc = saveNewHtmlDocumentFn();

        docMapper.saveDocument(doc, admin);
    }

    @Test(enabled = true)
    public void saveUrlDocument() throws Exception {
        DocumentDomainObject doc = saveNewUrlDocumentFn();

        docMapper.saveDocument(doc, admin);
    }

    
    @Test(enabled = true)
    public void saveFileDocument() throws Exception {
        DocumentDomainObject doc = saveNewFileDocumentFn();

        docMapper.saveDocument(doc, admin);
    }


    @Test(enabled = true)
    public void addMenu() {
        fail("NOT IMPLEMENTED");
    }

//    @Test(enabled = true)//(dependsOnMethods = {"createDocumentOfTypeFromParent"})
//    public void addMenu() throws Exception {
//        TextDocumentDomainObject parentDoc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage(true);
//        DocumentDomainObject menuItemDoc = docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, parentDoc, admin);
//        List<DocumentLabels> labels = new LinkedList<DocumentLabels>();
//
//        for (I18nLanguage lang: i18nSupport.getLanguages()) {
//            DocumentLabels l = new DocumentLabels();
//
//            l.setHeadline(":headline in:" + lang.getCode());
//            l.setMenuImageURL(":url in:" + lang.getCode());
//            l.setMenuText(":menuText in:" + lang.getCode());
//        }
//
//        Integer menuItemDocId =  docMapper.saveNewDocument(menuItemDoc, labels, admin, true);
//        DocumentReference docRef = docMapper.getDocumentReference(menuItemDoc);
//
//        MenuDomainObject menu = Factory.createNextMenu(parentDoc, docRef);
//        Integer menuNo = menu.getNo();
//
//        docMapper.saveDocumentMenu(parentDoc, menu, admin);
//
//        parentDoc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage(true);
//
//
//        DocumentRequest docRequestInfo = new DocumentRequest.WorkingDocRequest(admin);
//        docRequestInfo.setLanguage(i18nSupport.getDefaultLanguage());
//        Imcms.setUserDocRequest(docRequestInfo);
//
//        menu = parentDoc.getMenu(menuNo);
//
//        assertNotNull(menu);
//        MenuItemDomainObject[] menuItems = menu.getMenuItems();
//
//        assertEquals(1, menuItems.length);
//        assertEquals(menuItemDocId.intValue(), menuItems[0].getDocumentReference().getDocumentId());
//    }





    @Test(enabled = false, expectedExceptions = NoPermissionToAddDocumentToMenuException.class)
    public void copyTextDocumentNoPermission() throws Exception {
        TextDocumentDomainObject doc = saveNewTextDocumentFn();

        TextDocumentDomainObject docCopy = docMapper.copyDocument(doc, user);
    }


    @Test(enabled = true)
    public void copyTextDocument() throws Exception {
        TextDocumentDomainObject doc = saveNewTextDocumentFn();

        TextDocumentDomainObject docCopy = docMapper.copyDocument(doc, admin);
    }



    @Test(enabled = true)
    public void copyHtmlsDocument() throws Exception {
        HtmlDocumentDomainObject doc = saveNewHtmlDocumentFn();

        HtmlDocumentDomainObject docCopy = docMapper.copyDocument(doc, admin);
    }


    @Test(enabled = true)
    public void copyUrlDocument() throws Exception {
        UrlDocumentDomainObject doc = saveNewUrlDocumentFn();

        UrlDocumentDomainObject docCopy = docMapper.copyDocument(doc, admin);
    }
    

    @Test(enabled = true)
    public void copyFileDocument() throws Exception {
        FileDocumentDomainObject doc = saveNewFileDocumentFn();

        FileDocumentDomainObject docCopy = docMapper.copyDocument(doc, admin);
    }    
    

    @Test(enabled = true, dataProvider = "contentInfo")
    public void insertTextDocumentText(Integer contentLoopNo, Integer contentIndex) throws Exception {
        TextDocumentDomainObject doc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage(true);
        TextDomainObject text = Factory.createNextText(doc);

        text.setContentLoopNo(contentLoopNo);
        text.setContentNo(contentIndex);

        if (contentLoopNo != null) {
            ContentLoop loop = doc.getContentLoop(contentLoopNo);

            if (loop == null) {
                loop = Factory.createContentLoop(doc.getId(), doc.getVersion().getNo(), contentLoopNo);
                Content content = loop.addFirstContent();

                text.setContentNo(content.getNo());
                doc.setContentLoop(contentLoopNo, loop);
            }

            doc.setText(text.getNo(), text);
        }

        docMapper.saveTextDocumentText(doc, text, admin);
    }


    @Test(enabled = true)
    public void changeDocumentDefaultVersion() throws Exception {
        DocumentDomainObject parentDoc = getMainWorkingDocumentInDefaultLanguage(true);
        DocumentDomainObject doc = docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, parentDoc, admin);

        Integer docId = docMapper.saveNewDocument(doc, admin).getId();
        DocumentVersionInfo vi = docMapper.getDocumentVersionInfo(docId);

        doc = docMapper.getDefaultDocument(docId, i18nSupport.getDefaultLanguage());

        assertNotNull(doc, "New document is exists");
        assertEquals(doc.getVersion().getNo(), new Integer(0), "Default version of a new document is 0.");

        DocumentVersion version = docMapper.makeDocumentVersion(docId, admin);

        assertEquals(version.getNo(), new Integer(1), "New version no is 1.");

        docMapper.changeDocumentDefaultVersion(docId, 1, admin);

        doc = docMapper.getDefaultDocument(docId, i18nSupport.getDefaultLanguage());

        assertEquals(doc.getVersion().getNo(), new Integer(1), "Default version of a document is 1.");
    }


    @Test(enabled = true, dataProvider = "contentInfo")
    public void insertTextDocumentImage(Integer contentLoopNo, Integer contentIndex) throws Exception {
        TextDocumentDomainObject doc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage(true);
        ImageDomainObject image = Factory.createNextImage(doc);

        image.setSource(new NullImageSource());
        image.setContentLoopNo(contentLoopNo);
        image.setContentNo(contentIndex);

        docMapper.saveTextDocumentImage(doc, image, admin);
    }


    @Test(enabled = true)
    public void getDocuments() throws Exception {
        List<Integer> ids = docMapper.getAllDocumentIds();
        List<DocumentDomainObject> docs = docMapper.getDocuments(ids);

        assertEquals(ids.size(), docs.size());
    }


    @Test(enabled = true)
    public void getTextDocument() {
        getMainWorkingDocumentInDefaultLanguage(true);
    }


    @Test(enabled = true)
    public void getFileDocument() {
        fail("NOT IMPLEMENTED");
    }


    @Test(enabled = true)
    public void getHtmlDocument() {
        fail("NOT IMPLEMENTED");
    }


    @Test(enabled = true)
    public void getUrlDocument() {
        fail("NOT IMPLEMENTED");
    }
    

    @Test(enabled = true)
    public void makeTextDocumentVersion() throws Exception {
        TextDocumentDomainObject workingVersionDoc = getMainWorkingDocumentInDefaultLanguage(true);

        DocumentVersionInfo info = docMapper.getDocumentVersionInfo(workingVersionDoc.getId());

        docMapper.makeDocumentVersion(workingVersionDoc.getId(), admin);

        DocumentVersionInfo newInfo = docMapper.getDocumentVersionInfo(workingVersionDoc.getId());
        Integer expectedNewVersionNo = info.getLatestVersion().getNo() + 1;
        
        assertEquals(info.getVersionsCount() + 1, newInfo.getVersionsCount());
        assertEquals(newInfo.getLatestVersion().getNo(), expectedNewVersionNo);

        TextDocumentDomainObject newVersionDoc = (TextDocumentDomainObject)docMapper.getCustomDocument(workingVersionDoc.getId(), expectedNewVersionNo);

        assertNotNull(newVersionDoc);
    }


    /**
     * Saves document's content (all expect meta).
     */
    @Test
    public void saveDocumentContent() {
        fail("NOT IMPLEMENTED");
    }


    @Test(enabled = true)
    public void makeHtmlDocumentVersion() throws Exception {
        HtmlDocumentDomainObject doc = saveNewHtmlDocumentFn();

        DocumentVersionInfo info = docMapper.getDocumentVersionInfo(doc.getId());

        docMapper.makeDocumentVersion(doc.getId(), admin);

        DocumentVersionInfo newInfo = docMapper.getDocumentVersionInfo(doc.getId());
        Integer expectedNewVersionNo = info.getLatestVersion().getNo() + 1;

        assertEquals(info.getVersionsCount() + 1, newInfo.getVersionsCount());
        assertEquals(newInfo.getLatestVersion().getNo(), expectedNewVersionNo);

        HtmlDocumentDomainObject newVersionDoc = (HtmlDocumentDomainObject)docMapper.getCustomDocument(doc.getId(), expectedNewVersionNo);

        assertNotNull(newVersionDoc);
    }


    @Test(enabled = true)
    public void makeUrlDocumentVersion() throws Exception {
        UrlDocumentDomainObject doc = saveNewUrlDocumentFn();

        DocumentVersionInfo info = docMapper.getDocumentVersionInfo(doc.getId());

        docMapper.makeDocumentVersion(doc.getId(), admin);

        DocumentVersionInfo newInfo = docMapper.getDocumentVersionInfo(doc.getId());
        Integer expectedNewVersionNo = info.getLatestVersion().getNo() + 1;

        assertEquals(info.getVersionsCount() + 1, newInfo.getVersionsCount());
        assertEquals(newInfo.getLatestVersion().getNo(), expectedNewVersionNo);

        UrlDocumentDomainObject newVersionDoc = (UrlDocumentDomainObject)docMapper.getCustomDocument(doc.getId(), expectedNewVersionNo);

        assertNotNull(newVersionDoc);
    }


    @Test(enabled = true)
    public void makeFileDocumentVersion() throws Exception {
        FileDocumentDomainObject doc = saveNewFileDocumentFn();

        DocumentVersionInfo info = docMapper.getDocumentVersionInfo(doc.getId());
        DocumentVersion docVersionNew = docMapper.makeDocumentVersion(doc.getId(), admin);

        DocumentVersionInfo infoNew = docMapper.getDocumentVersionInfo(doc.getId());
        Integer expectedNewVersionNo = info.getLatestVersion().getNo() + 1;

        assertEquals(info.getVersionsCount() + 1, infoNew.getVersionsCount());
        assertEquals(infoNew.getLatestVersion().getNo(), expectedNewVersionNo);

        FileDocumentDomainObject docNew = (FileDocumentDomainObject)docMapper.getCustomDocument(doc.getId(), expectedNewVersionNo);

        assertNotNull(docNew);
        assertEquals(doc.getId(), docNew.getId());

        Map<String, FileDocumentDomainObject.FileDocumentFile> docFiles = doc.getFiles();
        Map<String, FileDocumentDomainObject.FileDocumentFile> docFilesNew = docNew.getFiles();

        assertEquals(docFiles.size(), 1);
        assertEquals(docFilesNew.size(), 1);

        Map.Entry<String, FileDocumentDomainObject.FileDocumentFile> docFileEntry = docFiles.entrySet().iterator().next();
        Map.Entry<String, FileDocumentDomainObject.FileDocumentFile> docFileEntryNew = docFilesNew.entrySet().iterator().next();


        FileDocumentDomainObject.FileDocumentFile docFile = docFileEntry.getValue();
        FileDocumentDomainObject.FileDocumentFile docFileNew = docFileEntryNew.getValue();
        
        assertEquals(docFile.getId(), docFile.getId());
        assertEquals(docFile.getFilename(), docFile.getFilename());

        //assertEquals(docFile.getInputStreamSource(), docFileNew.getInputStreamSource());

        String fileId = docFileEntry.getKey();
        String fileIdNew = docFileEntryNew.getKey();


        File file = DocumentStoringVisitor.getFileForFileDocumentFile(doc.getId(), doc.getVersionNo(), fileId);
        File fileNew = DocumentStoringVisitor.getFileForFileDocumentFile(docNew.getId(), docNew.getVersionNo(), fileIdNew);

        assertTrue(file.exists());
        assertTrue(fileNew.exists());

        assertEquals(file.getName(), doc.getId() + "." + fileId);
        assertEquals(fileNew.getName(), docNew.getId() + "_1." + fileId);
    }


    @Test
    public void getDocumentVersionInfo() throws Exception {
        fail("NOT IMPLEMENTED");
    }

    
    @Test
    public void saveTextDocumentText() throws Exception {
        fail("NOT IMPLEMENTED");
    }


    @Test
    public void saveTextDocumentImage() throws Exception {
        fail("NOT IMPLEMENTED");
    }


    @Test
    public void saveTextDocumentContentLoop() throws Exception {
        TextDocumentDomainObject doc = saveNewTextDocumentFn();

        ContentLoop loop = new ContentLoop();
        loop.addFirstContent();

        doc.setContentLoop(0, loop);

        docMapper.saveDocument(doc, admin);
    }


    @Test
    public void invalidateDocument() throws Exception {
        TextDocumentDomainObject doc = saveNewTextDocumentFn();

        docMapper.invalidateDocument(doc);
    }

    @Test
    public void getWorkingDocument() {
        TextDocumentDomainObject doc = getMainWorkingDocumentInDefaultLanguage(true);
    }


    @Test
    public void getDefaultDocument() {
        DocumentDomainObject doc = docMapper.getDefaultDocument(1001);

        assertNotNull(doc);
    }


    @Test
    public void getCustomDocument() {
        fail("NOT IMPLEMENTED");
    }


    @Test
    public void deleteTextDocument() throws Exception {
        DocumentDomainObject doc = saveNewTextDocumentFn();
        docMapper.deleteDocument(doc, admin);
    }


    @Test
    public void deleteHtmlDocument() throws Exception {
        DocumentDomainObject doc = saveNewHtmlDocumentFn();
        docMapper.deleteDocument(doc, admin);
    }


    @Test
    public void deleteUrlDocument() throws Exception {
        DocumentDomainObject doc = saveNewUrlDocumentFn();
        docMapper.deleteDocument(doc, admin);
    }

    @Test
    public void deleteFileDocument()  throws Exception {
        DocumentDomainObject doc = saveNewFileDocumentFn();
        docMapper.deleteDocument(doc, admin);
    }


    public TextDocumentDomainObject getMainWorkingDocumentInDefaultLanguage(boolean assertExists) {
        DocumentDomainObject doc = docMapper.getCustomDocument(1001, 0, i18nSupport.getDefaultLanguage());

        if (assertExists) {
            assertNotNull(doc);
        }

        return (TextDocumentDomainObject)doc;
    }


    /**
     * Return content loop no and content index:
     */
    @DataProvider
    public Object[][] contentInfo() {
        TextDocumentDomainObject doc = getMainWorkingDocumentInDefaultLanguage(true);
        ContentLoop existingContentLoop = doc.getContentLoops().values().iterator().next();
        ContentLoop unsavedContentLoop = Factory.createNextContentLoop(doc);

        unsavedContentLoop.addFirstContent();

        Integer noContentLoopNo = null;
        Integer noContentNo = null;

        Integer existingContentLoopNo = existingContentLoop.getNo();
        Integer existingContentNo = existingContentLoop.getContents().get(0).getNo();

        Integer unsavedContentLoopNo = unsavedContentLoop.getNo();
        Integer unsavedContentNo = unsavedContentLoop.getContents().get(0).getNo();

        return new Object [][] {
                {noContentLoopNo, noContentNo},
                {existingContentLoopNo, existingContentNo},
                {unsavedContentLoopNo, unsavedContentNo}
        };
    }
}