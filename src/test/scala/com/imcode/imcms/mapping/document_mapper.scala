package com.imcode
package imcms.dao

import scala.collection.JavaConversions._
import org.scalatest.junit.JUnitSuite
import org.scalatest.BeforeAndAfterAll
import org.junit.Test

import com.imcode.imcms.test.DB
import com.imcode.imcms.test.Project

import org.junit.Assert._
import imcode.server.user.UserDomainObject
import imcode.server.Imcms
import java.io.ByteArrayInputStream
import imcode.util.io.InputStreamSource
import org.apache.commons.io.FileUtils
import imcode.server.document.textdocument.{NoPermissionToAddDocumentToMenuException, MenuItemDomainObject, MenuDomainObject, TextDocumentDomainObject}
import imcms.api.{ContentLoop, I18nSupport}
import imcode.server.document.{HtmlDocumentDomainObject, FileDocumentDomainObject, UrlDocumentDomainObject, DocumentTypeDomainObject}
import imcms.mapping.{DocumentStoringVisitor, DocumentCreatingVisitor, DocumentMapper}

class DocumentMapperSuite extends JUnitSuite with BeforeAndAfterAll {

  var docMapper: DocumentMapper = _
  var admin: UserDomainObject = _
  var user: UserDomainObject = _
  var i18nSupport: I18nSupport = _

  override def beforeAll {
    val project = Project()
    val db = new DB(project)

    db.recreate()
    project.initImcms(true, true)

    i18nSupport = Imcms.getI18nSupport
    docMapper = Imcms.getServices().getDocumentMapper
    admin = Imcms.getServices().verifyUser("admin", "admin")
    user = Imcms.getServices().verifyUser("user", "user")
  }

  override def afterAll() = Imcms.stop()


  @Test
  def saveNewTextDocument() {
    saveNewTextDocumentFn()
  }


  @Test
  def saveNewUrlDocument() {
    saveNewUrlDocumentFn()
  }


  @Test
  def saveNewHtmlDocument() {
    saveNewHtmlDocumentFn()
  }


  @Test
  def saveNewFileDocument() {
    saveNewFileDocumentFn()
  }


  def saveNewTextDocumentFn() = {
    val parentDoc = getMainWorkingDocumentInDefaultLanguage(true)
    val newDoc = docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, parentDoc, admin)
      .asInstanceOf[TextDocumentDomainObject]

