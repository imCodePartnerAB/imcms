package com.imcode.imcms.mapping;

import com.imcode.db.mock.MockDatabase;
import com.imcode.db.mock.MockResultSet;
import com.imcode.imcms.servlet.LoginPasswordManager;
import imcode.server.MockImcmsServices;
import imcode.server.document.*;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.IntegerSet;
import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;

import java.io.Serializable;
import java.sql.ResultSet;

public class TestDocumentMapper extends TestCase {

    private DocumentMapper documentMapper;
    private MockDatabase database;
    private UserDomainObject user;
    private RoleDomainObject testRole;
    private RoleId userRole;
    private TextDocumentDomainObject textDocument;
    private TextDocumentDomainObject oldDocument;
    private MockDocumentIndex documentIndex;
    private static final Integer ONE = new Integer(1);

    protected void setUp() throws Exception {
        BasicConfigurator.configure(new NullAppender());
        super.setUp();
        user = new UserDomainObject(0);
        userRole = new RoleId( 3 );
        user.addRoleId( userRole );
        testRole = new RoleDomainObject( new RoleId(2), "Testrole", 0 );
        oldDocument = createTextDocument(1001);
        textDocument = createTextDocument(1002);
        database = new MockDatabase();
        MockImcmsServices mockImcmsServices = new MockImcmsServices();
        mockImcmsServices.setDatabase(database);
        LoginPasswordManager userLoginPasswordManager = new LoginPasswordManager();
        ImcmsAuthenticatorAndUserAndRoleMapper userRegistry = new ImcmsAuthenticatorAndUserAndRoleMapper(mockImcmsServices, userLoginPasswordManager) {
            public UserDomainObject getUser( int userId ) {
                return user ;
            }

        };
        MockImcmsServices services = new MockImcmsServices() ;
        services.setImcmsAuthenticatorAndUserAndRoleMapper(userRegistry);
        services.setTemplateMapper(new TemplateMapper(new MockImcmsServices())) ;
        documentIndex = new MockDocumentIndex();
        CategoryMapper categoryMapper = new CategoryMapper(database);
        documentMapper = new DocumentMapper( services, database);
        documentMapper.setDocumentIndex(documentIndex);
        services.setDocumentMapper(documentMapper);
        services.setCategoryMapper(categoryMapper);
        ImageCacheMapper imageCacheMapper = new ImageCacheMapper(database);
        services.setImageCacheMapper(imageCacheMapper);
    }

    private TextDocumentDomainObject createTextDocument(int documentId) {
        TextDocumentDomainObject newTextDocument = new TextDocumentDomainObject();
        newTextDocument.setId( documentId );
        return newTextDocument ;
    }

    public void testNotSerializable() {
        if ( DocumentMapper.class.isAssignableFrom( Serializable.class )) {
            fail("DocumentMapper must not be serializable so it can't be put in the session.") ;
        }
    }

    public void testUpdateDocumentRolePermissionsWithNoPermissions() throws Exception {
        textDocument.setDocumentPermissionSetTypeForRoleId(testRole.getId(), DocumentPermissionSetTypeDomainObject.READ);
        documentMapper.getDocumentSaver().updateDocumentRolePermissions( textDocument, user, oldDocument );
        assertEquals( 0, database.getSqlCallCount() );
    }

    public void testUpdateDocumentRolePermissionsWithRestricted1Permission() throws Exception {
        oldDocument.setDocumentPermissionSetTypeForRoleId(userRole, DocumentPermissionSetTypeDomainObject.RESTRICTED_1);
        textDocument.setRoleIdsMappedToDocumentPermissionSetTypes(oldDocument.getRoleIdsMappedToDocumentPermissionSetTypes());
        textDocument.setDocumentPermissionSetTypeForRoleId(testRole.getId(), DocumentPermissionSetTypeDomainObject.READ);
        DocumentPermissionSetDomainObject permissionSetForRestrictedOne = new DocumentPermissionSetDomainObject(DocumentPermissionSetTypeDomainObject.RESTRICTED_1) {
            public void setFromBits(
                    int permissionBits) {
            }
        };
        oldDocument.getPermissionSets().setRestricted1(permissionSetForRestrictedOne);

        permissionSetForRestrictedOne.setEditPermissions(false);
        documentMapper.getDocumentSaver().updateDocumentRolePermissions(textDocument, user, oldDocument);
        assertEquals(0, database.getSqlCallCount());

        permissionSetForRestrictedOne.setEditPermissions(true);
        documentMapper.getDocumentSaver().updateDocumentRolePermissions(textDocument, user, oldDocument);
        assertEquals(4, database.getSqlCallCount());

        textDocument.setDocumentPermissionSetTypeForRoleId(testRole.getId(), DocumentPermissionSetTypeDomainObject.RESTRICTED_1);
        documentMapper.getDocumentSaver().updateDocumentRolePermissions(textDocument, user, oldDocument);
        database.assertCalled(new MockDatabase.EqualsSqlCallPredicate(DocumentSaver.SQL_SET_ROLE_DOCUMENT_PERMISSION_SET_ID));
    }

