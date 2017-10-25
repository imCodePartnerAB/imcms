package com.imcode.imcms.api;

import imcode.server.MockImcmsServices;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

public class TestDefaultContentManagementSystem extends TestCase {

    private ContentManagementSystem contentManagementSystem;

    protected void setUp() throws Exception {
        super.setUp();
        MockImcmsServices mockImcmsServices = new MockImcmsServices();
        contentManagementSystem = new ContentManagementSystem(mockImcmsServices, new UserDomainObject());
    }

    public void testClonedCurrentUser() {
        User user = contentManagementSystem.getCurrentUser();
        assertNotNull(user);
        User userAgain = contentManagementSystem.getCurrentUser();
        assertNotNull(userAgain);
        assertNotSame(user.getInternal(), userAgain.getInternal());
    }

    public void testClone() throws CloneNotSupportedException {
        ContentManagementSystem clone = contentManagementSystem.clone();
        assertNotSame(contentManagementSystem.currentUser, clone.currentUser);
    }

}
