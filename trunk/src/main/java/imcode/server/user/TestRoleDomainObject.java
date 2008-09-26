package imcode.server.user;

import junit.framework.TestCase;

public class TestRoleDomainObject extends TestCase {

    RoleDomainObject role ;

    protected void setUp() throws Exception {
        super.setUp();
        role = new RoleDomainObject( "dummy test role" );
    }

    public void testPermissions() {
        RolePermissionDomainObject passwordMailPermission = RoleDomainObject.PASSWORD_MAIL_PERMISSION;

        assertFalse( role.hasPermission( passwordMailPermission ) ) ;

        role.addPermission( passwordMailPermission );
        assertTrue(role.hasPermission( passwordMailPermission ) ) ;

        role.removePermission( passwordMailPermission ) ;
        assertFalse(role.hasPermission( passwordMailPermission )) ;

        role.addUnionOfPermissionIdsToRole( passwordMailPermission.getId() );
        assertTrue( role.hasPermission( passwordMailPermission ) );
    }

}
