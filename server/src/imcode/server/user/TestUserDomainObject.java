package imcode.server.user;

import junit.framework.TestCase;

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

}
