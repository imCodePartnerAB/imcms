package com.imcode.imcms.api;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.RoleDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import java.security.KeyStore;

public class DefaultContentManagementSystem extends ContentManagementSystem implements Cloneable {

    private UserService userService;
    private DocumentService documentService;
    private TemplateService templateService;
    private DatabaseService databaseService;
    private MailService mailService;
    UserDomainObject currentUser;
    protected ImcmsServices service;
    protected SecurityChecker securityChecker;

    public DefaultContentManagementSystem( ImcmsServices service, UserDomainObject accessor ) {
        this.service = service ;
        currentUser = accessor;
    }

    public static DefaultContentManagementSystem create( ImcmsServices service, UserDomainObject accessor ) {
        DefaultContentManagementSystem contentManagementSystem = new DefaultContentManagementSystem( service, accessor );
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

    protected Object clone() throws CloneNotSupportedException {
        DefaultContentManagementSystem clone = (DefaultContentManagementSystem)super.clone() ;
        clone.currentUser = (UserDomainObject)currentUser.clone() ;
        return clone ;
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

    public void runAsSuperadmin( ContentManagementSystemRunnable runnable ) throws NoPermissionException {
        KeyStore keyStore = service.getKeyStore();
        Class clazz = runnable.getClass();
        if (!Utility.classIsSignedByCertificatesInKeyStore( clazz, keyStore )) {
            throw new NoPermissionException("Class "+clazz.getName()+" is not signed by certificates in keystore.") ;
        }
        DefaultContentManagementSystem cms = DefaultContentManagementSystem.create( service, (UserDomainObject)currentUser.clone() );
        cms.currentUser.addRole( RoleDomainObject.SUPERADMIN );
        runnable.runWith( cms );
        cms.currentUser = null;
    }
}