    docMapper.saveNewDocument(newDoc, admin)
  }


  def saveNewUrlDocumentFn() = {
    val parentDoc = getMainWorkingDocumentInDefaultLanguage(true)
    val newDoc = docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.URL_ID, parentDoc, admin)
      .asInstanceOf[UrlDocumentDomainObject]

    docMapper.saveNewDocument(newDoc, admin)
  }


  def saveNewHtmlDocumentFn() = {
    val parentDoc = getMainWorkingDocumentInDefaultLanguage(true)
    val newDoc = docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.HTML_ID, parentDoc, admin)
      .asInstanceOf[HtmlDocumentDomainObject]

    docMapper.saveNewDocument(newDoc, admin)
  }

  /**
   * Saves new file document file containing 3 files.
   *
   * @return
   * @throws Exception
   */
  def saveNewFileDocumentFn() = {
    class Source(data: String) extends InputStreamSource {
      val bin = new ByteArrayInputStream(data.getBytes())

      def getInputStream() = bin

      def getSize() = bin.available()
    }

    val parentDoc = getMainWorkingDocumentInDefaultLanguage(true)
    val newDoc = docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.FILE_ID, parentDoc, admin)
      .asInstanceOf[FileDocumentDomainObject]


    for (i <- 0 to 2) {
        val fdf = new FileDocumentDomainObject.FileDocumentFile

        fdf.setFilename("test_file_%d.txt" format i)
        fdf.setMimeType("text")
        fdf.setCreatedAsImage(false)
        fdf.setInputStreamSource(new Source("test content " + i))

        newDoc.addFile("file_id_" + i, fdf)
    }

    assertSavedFiles(docMapper.saveNewDocument(newDoc, admin))
  }


  def assertSavedFiles(doc: FileDocumentDomainObject) = {
    val defaultFileId = doc.getDefaultFileId
    val defaultFile = doc.getDefaultFile
    val docFiles = doc.getFiles

    assertEquals(defaultFileId, "file_id_0")
    assertEquals(docFiles.size(), 3)

    for (i <- 0 to 2) {
      val fdfId = "file_id_" + i
      val fdf = docFiles.get(fdfId)

      assertNotNull(fdf)
      assertEquals(fdf.getFilename, "test_file_%d.txt" format i)
      assertEquals(fdf.getMimeType(), "text")

      val file = DocumentStoringVisitor.getFileForFileDocumentFile(doc.getId, doc.getVersionNo.intValue, fdfId)
      assertTrue(file.exists)

      val content = FileUtils.readFileToString(file)
      assertEquals(content, "test content " + i)
    }

    doc
  }


  @Test
  def saveTextDocument()  {
    val doc = saveNewTextDocumentFn()

    docMapper.saveDocument(doc, admin)
  }


  //@Test(enabled = true, expectedExceptions = NoPermissionToEditDocumentException.class)
  @Test
  def saveTextDocumentNoPermissions() {
    val doc = saveNewTextDocumentFn()

    docMapper.saveDocument(doc, user)
  }


  @Test
  def saveHtmlDocument() {
    val doc = saveNewHtmlDocumentFn()

    docMapper.saveDocument(doc, admin)
  }

  @Test
  def saveUrlDocument()  {
    val doc = saveNewUrlDocumentFn()

    docMapper.saveDocument(doc, admin)
  }


  @Test
  def saveFileDocument() {
    val doc = saveNewFileDocumentFn()

    docMapper.saveDocument(doc, admin)
  }


  @Test
  def addMenu() {
      val textDoc = saveNewTextDocumentFn()
      val menuDoc = saveNewTextDocumentFn()

      val menu = new MenuDomainObject
      val item = new MenuItemDomainObject(docMapper.getDocumentReference(menuDoc))
      menu.addMenuItem(item)


      textDoc.setMenu(0, menu)

      docMapper.saveDocument(textDoc, admin)

      val savedTextDoc = docMapper.getCustomDocument(textDoc.getId, textDoc.getVersionNo, textDoc.getLanguage)
        .asInstanceOf[TextDocumentDomainObject]

      val savedMenu = savedTextDoc.getMenus.get(0)

      assertNotNull(savedMenu)

      assertEquals(savedMenu.getMenuItems.length, 1)

      val savedMenuDoc = savedMenu.getMenuItems()(0).getDocument

      assertEquals(savedMenuDoc.getId, menuDoc.getId);

  }

