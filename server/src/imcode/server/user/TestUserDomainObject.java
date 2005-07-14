package imcode.server.user;

import junit.framework.TestCase;
import org.apache.commons.lang.ArrayUtils;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.document.DocumentPermissionSetDomainObject;

public class TestUserDomainObject extends TestCase {

    private UserDomainObject user;

    protected void setUp() throws Exception {
        super.setUp();
        user = new UserDomainObject() ;
    }

    public void testClone() {
        UserDomainObject clone = (UserDomainObject)user.clone() ;
        assertNotSame( "Roles cloned", clone.roles, user.roles);
        assertNotSame( "Useradmin roles cloned", clone.userAdminRoles, user.userAdminRoles);
    }

    public void testUserAlwaysHasUsersRole() {
        assertTrue(user.hasRole( RoleDomainObject.USERS )) ;
        assertTrue(ArrayUtils.contains( user.getRoles(), RoleDomainObject.USERS ) ) ;
        user.removeRole( RoleDomainObject.USERS );
        assertTrue( user.hasRole( RoleDomainObject.USERS ) );
        assertTrue( ArrayUtils.contains( user.getRoles(), RoleDomainObject.USERS ) );
        user.setRoles( new RoleDomainObject[0] );
        assertTrue( user.hasRole( RoleDomainObject.USERS ) );
        assertTrue( ArrayUtils.contains( user.getRoles(), RoleDomainObject.USERS ) );
    }

    public void testCanAddDocumentToAnyMenu() {
        TextDocumentDomainObject textDocument = new TextDocumentDomainObject();
        assertFalse(user.canAddDocumentToAnyMenu( textDocument )) ;
        RoleDomainObject readRole = new RoleDomainObject( "read" );
        textDocument.setPermissionSetIdForRole( readRole, DocumentPermissionSetDomainObject.TYPE_ID__READ );
        user.addRole( readRole );
        assertFalse(user.canAddDocumentToAnyMenu( textDocument )) ;
        textDocument.setLinkableByOtherUsers( true );
        assertTrue(user.canAddDocumentToAnyMenu( textDocument )) ;
        textDocument.setLinkableByOtherUsers( false );
        assertFalse(user.canAddDocumentToAnyMenu( textDocument )) ;
        RoleDomainObject editRole = new RoleDomainObject( "edit" );
        textDocument.setPermissionSetIdForRole( editRole, DocumentPermissionSetDomainObject.TYPE_ID__FULL );
        user.addRole( editRole );
        assertTrue(user.canAddDocumentToAnyMenu( textDocument )) ;
    }

}
