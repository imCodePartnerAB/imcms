package com.imcode.imcms.api;

import imcode.server.ApplicationServer;
import imcode.server.IMCServiceInterface;
import imcode.server.user.UserDomainObject;
import org.apache.log4j.Logger;

/**
 * @author kreiger
 */
public abstract class ContentManagementSystem {

    public abstract UserService getUserService() throws NotLoggedInException;

    public abstract DocumentService getDocumentService() throws NotLoggedInException;

    public abstract User getCurrentUser() throws NotLoggedInException;

    public abstract DatabaseService getDatabaseService() throws NotLoggedInException;

    public abstract TemplateService getTemplateService() throws NotLoggedInException;

    public static ContentManagementSystem getContentManagementSystem( String userName, String password ) {
        IMCServiceInterface imcref = null;
        imcref = ApplicationServer.getIMCServiceInterface();
        UserDomainObject user = imcref.verifyUser( userName, password );
        ContentManagementSystem cms = new DefaultContentManagementSystem( imcref, user );
        return cms;
    }
}
