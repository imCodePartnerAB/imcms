package com.imcode.imcms.api;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;

import javax.servlet.ServletRequest;

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
        ContentManagementSystem cms = DefaultContentManagementSystem.create( imcref, user );
        return cms;
    }

    /**
     * @return The ContentManagementSystem for the request
     * @since 2.0
     */ 
    public static ContentManagementSystem fromRequest(ServletRequest request) {
        return (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    }

    abstract ImcmsServices getInternal() ;

    abstract SecurityChecker getSecurityChecker() ;
}