    public void testUpdateDocumentRolePermissionsWithFullPermission() throws Exception {
        oldDocument.setDocumentPermissionSetTypeForRoleId(userRole, DocumentPermissionSetTypeDomainObject.FULL);
        textDocument.setDocumentPermissionSetTypeForRoleId(testRole.getId(), DocumentPermissionSetTypeDomainObject.READ);
        textDocument.setRoleIdsMappedToDocumentPermissionSetTypes(oldDocument.getRoleIdsMappedToDocumentPermissionSetTypes());
        documentMapper.getDocumentSaver().updateDocumentRolePermissions(textDocument, user, oldDocument);
        assertEquals(2, database.getSqlCallCount());
    }

    public void testUpdateDocumentRolePermissionsRemovesPermission() {
        oldDocument.setDocumentPermissionSetTypeForRoleId(userRole, DocumentPermissionSetTypeDomainObject.FULL);
        documentMapper.getDocumentSaver().updateDocumentRolePermissions(textDocument, user, oldDocument);
        database.assertCalled(new MockDatabase.EqualsSqlCallPredicate(DocumentSaver.SQL_DELETE_ROLE_DOCUMENT_PERMISSION_SET_ID));
        database.assertNotCalled(new MockDatabase.EqualsWithParametersSqlCallPredicate(DocumentSaver.SQL_SET_ROLE_DOCUMENT_PERMISSION_SET_ID,
                                                                                       new String[] {
                                                                                               "" + userRole.intValue(),
                                                                                               ""
                                                                                               + textDocument.getId(),
                                                                                               ""
                                                                                               + DocumentPermissionSetTypeDomainObject
                                                                                                       .FULL }));
        database.assertNotCalled(new MockDatabase.EqualsWithParametersSqlCallPredicate(DocumentSaver.SQL_SET_ROLE_DOCUMENT_PERMISSION_SET_ID,
                                                                                       new String[] {
                                                                                               "" + userRole.intValue(),
                                                                                               ""
                                                                                               + textDocument.getId(),
                                                                                               ""
                                                                                               + DocumentPermissionSetTypeDomainObject
                                                                                                       .NONE }));
        assertEquals(1, database.getSqlCallCount());
    }

    public void testUpdateDocumentRolePermissionsAllowsNullOldDocument() throws Exception {
        documentMapper.getDocumentSaver().updateDocumentRolePermissions( textDocument, user, null );
        assertTrue(true);
    }

    public void testSaveNewBrowserDocument() throws Exception {
        BrowserDocumentDomainObject browserDocument = new BrowserDocumentDomainObject();
        browserDocument.setDocumentPermissionSetTypeForRoleId(userRole, DocumentPermissionSetTypeDomainObject.FULL);
        browserDocument.setBrowserDocumentId(BrowserDocumentDomainObject.Browser.DEFAULT, 1001);
        browserDocument.setCreator(new UserDomainObject(0));
        database.addExpectedSqlCall(new MockDatabase.InsertIntoTableSqlCallPredicate("meta"), new Integer(1002));
        documentMapper.saveNewDocument(browserDocument, user, false);
        database.assertExpectedSqlCalls();
        database.assertCallCount(1, new MockDatabase.InsertIntoTableSqlCallPredicate("browser_docs"));
        assertEquals(1002, browserDocument.getId());
    }

    public void testDeleteDocument() {
        Object[][] documentResultRows = new Object[1][19];
        documentResultRows[0][0] = new Integer(textDocument.getId()) ;
        documentResultRows[0][1] = new Integer(textDocument.getDocumentTypeId()) ;
        documentResultRows[0][5] = new Integer(user.getId()) ;
        documentResultRows[0][16] = new Integer(DocumentSaver.convertPublicationStatusToInt(textDocument.getPublicationStatus())) ;
        database.addExpectedSqlCall( new MockDatabase.StartsWithSqlCallPredicate( DatabaseDocumentGetter.SQL_GET_DOCUMENTS ), new MockResultSet(documentResultRows) );
        ResultSet textDocsResultRow = new MockResultSet(new Object[][] { { ONE, ONE, ONE, ONE, ONE } } ) ;
        database.addExpectedSqlCall( new MockDatabase.MatchesRegexSqlCallPredicate( "FROM text_docs"), textDocsResultRow );
        DocumentDomainObject document = documentMapper.getDocument(textDocument.getId());
        assertNotNull( document ) ;
        documentMapper.deleteDocument( textDocument, user );
        database.assertCalledInOrder(new MockDatabase.SqlCallPredicate[] {
                                    new MockDatabase.DeleteFromTableSqlCallPredicate("text_docs"),
                                    new MockDatabase.DeleteFromTableSqlCallPredicate("meta")});
        database.assertCalledInOrder(new MockDatabase.SqlCallPredicate[] {
                new MockDatabase.DeleteFromTableSqlCallPredicate("texts"),
                new MockDatabase.DeleteFromTableSqlCallPredicate("meta")});
        database.assertCalledInOrder(new MockDatabase.SqlCallPredicate[] {
                new MockDatabase.DeleteFromTableSqlCallPredicate("childs"),
                new MockDatabase.DeleteFromTableSqlCallPredicate("childs"),
                new MockDatabase.DeleteFromTableSqlCallPredicate("menus"),
                new MockDatabase.DeleteFromTableSqlCallPredicate("meta")});
        assertTrue(documentIndex.isRemoveDocumentCalled()) ;
        assertFalse(documentIndex.isIndexDocumentCalled()) ;
    }

