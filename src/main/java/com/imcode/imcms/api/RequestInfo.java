package com.imcode.imcms.api;

import imcode.server.user.UserDomainObject;

/**
 * Request info bound to thread local.
 *
 * @see imcode.server.Imcms
 * @see com.imcode.imcms.servlet.ImcmsFilter
 */
public class RequestInfo {

    public enum DocVersionMode {
        WORKING,
        DEFAULT
    }

    /**
     * 
     */
    public static class CustomDoc {
        
        public final Integer id;

        public final Integer versionNo;

        public CustomDoc(Integer id, Integer versionNo) {
            this.id = id;
            this.versionNo = versionNo;
        }
    }

    private DocVersionMode docVersionMode;

    private UserDomainObject user;

    private I18nLanguage language;

    private CustomDoc customDoc;

    public UserDomainObject getUser() {
        return user;
    }

    public void setUser(UserDomainObject user) {
        this.user = user;
    }

    public I18nLanguage getLanguage() {
        return language;
    }

    public void setLanguage(I18nLanguage language) {
        this.language = language;
    }

    public DocVersionMode getDocVersionMode() {
        return docVersionMode;
    }

    public void setDocVersionMode(DocVersionMode docVersionMode) {
        this.docVersionMode = docVersionMode;
    }

    public CustomDoc getCustomDoc() {
        return customDoc;
    }

    public void setCustomDoc(CustomDoc customDoc) {
        this.customDoc = customDoc;
    }
}
