package com.imcode.imcms.api;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;

public abstract class ContentManagementSystem {

    protected IMCServiceInterface service;
    protected SecurityChecker securityChecker;

    public abstract UserService getUserService();

    public abstract DocumentService getDocumentService();

    public abstract User getCurrentUser();

    public abstract DatabaseService getDatabaseService();

    public abstract TemplateService getTemplateService();

    public static ContentManagementSystem getContentManagementSystem( String userName, String password ) {
        IMCServiceInterface imcref;
        imcref = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = imcref.verifyUser( userName, password );
        ContentManagementSystem cms = new DefaultContentManagementSystem( imcref, user );
        return cms;
    }

    IMCServiceInterface getInternal() {
        return service ;
    }

    SecurityChecker getSecurityChecker() {
        return securityChecker;
    }
}
