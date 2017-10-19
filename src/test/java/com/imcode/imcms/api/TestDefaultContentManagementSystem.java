package com.imcode.imcms.api;

import imcode.server.MockImcmsServices;
import imcode.server.user.UserDomainObject;
import junit.framework.TestCase;

public class TestDefaultContentManagementSystem extends TestCase {

    private DefaultContentManagementSystem contentManagementSystem;
    private MockImcmsServices mockImcmsServices;

    protected void setUp() throws Exception {
        super.setUp();
        mockImcmsServices = new MockImcmsServices();
        contentManagementSystem = new DefaultContentManagementSystem(mockImcmsServices, new UserDomainObject());
    }

    public void testClonedCurrentUser() {
        User user = contentManagementSystem.getCurrentUser();
        assertNotNull(user);
        User userAgain = contentManagementSystem.getCurrentUser();
        assertNotNull(userAgain);
        assertNotSame(user.getInternal(), userAgain.getInternal());
    }

    public void testClone() throws CloneNotSupportedException {
        DefaultContentManagementSystem clone = (DefaultContentManagementSystem) contentManagementSystem.clone();
        assertNotSame(contentManagementSystem.currentUser, clone.currentUser);
    }

}