//    @Test//(dependsOnMethods = {"createDocumentOfTypeFromParent"})
//    public void addMenu() throws Exception {
//        TextDocumentDomainObject parentDoc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage(true);
//        DocumentDomainObject menuItemDoc = docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, parentDoc, admin);
//        List<I18nMeta> labels = new LinkedList<I18nMeta>();
//
//        for (I18nLanguage lang: i18nSupport.getAllLanguages()) {
//            I18nMeta l = new I18nMeta();
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
//        docMapper.saveTextDocMenu(parentDoc, menu, admin);
//
//        parentDoc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage(true);
//
//
//        DocGetterCallback docRequestInfo = new DocGetterCallback.WorkingDocRequestHandler(admin);
//        docRequestInfo.setLanguage(i18nSupport.getDefaultLanguage());
//        Imcms.setGetDocumentCallback(docRequestInfo);
//
//        menu = parentDoc.getMenu(menuNo);
//
//        assertNotNull(menu);
//        MenuItemDomainObject[] menuItems = menu.getMenuItems();
//
//        assertEquals(1, menuItems.length);
//        assertEquals(menuItemDocId.intValue(), menuItems[0].getDocumentReference().getDocumentId());
//    }



  @Test(expected = classOf[NoPermissionToAddDocumentToMenuException])
  def copyTextDocumentNoPermission() {
    val doc = saveNewTextDocumentFn()
    val docCopy = docMapper.copyDocument(doc, user)
  }


  @Test
  def copyTextDocument() {
    //TextDocumentDomainObject doc = saveNewTextDocumentFn();
    for (l <- Imcms.getI18nSupport.getLanguages) {
      val doc = docMapper.getDocument(1001).asInstanceOf[TextDocumentDomainObject]
      assertNotNull(doc)
    }

    val doc = docMapper.getDocument(1001).asInstanceOf[TextDocumentDomainObject]

    val docCopy = docMapper.copyDocument(doc, admin)
    val docCopyId = docCopy.getId

    assertNotSame(doc.getId, docCopyId)

    for (l <- Imcms.getI18nSupport.getLanguages.toList) {
      val d = docMapper.getDocument(docCopyId)
      assertNotNull(doc)
    }
  }



  @Test
  def copyHtmlsDocument() {
    val doc = saveNewHtmlDocumentFn()
    val docCopy = docMapper.copyDocument(doc, admin)
  }


  @Test
  def copyUrlDocument() {
    val doc = saveNewUrlDocumentFn()
    val docCopy = docMapper.copyDocument(doc, admin)
  }


  @Test
  def copyFileDocument()  {
    val doc = saveNewFileDocumentFn()
    val docCopy = docMapper.copyDocument(doc, admin)
  }


//    @Test(enabled = true, dataProvider = "contentInfo")
//    public void insertTextDocumentText(Integer contentLoopNo, Integer contentIndex) throws Exception {
//        TextDocumentDomainObject doc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage(true);
//        TextDomainObject text = Factory.createNextText(doc);
//
//        text.setContentLoopNo(contentLoopNo);
//        text.setContentNo(contentIndex);
//
//        if (contentLoopNo != null) {
//            ContentLoop loop = doc.getContentLoop(contentLoopNo);
//
//            if (loop == null) {
//                loop = Factory.createContentLoop(doc.getId(), doc.getVersion().getNo(), contentLoopNo);
//                Content content = loop.addFirstContent();
//
//                text.setContentNo(content.getNo());
//                doc.setContentLoop(contentLoopNo, loop);
//            }
//
//            doc.setText(text.getNo(), text);
//        }
//
//        docMapper.saveTextDocText(doc, text, admin);
//    }


  @Test
  def changeDocumentDefaultVersion() {
    val parentDoc = getMainWorkingDocumentInDefaultLanguage(true)
    var doc = docMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, parentDoc, admin)

    val docId = docMapper.saveNewDocument(doc, admin).getId
    val vi = docMapper.getDocumentVersionInfo(docId)

    doc = docMapper.getDefaultDocument(docId, i18nSupport.getDefaultLanguage)

    assertNotNull("New document exists",  doc)
    assertEquals("Default version of a new document is 0.", doc.getVersion.getNo, new JInteger(0))

    val version = docMapper.makeDocumentVersion(docId, admin)

    assertEquals("New doc version no is 1.", version.getNo, new JInteger(1))

    docMapper.changeDocumentDefaultVersion(docId, 1, admin)

    doc = docMapper.getDefaultDocument(docId, i18nSupport.getDefaultLanguage)

    assertEquals("Default version of a document is 1.", doc.getVersion.getNo, new JInteger(1))
  }


