package com.imcode.imcms.api;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;

public abstract class ContentManagementSystem {

    protected ImcmsServices service;
    protected SecurityChecker securityChecker;

    public abstract UserService getUserService();

    public abstract DocumentService getDocumentService();

    public abstract User getCurrentUser();

    public abstract DatabaseService getDatabaseService();

    public abstract TemplateService getTemplateService();

    public static ContentManagementSystem getContentManagementSystem( String userName, String password ) {
        ImcmsServices imcref;
        imcref = Imcms.getServices();
        UserDomainObject user = imcref.verifyUser( userName, password );
        ContentManagementSystem cms = new DefaultContentManagementSystem( imcref, user );
        return cms;
    }

    ImcmsServices getInternal() {
        return service ;
    }

    SecurityChecker getSecurityChecker() {
        return securityChecker;
    }
}
