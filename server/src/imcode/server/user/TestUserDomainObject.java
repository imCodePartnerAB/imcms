package imcode.server.user;

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
        assertNotSame( "Roles cloned", clone.roles, user.roles);
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

}
