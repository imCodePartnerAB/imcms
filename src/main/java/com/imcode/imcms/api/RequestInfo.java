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

    private Integer docId;
    
    private Integer docVersionNo;

    private DocVersionMode docVersionMode;

    private UserDomainObject user;

    private I18nLanguage language;

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

    public Integer getDocVersionNo() {
        return docVersionNo;
    }

    public void setDocVersionNo(Integer docVersionNo) {
        this.docVersionNo = docVersionNo;
    }

    public DocVersionMode getDocVersionMode() {
        return docVersionMode;
    }

    public void setDocVersionMode(DocVersionMode docVersionMode) {
        this.docVersionMode = docVersionMode;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }
}
