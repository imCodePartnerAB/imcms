package com.imcode.imcms.api;

import junit.framework.TestCase;
import imcode.server.MockImcmsServices;
import imcode.server.user.UserDomainObject;

public class TestDefaultContentManagementSystem extends TestCase {

    private DefaultContentManagementSystem contentManagementSystem;

    protected void setUp() throws Exception {
        super.setUp();
        contentManagementSystem = new DefaultContentManagementSystem( new MockImcmsServices(), new UserDomainObject() );
    }

    public void testClonedCurrentUser() {
        User user = contentManagementSystem.getCurrentUser();
        assertNotNull( user );
        User userAgain = contentManagementSystem.getCurrentUser();
        assertNotNull( userAgain );
        assertNotSame( user.getInternal(), userAgain.getInternal() );
    }

}