    public void testCreateTextDocument() throws NoPermissionToAddDocumentToMenuException, NoPermissionToCreateDocumentException, DocumentSaveException {
        user.addRoleId( RoleId.SUPERADMIN );
        TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.createDocumentOfTypeFromParent( DocumentTypeDomainObject.TEXT_ID, textDocument, user );
        document.setTemplateName( "1" );
        database.addExpectedSqlCall( new MockDatabase.InsertIntoTableSqlCallPredicate( "meta" ), new Integer(1002));
        documentMapper.saveNewDocument( document, user, false);
        database.assertExpectedSqlCalls();
    }

    public void testCreateHtmlDocument() throws NoPermissionToAddDocumentToMenuException, NoPermissionToCreateDocumentException, DocumentSaveException {
        user.addRoleId( RoleId.SUPERADMIN );
        DocumentDomainObject document = documentMapper.createDocumentOfTypeFromParent( DocumentTypeDomainObject.HTML_ID, textDocument, user );
        database.addExpectedSqlCall( new MockDatabase.InsertIntoTableSqlCallPredicate( "meta" ), new Integer(1002));
        documentMapper.saveNewDocument( document, user, false);
        database.assertExpectedSqlCalls();
    }

    public void testCreateUrlDocument() throws NoPermissionToAddDocumentToMenuException, NoPermissionToCreateDocumentException, DocumentSaveException {
        user.addRoleId( RoleId.SUPERADMIN );
        DocumentDomainObject document = documentMapper.createDocumentOfTypeFromParent( DocumentTypeDomainObject.URL_ID, textDocument, user );
        database.addExpectedSqlCall( new MockDatabase.InsertIntoTableSqlCallPredicate( "meta" ), new Integer(1002));
        documentMapper.saveNewDocument( document, user, false);
        database.assertExpectedSqlCalls();
    }


    public void testSetTemplateForNewTextDocument() throws Exception {
        String templateId1 = "1";
        String templateId2 = "2";
        String templateId3 = "3";
        String templateId4 = "4";
        oldDocument.setTemplateName(templateId1);
        oldDocument.setDefaultTemplateIdForRestricted1(templateId3);
        TextDocumentPermissionSetDomainObject permissionSetForR1 = (TextDocumentPermissionSetDomainObject) oldDocument.getPermissionSets().getRestricted1();
        permissionSetForR1.setAllowedDocumentTypeIds(new IntegerSet(DocumentTypeDomainObject.TEXT_ID));
        oldDocument.setDefaultTemplateIdForRestricted2(templateId4);
        TextDocumentPermissionSetDomainObject permissionSetForR2 = (TextDocumentPermissionSetDomainObject) oldDocument.getPermissionSets().getRestricted2();
        permissionSetForR2.setAllowedDocumentTypeIds(new IntegerSet(DocumentTypeDomainObject.TEXT_ID));

        oldDocument.setDocumentPermissionSetTypeForRoleId(userRole, DocumentPermissionSetTypeDomainObject.FULL);
        TextDocumentDomainObject newDocument = (TextDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, oldDocument, user);
        assertEquals(oldDocument.getTemplateName(), newDocument.getTemplateName());
        assertEquals(templateId1, newDocument.getTemplateName());

        oldDocument.setDefaultTemplateId(templateId2);
        newDocument = (TextDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, oldDocument, user);
        assertEquals(templateId2, newDocument.getTemplateName());

        oldDocument.setDocumentPermissionSetTypeForRoleId(userRole, DocumentPermissionSetTypeDomainObject.RESTRICTED_1);
        newDocument = (TextDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, oldDocument, user);
        assertEquals(templateId3, newDocument.getTemplateName());

        oldDocument.setDefaultTemplateIdForRestricted1(null);
        newDocument = (TextDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, oldDocument, user);
        assertEquals(templateId2, newDocument.getTemplateName());

        oldDocument.setDocumentPermissionSetTypeForRoleId(userRole, DocumentPermissionSetTypeDomainObject.RESTRICTED_2);
        newDocument = (TextDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, oldDocument, user);
        assertEquals(templateId4, newDocument.getTemplateName());

        oldDocument.setDefaultTemplateIdForRestricted2(null);
        newDocument = (TextDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID, oldDocument, user);
        assertEquals(templateId2, newDocument.getTemplateName());
    }

}