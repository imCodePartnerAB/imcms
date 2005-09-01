package com.imcode.imcms.mapping;

import imcode.server.Config;
import imcode.server.MockImcmsServices;
import imcode.server.db.impl.MockDatabase;
import imcode.server.document.*;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.IndexException;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.NoPermissionToAddDocumentToMenuException;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;
import org.apache.lucene.search.Query;

import java.io.Serializable;
import java.util.Collection;

public class TestDefaultDocumentMapper extends TestCase {

    private DefaultDocumentMapper documentMapper;
    private MockDatabase database;
    private UserDomainObject user;
    private RoleDomainObject testRole;
    private RoleDomainObject userRole;
    private TextDocumentDomainObject textDocument;
    private TextDocumentDomainObject oldDocument;
    private TestDefaultDocumentMapper.MockDocumentIndex documentIndex;

    protected void setUp() throws Exception {
        BasicConfigurator.configure(new NullAppender());
        super.setUp();
        user = new UserDomainObject();
        userRole = new RoleDomainObject( 1, "Userrole", 0 );
        user.addRole( userRole );
        testRole = new RoleDomainObject( 2, "Testrole", 0 );
        oldDocument = createTextDocument(1001);
        textDocument = createTextDocument(1002);
        database = new MockDatabase();
        ImcmsAuthenticatorAndUserAndRoleMapper userRegistry = new ImcmsAuthenticatorAndUserAndRoleMapper( null, null) {
            public UserDomainObject getUser( int userId ) {
                return user ;
            }

        };
        MockImcmsServices services = new MockImcmsServices() ;
        services.setImcmsAuthenticatorAndUserAndRoleMapper(userRegistry);
        services.setTemplateMapper(new TemplateMapper(new MockImcmsServices() ) {
            public TemplateDomainObject getTemplateById( int template_id ) {
                return null ;
            }
        }) ;
        documentIndex = new MockDocumentIndex();
        documentMapper = new DefaultDocumentMapper( services, database, new DatabaseDocumentGetter(database, services), new DocumentPermissionSetMapper( database, services ), documentIndex, null, new Config(), new CategoryMapper(database));
        services.setDocumentMapper(documentMapper);
    }

    private TextDocumentDomainObject createTextDocument(int documentId) {
        TextDocumentDomainObject textDocument = new TextDocumentDomainObject();
        textDocument.setId( documentId );
        return textDocument ;
    }

    public void testNotSerializable() {
        if ( DefaultDocumentMapper.class.isAssignableFrom( Serializable.class )) {
            fail("DocumentMapper must not be serializable so it can't be put in the session.") ;
        }
    }

