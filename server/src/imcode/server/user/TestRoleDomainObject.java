package imcode.server.user;

import junit.framework.TestCase;

public class TestRoleDomainObject extends TestCase {

    RoleDomainObject role ;

    protected void setUp() throws Exception {
        super.setUp();
        role = new RoleDomainObject( 0, "dummy test role", 0 );
    }

    public void testPermissions() {
        RoleDomainObject.RolePermissionDomainObject passwordMailPermission = RoleDomainObject.PASSWORD_MAIL_PERMISSION;

        assertFalse( role.hasPermission( passwordMailPermission ) ) ;

        role.addPermission( passwordMailPermission );
        assertTrue(role.hasPermission( passwordMailPermission ) ) ;

        role.removePermission( passwordMailPermission ) ;
        assertFalse(role.hasPermission( passwordMailPermission )) ;
    }

}
