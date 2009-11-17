package com.imcode.imcms.api;

import imcode.server.user.UserDomainObject;

/**
 * Request info.
 *
 * @see imcode.server.Imcms
 * @see com.imcode.imcms.servlet.ImcmsFilter
 */
public class RequestInfo {

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
}
