package com.imcode.imcms.api;

import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;
import imcode.server.user.UserDomainObject;

public abstract class ContentManagementSystem {

    public abstract UserService getUserService();

    public abstract DocumentService getDocumentService();

    public abstract User getCurrentUser();

    public abstract DatabaseService getDatabaseService();

    public abstract TemplateService getTemplateService();

    public static ContentManagementSystem getContentManagementSystem( String userName, String password ) {
        IMCServiceInterface imcref = ApplicationServer.getIMCServiceInterface() ;
        UserDomainObject user = imcref.verifyUser( userName, password );
        ContentManagementSystem cms = new DefaultContentManagementSystem( imcref, user );
        return cms;
    }

    abstract IMCServiceInterface getInternal();

    abstract SecurityChecker getSecurityChecker();
}