//    @Test(enabled = true, dataProvider = "contentInfo")
//    public void insertTextDocumentImage(Integer contentLoopNo, Integer contentIndex) throws Exception {
//        TextDocumentDomainObject doc = (TextDocumentDomainObject)getMainWorkingDocumentInDefaultLanguage(true);
//        ImageDomainObject image = Factory.createNextImage(doc);
//
//        image.setSource(new NullImageSource());
//        image.setContentLoopNo(contentLoopNo);
//        image.setContentNo(contentIndex);
//
//        docMapper.saveTextDocImage(doc, image, admin);
//    }


  @Test
  def getDocuments() {
    val ids = docMapper.getAllDocumentIds()
    val docs = docMapper.getDocuments(ids)

    assertEquals(ids.size(), docs.size)
  }


  @Test
  def getTextDocument() {
    getMainWorkingDocumentInDefaultLanguage(true)
  }


  @Test
  def getFileDocument(): Unit = pending


  @Test
  def getHtmlDocument(): Unit = pending


  @Test
  def getUrlDocument(): Unit = pending


  @Test
  def makeTextDocumentVersion() {
    val workingVersionDoc = getMainWorkingDocumentInDefaultLanguage(true)
    val info = docMapper.getDocumentVersionInfo(workingVersionDoc.getId)

    docMapper.makeDocumentVersion(workingVersionDoc.getId, admin)

    val newInfo = docMapper.getDocumentVersionInfo(workingVersionDoc.getId)
    val expectedNewVersionNo = info.getLatestVersion.getNo.intValue + 1

    assertEquals(info.getVersionsCount + 1, newInfo.getVersionsCount)
    assertEquals(newInfo.getLatestVersion.getNo, expectedNewVersionNo)

    val newVersionDoc = docMapper.getCustomDocument(workingVersionDoc.getId, expectedNewVersionNo)
    // instance of TextDocumentDomainObject ???

    assertNotNull(newVersionDoc)
  }


  /**
   * Saves document's content (all expect meta).
   */
  @Test
  def saveDocumentContent(): Unit = pending


  @Test
  def makeHtmlDocumentVersion() {
    val doc = saveNewHtmlDocumentFn();

    val info = docMapper.getDocumentVersionInfo(doc.getId)

    docMapper.makeDocumentVersion(doc.getId(), admin)

    val newInfo = docMapper.getDocumentVersionInfo(doc.getId)
    val expectedNewVersionNo = info.getLatestVersion.getNo.intValue + 1

    assertEquals(info.getVersionsCount + 1, newInfo.getVersionsCount)
    assertEquals(newInfo.getLatestVersion.getNo, expectedNewVersionNo)

    val newVersionDoc = docMapper.getCustomDocument(doc.getId, expectedNewVersionNo)
    // instance of HtmlDocumentDomainObject

    assertNotNull(newVersionDoc)
  }


  @Test
  def makeUrlDocumentVersion() {
    val doc = saveNewUrlDocumentFn();
    val info = docMapper.getDocumentVersionInfo(doc.getId)

    docMapper.makeDocumentVersion(doc.getId, admin)

    val newInfo = docMapper.getDocumentVersionInfo(doc.getId)
    val expectedNewVersionNo = info.getLatestVersion.getNo.intValue + 1

    assertEquals(info.getVersionsCount + 1, newInfo.getVersionsCount)
    assertEquals(newInfo.getLatestVersion.getNo, expectedNewVersionNo)

    val newVersionDoc = docMapper.getCustomDocument(doc.getId, expectedNewVersionNo)
    // instanceOf UrlDocumentDomainObject

    assertNotNull(newVersionDoc);
  }


  @Test
  def makeFileDocumentVersion() {
    val doc = saveNewFileDocumentFn()
    val info = docMapper.getDocumentVersionInfo(doc.getId)
    val docVersionNew = docMapper.makeDocumentVersion(doc.getId, admin)
    val infoNew = docMapper.getDocumentVersionInfo(doc.getId)
    val expectedNewVersionNo = info.getLatestVersion.getNo.intValue + 1

    assertEquals(info.getVersionsCount + 1, infoNew.getVersionsCount)
    assertEquals(infoNew.getLatestVersion.getNo, expectedNewVersionNo)

    val docNew = docMapper.getCustomDocument(doc.getId, expectedNewVersionNo)
    // instance of FileDocumentDomainObject
    assertNotNull(docNew)
    assertEquals(doc.getId, docNew.getId)

    assertSavedFiles(docNew.asInstanceOf[FileDocumentDomainObject])
  }


  @Test
  def getDocumentVersionInfo(): Unit = pending


  @Test
  def saveTextDocumentText(): Unit = pending


  @Test
  def saveTextDocumentImage(): Unit = pending


  @Test
  def saveTextDocumentContentLoop() {
    val doc = saveNewTextDocumentFn()
    val loop = new ContentLoop
    loop.addFirstContent

    doc.setContentLoop(0, loop)

    docMapper.saveDocument(doc, admin)
  }


  @Test
  def invalidateDocument() {
    val doc = saveNewTextDocumentFn()

    docMapper.invalidateDocument(doc)
  }

  @Test
  def getWorkingDocument() {
    val doc = getMainWorkingDocumentInDefaultLanguage(true)
  }


  @Test
  def getDefaultDocument() {
    val doc = docMapper.getDefaultDocument(1001)

    assertNotNull(doc)
  }


  @Test
  def getCustomDocument(): Unit = pending


  @Test
  def deleteTextDocument() {
    val doc = saveNewTextDocumentFn()
    docMapper.deleteDocument(doc, admin)
  }


  @Test
  def deleteHtmlDocument() {
    val doc = saveNewHtmlDocumentFn()
    docMapper.deleteDocument(doc, admin)
  }


  @Test
  def deleteUrlDocument() {
    val doc = saveNewUrlDocumentFn()
    docMapper.deleteDocument(doc, admin)
  }

  @Test
  def deleteFileDocument() {
    val doc = saveNewFileDocumentFn()

    for (i <- 0 to 2) {
      val fdfId = "file_id_" + i
      val file = DocumentStoringVisitor.getFileForFileDocumentFile(doc.getId, doc.getVersionNo.intValue, fdfId)

      assertTrue(file.exists)
    }


    docMapper.deleteDocument(doc, admin)

    for (i <- 0 to 2) {
      val fdfId = "file_id_" + i
      val file = DocumentStoringVisitor.getFileForFileDocumentFile(doc.getId, doc.getVersionNo.intValue, fdfId)

      assertTrue(!file.exists)
    }
  }


  def getMainWorkingDocumentInDefaultLanguage(assertDocExists: Boolean) = {
    val doc = docMapper.getCustomDocument(1001, 0, i18nSupport.getDefaultLanguage)

    if (assertDocExists) {
      assertNotNull(doc)
    }

    doc.asInstanceOf[TextDocumentDomainObject]
  }


//    /**
//     * Return content loop no and content index:
//     */
//    @DataProvider
//    public Object[][] contentInfo() {
//        TextDocumentDomainObject doc = getMainWorkingDocumentInDefaultLanguage(true);
//        ContentLoop existingContentLoop = doc.getContentLoops().values().iterator().next();
//        ContentLoop unsavedContentLoop = Factory.createNextContentLoop(doc);
//
//        unsavedContentLoop.addFirstContent();
//
//        Integer noContentLoopNo = null;
//        Integer noContentNo = null;
//
//        Integer existingContentLoopNo = existingContentLoop.getNo();
//        Integer existingContentNo = existingContentLoop.getContents().get(0).getNo();
//
//        Integer unsavedContentLoopNo = unsavedContentLoop.getNo();
//        Integer unsavedContentNo = unsavedContentLoop.getContents().get(0).getNo();
//
//        return new Object [][] {
//                {noContentLoopNo, noContentNo},
//                {existingContentLoopNo, existingContentNo},
//                {unsavedContentLoopNo, unsavedContentNo}
//        };
//    }


  def updateDocumentPermissions(): Unit = pending
}