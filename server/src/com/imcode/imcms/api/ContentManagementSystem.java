package com.imcode.imcms.api;

/**
 * @author kreiger
 */
public interface ContentManagementSystem {
    UserService getUserService() throws NotLoggedInException;

    DocumentService getDocumentService() throws NotLoggedInException;

    User getCurrentUser() throws NotLoggedInException;

    DatabaseService getDatabaseService() throws NotLoggedInException;

    TemplateService getTemplateService() throws NotLoggedInException;
}
