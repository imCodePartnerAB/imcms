package com.imcode.imcms.api;

import imcode.server.user.UserDomainObject;
import imcode.server.IMCServiceInterface;
import imcode.server.ApplicationServer;

public class DefaultContentManagementSystem extends ContentManagementSystem {

    private UserService userService;
    private DocumentService documentService;
    private TemplateService templateService;
    private DatabaseService databaseService;
    private User currentUser;
    protected IMCServiceInterface service;
    protected SecurityChecker securityChecker;

    public DefaultContentManagementSystem( IMCServiceInterface service, UserDomainObject accessor ) {
        this.service = service;
        init( accessor );
    }

    private void init( UserDomainObject accessor ) {
        securityChecker = new SecurityChecker( this );
        currentUser = new User( accessor, this );
        userService = new UserService( this );
        documentService = new DocumentService( this );
        templateService = new TemplateService( this );
        databaseService = new DatabaseService( ApplicationServer.getApiConnectionPool() );
    }

    public UserService getUserService() {
        return userService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public TemplateService getTemplateService() {
        return templateService;
    }

    IMCServiceInterface getInternal() {
        return service;
    }

    SecurityChecker getSecurityChecker() {
        return securityChecker;
    }

}