    public void testUpdateDocumentRolePermissionsWithNoPermissions() throws Exception {
        textDocument.setPermissionSetIdForRole( testRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        documentMapper.getDocumentSaver().updateDocumentRolePermissions( textDocument, user, oldDocument );
        assertEquals( 0, database.getSqlCallCount() );
    }

    public void testUpdateDocumentRolePermissionsWithRestricted1Permission() throws Exception {
        oldDocument.setPermissionSetIdForRole( userRole, DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1 );
        textDocument.setRolesMappedToPermissionSetIds( oldDocument.getRolesMappedToPermissionSetIds() );
        textDocument.setPermissionSetIdForRole( testRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        DocumentPermissionSetDomainObject permissionSetForRestrictedOne = new DocumentPermissionSetDomainObject( 1 ) {
            public void setFromBits( DocumentDomainObject document, DocumentPermissionSetMapper documentPermissionSetMapper,
                                     int permissionBits, boolean forNewDocuments ) {
            }
        };
        oldDocument.setPermissionSetForRestrictedOne( permissionSetForRestrictedOne );

        permissionSetForRestrictedOne.setEditPermissions( false );
        documentMapper.getDocumentSaver().updateDocumentRolePermissions( textDocument, user, oldDocument );
        assertEquals( 0, database.getSqlCallCount() );

        permissionSetForRestrictedOne.setEditPermissions( true );
        documentMapper.getDocumentSaver().updateDocumentRolePermissions( textDocument, user, oldDocument );
        assertEquals( 4, database.getSqlCallCount() );

        textDocument.setPermissionSetIdForRole( testRole, DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1 );
        documentMapper.getDocumentSaver().updateDocumentRolePermissions( textDocument, user, oldDocument );
        database.assertCalled( new MockDatabase.EqualsSqlCallPredicate( DefaultDocumentMapper.SQL_SET_ROLE_DOCUMENT_PERMISSION_SET_ID ) );
    }

    public void testUpdateDocumentRolePermissionsWithFullPermission() throws Exception {
        oldDocument.setPermissionSetIdForRole( userRole, DocumentPermissionSetDomainObject.TYPE_ID__FULL );
        textDocument.setPermissionSetIdForRole( testRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        textDocument.setRolesMappedToPermissionSetIds( oldDocument.getRolesMappedToPermissionSetIds() );
        documentMapper.getDocumentSaver().updateDocumentRolePermissions( textDocument, user, oldDocument );
        assertEquals( 2, database.getSqlCallCount() );
    }

    public void testUpdateDocumentRolePermissionsRemovesPermission() {
        oldDocument.setPermissionSetIdForRole( userRole, DocumentPermissionSetDomainObject.TYPE_ID__FULL );
        documentMapper.getDocumentSaver().updateDocumentRolePermissions( textDocument, user, oldDocument );
        database.assertCalled( new MockDatabase.EqualsSqlCallPredicate( DefaultDocumentMapper.SQL_DELETE_ROLE_DOCUMENT_PERMISSION_SET_ID ) ) ;
        database.assertNotCalled( new MockDatabase.EqualsWithParametersSqlCallPredicate( DefaultDocumentMapper.SQL_SET_ROLE_DOCUMENT_PERMISSION_SET_ID,
                                                                                         new String[]{
                                                                                             "" + userRole.getId(),
                                                                                             "" + textDocument.getId(),
                                                                                             "" + DocumentPermissionSetDomainObject.TYPE_ID__FULL} ) );
        database.assertNotCalled( new MockDatabase.EqualsWithParametersSqlCallPredicate( DefaultDocumentMapper.SQL_SET_ROLE_DOCUMENT_PERMISSION_SET_ID,
                                                                                         new String[]{
                                                                                             "" + userRole.getId(),
                                                                                             "" + textDocument.getId(),
                                                                                             "" + DocumentPermissionSetDomainObject.TYPE_ID__NONE} ) );
        assertEquals( 1, database.getSqlCallCount() );
    }

    public void testUpdateDocumentRolePermissionsAllowsNullOldDocument() throws Exception {
        documentMapper.getDocumentSaver().updateDocumentRolePermissions( textDocument, user, null );
        assertTrue(true);
    }

    public void testSaveNewBrowserDocument() throws Exception {
        BrowserDocumentDomainObject browserDocument = new BrowserDocumentDomainObject();
        browserDocument.setPermissionSetIdForRole( userRole, DocumentPermissionSetDomainObject.TYPE_ID__FULL );
        browserDocument.setBrowserDocumentId( BrowserDocumentDomainObject.Browser.DEFAULT, 1001 );
        browserDocument.setCreator(new UserDomainObject());
        database.addExpectedSqlCall( new MockDatabase.InsertIntoTableSqlCallPredicate( "meta" ), new Integer(1002) );
        documentMapper.saveNewDocument( browserDocument, user );
        database.verifyExpectedSqlCalls();
        database.assertCallCount( 1, new MockDatabase.InsertIntoTableSqlCallPredicate( "browser_docs" ));
        assertEquals( 1002, browserDocument.getId() ) ;
    }

    public void testDeleteDocument() {
        String[] documentResultRow = new String[19];
        documentResultRow[0] = ""+textDocument.getId() ;
        documentResultRow[1] = ""+textDocument.getDocumentTypeId() ;
        documentResultRow[5] = ""+user.getId() ;
        documentResultRow[16] = ""+textDocument.getPublicationStatus() ;
        database.addExpectedSqlCall( new MockDatabase.EqualsSqlCallPredicate( DefaultDocumentMapper.SQL_GET_DOCUMENT ), documentResultRow );
        String[] textDocsResultRow = new String[] { "1","1","1","1","1" } ;
        database.addExpectedSqlCall( new MockDatabase.MatchesRegexSqlCallPredicate( "FROM text_docs"), textDocsResultRow );
        assertNotNull( documentMapper.getDocument( textDocument.getId() ) ) ;
        documentMapper.deleteDocument( textDocument, user );
        database.addExpectedSqlCall( new MockDatabase.EqualsSqlCallPredicate( DefaultDocumentMapper.SQL_GET_DOCUMENT ), new String[0] );
        assertNull( documentMapper.getDocument( textDocument.getId() ) ) ;
        assertTrue(documentIndex.removeDocumentCalled) ;
        assertFalse(documentIndex.indexDocumentCalled) ;
    }

    public void testCreateTextDocument() throws NoPermissionToAddDocumentToMenuException, NoPermissionToCreateDocumentException {
        user.addRole( RoleDomainObject.SUPERADMIN );
        TextDocumentDomainObject document = (TextDocumentDomainObject)documentMapper.createDocumentOfTypeFromParent( DocumentTypeDomainObject.TEXT_ID, textDocument, user );
        document.setTemplate( new TemplateDomainObject( 1, "test", "test" ) );
        database.addExpectedSqlCall( new MockDatabase.InsertIntoTableSqlCallPredicate( "meta" ), new Integer(1002));
        documentMapper.saveNewDocument( document, user );
    }

    public void testCreateHtmlDocument() throws NoPermissionToAddDocumentToMenuException, NoPermissionToCreateDocumentException {
        user.addRole( RoleDomainObject.SUPERADMIN );
        DocumentDomainObject document = documentMapper.createDocumentOfTypeFromParent( DocumentTypeDomainObject.HTML_ID, textDocument, user );
        database.addExpectedSqlCall( new MockDatabase.InsertIntoTableSqlCallPredicate( "meta" ), new Integer(1002));
        documentMapper.saveNewDocument( document, user );
    }

    public void testCreateUrlDocument() throws NoPermissionToAddDocumentToMenuException, NoPermissionToCreateDocumentException {
        user.addRole( RoleDomainObject.SUPERADMIN );
        DocumentDomainObject document = documentMapper.createDocumentOfTypeFromParent( DocumentTypeDomainObject.URL_ID, textDocument, user );
        database.addExpectedSqlCall( new MockDatabase.InsertIntoTableSqlCallPredicate( "meta" ), new Integer(1002));
        documentMapper.saveNewDocument( document, user );
    }

    public void testDocumentAddedWithoutPermission() {
        UserDomainObject user = new UserDomainObject();
        TextDocumentDomainObject document = createTextDocument(1001);
        TextDocumentDomainObject addedDocument = createTextDocument(1002);
        document.getMenu(1).addMenuItem(new MenuItemDomainObject(new MockDocumentReference(addedDocument)));
        TextDocumentDomainObject oldDocument = createTextDocument(1001);

        addedDocument.setLinkableByOtherUsers(true);
        testDocumentsAddedWithPermission(document, null, user);

        addedDocument.setLinkableByOtherUsers(false);
        testDocumentsAddedWithoutPermission(document, null, user);

        testDocumentsAddedWithoutPermission(document, oldDocument, user);

        addedDocument.setLinkableByOtherUsers(true);
        testDocumentsAddedWithPermission(document, oldDocument, user);

        addedDocument.setLinkableByOtherUsers(false);
        testDocumentsAddedWithoutPermission(document, oldDocument, user);

        addedDocument.setPermissionSetIdForRole(RoleDomainObject.USERS, DocumentPermissionSetDomainObject.TYPE_ID__FULL);
        testDocumentsAddedWithPermission(document, oldDocument, user);

        addedDocument.setPermissionSetIdForRole(RoleDomainObject.USERS, DocumentPermissionSetDomainObject.TYPE_ID__NONE);
        testDocumentsAddedWithoutPermission(document, oldDocument, user);

        user.addRole(RoleDomainObject.SUPERADMIN);
        testDocumentsAddedWithPermission(document, oldDocument, user);

        user.removeRole(RoleDomainObject.SUPERADMIN);
        testDocumentsAddedWithoutPermission(document, oldDocument, user);

        try {
            documentMapper.getDocumentSaver().checkDocumentsAddedWithoutPermission(document, oldDocument, user);
            fail("Expected exception.");
        } catch( NoPermissionToAddDocumentToMenuException e) {}
    }

    private void testDocumentsAddedWithPermission(TextDocumentDomainObject document,
                                                  TextDocumentDomainObject oldDocument, UserDomainObject user) {
        assertEmpty(documentMapper.getDocumentSaver().getDocumentsAddedWithoutPermission(document, oldDocument, user));
    }

    private void testDocumentsAddedWithoutPermission(TextDocumentDomainObject document,
                                                     TextDocumentDomainObject oldDocument, UserDomainObject user) {
        assertNotEmpty(documentMapper.getDocumentSaver().getDocumentsAddedWithoutPermission(document, oldDocument, user));
    }

    private void assertEmpty(Collection collection) {
        assertTrue(collection.isEmpty());
    }

    private void assertNotEmpty(Collection collection) {
        assertFalse(collection.isEmpty());
    }

    public void testSetTemplateForNewTextDocument() throws Exception {
        TemplateDomainObject template1 = new TemplateDomainObject(1, "Template1", "Template1");
        TemplateDomainObject template2 = new TemplateDomainObject(2, "Template2", "Template2");
        TemplateDomainObject template3 = new TemplateDomainObject(2, "Template3", "Template3");
        TemplateDomainObject template4 = new TemplateDomainObject(2, "Template4", "Template4");
        oldDocument.setTemplate(template1);
        TextDocumentPermissionSetDomainObject permissionSetForNewR1 = (TextDocumentPermissionSetDomainObject) oldDocument.getPermissionSetForRestrictedOneForNewDocuments();
        permissionSetForNewR1.setDefaultTemplate(template3);
        TextDocumentPermissionSetDomainObject permissionSetForR1 = (TextDocumentPermissionSetDomainObject) oldDocument.getPermissionSetForRestrictedOne();
        permissionSetForR1.setAllowedDocumentTypeIds(new int[]{DocumentTypeDomainObject.TEXT_ID});
        TextDocumentPermissionSetDomainObject permissionSetForNewR2 = (TextDocumentPermissionSetDomainObject) oldDocument.getPermissionSetForRestrictedTwoForNewDocuments();
        permissionSetForNewR2.setDefaultTemplate(template4);
        TextDocumentPermissionSetDomainObject permissionSetForR2 = (TextDocumentPermissionSetDomainObject) oldDocument.getPermissionSetForRestrictedTwo();
        permissionSetForR2.setAllowedDocumentTypeIds(new int[]{DocumentTypeDomainObject.TEXT_ID});

        TextDocumentDomainObject newDocument ;

        oldDocument.setPermissionSetIdForRole(userRole, DocumentPermissionSetDomainObject.TYPE_ID__FULL);
        newDocument = (TextDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID,oldDocument,user) ;
        assertEquals(oldDocument.getTemplate(), newDocument.getTemplate() ) ;
        assertEquals(template1, newDocument.getTemplate()) ;

        oldDocument.setDefaultTemplate(template2);
        newDocument = (TextDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID,oldDocument,user) ;
        assertEquals(template2, newDocument.getTemplate()) ;

        oldDocument.setPermissionSetIdForRole(userRole, DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1);
        newDocument = (TextDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID,oldDocument,user) ;
        assertEquals(template3, newDocument.getTemplate());

        permissionSetForNewR1.setDefaultTemplate(null);
        newDocument = (TextDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID,oldDocument,user) ;
        assertEquals(template2, newDocument.getTemplate());

        oldDocument.setPermissionSetIdForRole(userRole, DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_2);
        newDocument = (TextDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID,oldDocument,user) ;
        assertEquals(template4, newDocument.getTemplate());

        permissionSetForNewR2.setDefaultTemplate(null);
        newDocument = (TextDocumentDomainObject) documentMapper.createDocumentOfTypeFromParent(DocumentTypeDomainObject.TEXT_ID,oldDocument,user) ;
        assertEquals(template2, newDocument.getTemplate());
    }

    public class MockDocumentIndex implements DocumentIndex {
        private boolean indexDocumentCalled;
        private boolean removeDocumentCalled;

        public void indexDocument( DocumentDomainObject document ) throws IndexException {
            this.indexDocumentCalled = true ;
        }

        public void removeDocument( DocumentDomainObject document ) throws IndexException {
            this.removeDocumentCalled = true ;
        }

        public DocumentDomainObject[] search( Query query, UserDomainObject searchingUser ) throws IndexException {
            return new DocumentDomainObject[0];
        }

        public void rebuild() {
        }
    }

    private static class MockDocumentReference extends DocumentReference {
        private DocumentDomainObject document;

        MockDocumentReference(DocumentDomainObject document) {
            super(document.getId(), null);
            this.document = document ;
        }

        public DocumentDomainObject getDocument() {
            return document ;
        }
    }
}