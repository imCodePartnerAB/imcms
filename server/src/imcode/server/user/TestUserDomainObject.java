package imcode.server.user;

import imcode.server.document.DocumentPermissionSetTypeDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import junit.framework.TestCase;
import org.apache.commons.lang.ArrayUtils;

public class TestUserDomainObject extends TestCase {

    private UserDomainObject user;

    protected void setUp() throws Exception {
        super.setUp();
        user = new UserDomainObject() ;
    }

    public void testClone() {
        UserDomainObject clone = (UserDomainObject)user.clone() ;
        assertNotSame( "Roles cloned", clone.roleIds, user.roleIds);
        assertNotSame( "Useradmin roles cloned", clone.userAdminRoleIds, user.userAdminRoleIds);
    }

    public void testUserAlwaysHasUsersRole() {
        assertTrue(user.hasRoleId( RoleId.USERS )) ;
        assertTrue(ArrayUtils.contains( user.getRoleIds(), RoleId.USERS ) ) ;
        user.removeRoleId( RoleId.USERS );
        assertTrue( user.hasRoleId( RoleId.USERS ) );
        assertTrue( ArrayUtils.contains( user.getRoleIds(), RoleId.USERS ) );
        user.setRoleIds( new RoleId[0] );
        assertTrue( user.hasRoleId( RoleId.USERS ) );
        assertTrue( ArrayUtils.contains( user.getRoleIds(), RoleId.USERS ) );
    }

    public void testCanAddDocumentToAnyMenu() {
        TextDocumentDomainObject textDocument = new TextDocumentDomainObject();
        assertFalse(user.canAddDocumentToAnyMenu( textDocument )) ;
        RoleId readRole = new RoleId( 3);
        textDocument.setDocumentPermissionSetTypeForRoleId( readRole, DocumentPermissionSetTypeDomainObject.READ );
        user.addRoleId( readRole );
        assertFalse(user.canAddDocumentToAnyMenu( textDocument )) ;
        textDocument.setLinkableByOtherUsers( true );
        assertTrue(user.canAddDocumentToAnyMenu( textDocument )) ;
        textDocument.setLinkableByOtherUsers( false );
        assertFalse(user.canAddDocumentToAnyMenu( textDocument )) ;
        RoleId editRole = new RoleId( 4);
        textDocument.setDocumentPermissionSetTypeForRoleId( editRole, DocumentPermissionSetTypeDomainObject.FULL );
        user.addRoleId( editRole );
        assertTrue(user.canAddDocumentToAnyMenu( textDocument )) ;
    }

}
