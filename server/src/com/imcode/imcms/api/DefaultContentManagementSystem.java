package com.imcode.imcms.api;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;

public class DefaultContentManagementSystem extends ContentManagementSystem {

    private UserService userService;
    private DocumentService documentService;
    private TemplateService templateService;
    private DatabaseService databaseService;
    private MailService mailService;
    private UserDomainObject currentUser;
    protected ImcmsServices service;
    protected SecurityChecker securityChecker;

    public DefaultContentManagementSystem( ImcmsServices service, UserDomainObject accessor ) {
        this.service = service ;
        currentUser = accessor;
    }

    static DefaultContentManagementSystem create( ImcmsServices service, UserDomainObject accessor ) {
        DefaultContentManagementSystem contentManagementSystem = DefaultContentManagementSystem.create( service, accessor );
        contentManagementSystem.init();
        return contentManagementSystem ;
    }

    private void init() {
        securityChecker = new SecurityChecker( this );
        userService = new UserService( this );
        documentService = new DocumentService( this ) ;
        templateService = new TemplateService( this );
        databaseService = new DatabaseService( Imcms.getApiConnectionPool() );
        mailService = new MailService(this.service.getSMTP()) ;
    }

    public UserService getUserService(){
        return userService;
    }

    public DocumentService getDocumentService(){
        return documentService;
    }

    public User getCurrentUser() {
        return new User((UserDomainObject)currentUser.clone()) ;
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
