package com.imcode.imcms.api;

/**
 * @deprecated Use {@link ContentManagementSystem#fromRequest(javax.servlet.ServletRequest)} instead. Will be removed in imCMS 3.0 or later.
 */
public interface RequestConstants {
    /**
     *  Is used to get the ContentManagementSystem object from the request objects attributes.
     *
     * @deprecated Use {@link ContentManagementSystem#fromRequest(javax.servlet.ServletRequest)} instead.
     */
    public final static String SYSTEM = "com.imcode.imcms.ImcmsSystem";
}
