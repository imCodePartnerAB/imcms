package com.imcode.imcms.api;

/**
 * Used with {@link ContentManagementSystem#runAsSuperadmin(ContentManagementSystemRunnable)}
 */
public interface ContentManagementSystemRunnable {

    /**
     * Runs with passed {@link ContentManagementSystem}
     * @param contentManagementSystem {@link ContentManagementSystem}
     */
    void runWith(ContentManagementSystem contentManagementSystem) ;

}