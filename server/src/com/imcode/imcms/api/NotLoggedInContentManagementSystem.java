package com.imcode.imcms.api;

/**
 * @author kreiger
 */
public class NotLoggedInContentManagementSystem implements ContentManagementSystem {

    public UserService getUserService() throws NotLoggedInException {
        throw new NotLoggedInException() ;
    }

    public DocumentService getDocumentService() throws NotLoggedInException {
        throw new NotLoggedInException();
    }

    public User getCurrentUser() throws NotLoggedInException {
        throw new NotLoggedInException();
    }

    public DatabaseService getDatabaseService() throws NotLoggedInException {
        throw new NotLoggedInException();
    }

    public TemplateService getTemplateService() throws NotLoggedInException {
        throw new NotLoggedInException();
    }
}
