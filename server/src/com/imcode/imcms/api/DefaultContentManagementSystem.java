package com.imcode.imcms.api;

import imcode.server.ImcmsServices;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;

public class DefaultContentManagementSystem extends ContentManagementSystem {

    private UserService userService;
    private DocumentService documentService;
    private TemplateService templateService;
    private DatabaseService databaseService;
    private MailService mailService;
    private User currentUser;
    protected ImcmsServices service;
    protected SecurityChecker securityChecker;

    public DefaultContentManagementSystem( ImcmsServices service, UserDomainObject accessor ) {
        this.service = service ;
        init( accessor );
    }

    private void init( UserDomainObject accessor ) {
        securityChecker = new SecurityChecker( this );
        currentUser = new User( accessor, this );
        userService = new UserService( this );
        documentService = new DocumentService( this ) ;
        templateService = new TemplateService( this );
        databaseService = new DatabaseService( Imcms.getApiConnectionPool() );
        mailService = new MailService(service.getSMTP()) ;
    }

    public UserService getUserService(){
        return userService;
    }

    public DocumentService getDocumentService(){
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

    public MailService getMailService() {
        return mailService ;
    }

    ImcmsServices getInternal() {
        return service ;
    }

    SecurityChecker getSecurityChecker() {
        return securityChecker ;
    }

}
