package com.imcode.imcms.api;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;

public abstract class ContentManagementSystem {

    public abstract UserService getUserService();

    public abstract DocumentService getDocumentService();

    public abstract User getCurrentUser();

    public abstract DatabaseService getDatabaseService();

    public abstract TemplateService getTemplateService();

    public abstract MailService getMailService() ;

    public static ContentManagementSystem getContentManagementSystem( String userName, String password ) {
        ImcmsServices imcref;
        imcref = Imcms.getServices();
        UserDomainObject user = imcref.verifyUser( userName, password );
        ContentManagementSystem cms = new DefaultContentManagementSystem( imcref, user );
        return cms;
    }

    abstract ImcmsServices getInternal() ;

    abstract SecurityChecker getSecurityChecker() ;
}
