package imcode.server.document;

import imcode.server.db.MockDatabase;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

public class TestDocumentMapper extends TestCase {

    private DocumentMapper documentMapper;
    private MockDatabase database;
    private UserDomainObject user;
    private RoleDomainObject testRole;
    private RoleDomainObject userRole;
    private TextDocumentDomainObject document;
    private TextDocumentDomainObject oldDocument;

    protected void setUp() throws Exception {
        super.setUp();
        user = new UserDomainObject();
        userRole = new RoleDomainObject( 1, "Userrole", 0 );
        user.addRole( userRole );
        testRole = new RoleDomainObject( 2, "Testrole", 0 );
        oldDocument = new TextDocumentDomainObject();
        oldDocument.setId( 1001 );
        document = new TextDocumentDomainObject();
        document.setId( 1002 );
        database = new MockDatabase();
        documentMapper = new DocumentMapper( null, database, null, null, null, null );
    }

    public void testUpdateDocumentRolePermissionsWithNoPermissions() throws Exception {
        document.setPermissionSetIdForRole( testRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        documentMapper.updateDocumentRolePermissions( document, user, oldDocument );
        assertEquals( 0, database.getSqlCallCount() );
    }

    public void testUpdateDocumentRolePermissionsWithRestricted1Permission() throws Exception {
        oldDocument.setPermissionSetIdForRole( userRole, DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1 );
        document.setRolesMappedToPermissionSetIds( oldDocument.getRolesMappedToPermissionSetIds() );
        document.setPermissionSetIdForRole( testRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        DocumentPermissionSetDomainObject permissionSetForRestrictedOne = new DocumentPermissionSetDomainObject( 1 );
        oldDocument.setPermissionSetForRestrictedOne( permissionSetForRestrictedOne );

        permissionSetForRestrictedOne.setEditPermissions( false );
        documentMapper.updateDocumentRolePermissions( document, user, oldDocument );
        assertEquals( 0, database.getSqlCallCount() );

        permissionSetForRestrictedOne.setEditPermissions( true );
        documentMapper.updateDocumentRolePermissions( document, user, oldDocument );
        assertEquals( 2, database.getSqlCallCount() );

        document.setPermissionSetIdForRole( testRole, DocumentPermissionSetDomainObject.TYPE_ID__RESTRICTED_1 );
        documentMapper.updateDocumentRolePermissions( document, user, oldDocument );
        database.assertCalled( new MockDatabase.ProcedureSqlCallPredicate( DocumentMapper.SPROC_SET_PERMISSION_SET_ID_FOR_ROLE_ON_DOCUMENT ) );
    }

    public void testUpdateDocumentRolePermissionsWithFullPermission() throws Exception {
        oldDocument.setPermissionSetIdForRole( userRole, DocumentPermissionSetDomainObject.TYPE_ID__FULL );
        document.setPermissionSetIdForRole( testRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        document.setRolesMappedToPermissionSetIds( oldDocument.getRolesMappedToPermissionSetIds() );
        documentMapper.updateDocumentRolePermissions( document, user, oldDocument );
        assertEquals( 1, database.getSqlCallCount() );
    }

    public void testUpdateDocumentRolePermissionsRemovesPermission() {
        oldDocument.setPermissionSetIdForRole( userRole, DocumentPermissionSetDomainObject.TYPE_ID__FULL );
        documentMapper.updateDocumentRolePermissions( document, user, oldDocument );
        database.assertNotCalled( new MockDatabase.EqualsWithParametersSqlCallPredicate( DocumentMapper.SPROC_SET_PERMISSION_SET_ID_FOR_ROLE_ON_DOCUMENT,
                                                                                         new String[]{
                                                                                             "" + userRole.getId(),
                                                                                             "" + document.getId(),
                                                                                             "" + DocumentPermissionSetDomainObject.TYPE_ID__FULL} ) );
        database.assertCalled( new MockDatabase.EqualsWithParametersSqlCallPredicate( DocumentMapper.SPROC_SET_PERMISSION_SET_ID_FOR_ROLE_ON_DOCUMENT,
                                                                                      new String[]{
                                                                                          "" + userRole.getId(),
                                                                                          "" + document.getId(),
                                                                                          "" + DocumentPermissionSetDomainObject.TYPE_ID__NONE} ) );
        assertEquals( 1, database.getSqlCallCount() );
    }

    public void testUpdateDocumentRolePermissionsAllowsNullOldDocument() throws Exception {
        documentMapper.updateDocumentRolePermissions( document, user, null );
    }

}