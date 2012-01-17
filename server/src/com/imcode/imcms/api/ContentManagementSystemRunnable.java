package com.imcode.imcms.api;

/**
 * Used for running code and having access to {@link ContentManagementSystem} with user having super admin privileges.
 * @see {@link ContentManagementSystem#runAsSuperadmin(ContentManagementSystemRunnable)}
 */
public interface ContentManagementSystemRunnable {

    /**
     * Runs code with passed {@link ContentManagementSystem}
     * @param contentManagementSystem {@link ContentManagementSystem}
     */
    void runWith(ContentManagementSystem contentManagementSystem) ;

}