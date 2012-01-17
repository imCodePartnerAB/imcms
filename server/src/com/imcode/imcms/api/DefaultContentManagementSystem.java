package com.imcode.imcms.api;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.sql.DataSource;
import java.security.KeyStore;

/**
 * The default implementation of ContentManagementSystem, supporting services like {@link UserService}, {@link DocumentService},
 * {@link TemplateService}, {@link DatabaseService}, {@link MailService}
 */
public class DefaultContentManagementSystem extends ContentManagementSystem implements Cloneable {

    private UserService userService;
    private DocumentService documentService;
    private TemplateService templateService;
    private DatabaseService databaseService;
    private MailService mailService;
    UserDomainObject currentUser;
    protected ImcmsServices service;

    /**
     * Constructs DefaultContentManagementSystem for the given user, does not initiallize the services like {@link UserService} etc
     * @param service ImcmsServices
     * @param accessor UserDomainObject becomes current user
     */
    public DefaultContentManagementSystem( ImcmsServices service, UserDomainObject accessor ) {
        this.service = service ;
        currentUser = accessor;
    }

    /**
     * Constructs DefaultContentManagementSystem and initializes services
     * @param service ImcmsServices
     * @param accessor UserDomainObject becomes current user
     * @param apiDataSource DataSource to be user for {@link DatabaseService}
     * @return DefaultContentManagementSystem with initialized services
     */
    public static DefaultContentManagementSystem create(ImcmsServices service, UserDomainObject accessor,
                                                        DataSource apiDataSource) {
        DefaultContentManagementSystem contentManagementSystem = new DefaultContentManagementSystem( service, accessor );
        contentManagementSystem.init(apiDataSource);
        return contentManagementSystem ;
    }

    private void init(DataSource apiDataSource) {
        userService = new UserService( this );
        documentService = new DocumentService( this ) ;
        templateService = new TemplateService( this );
        databaseService = new DatabaseService( apiDataSource );
        mailService = new MailService(service.getSMTP()) ;
    }

    protected Object clone() throws CloneNotSupportedException {
        DefaultContentManagementSystem clone = (DefaultContentManagementSystem)super.clone() ;
        clone.currentUser = (UserDomainObject)currentUser.clone() ;
        return clone ;
    }

    /**
     * Returns {@link UserService}
     * @return UserService
     */
    public UserService getUserService(){
        return userService;
    }

    /**
     * Returns {@link DocumentService}
     * @return DocumentService
     */
    public DocumentService getDocumentService(){
        return documentService;
    }

    /**
     * Returns current {@link User}
     * @return current user
     */
    public User getCurrentUser() {
        return new User((UserDomainObject)currentUser.clone()) ;
    }

    /**
     * Returns {@link DatabaseService}
     * @return DatabaseService
     */
    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    /**
     * Returns {@link TemplateService}
     * @return TemplateService
     */
    public TemplateService getTemplateService() {
        return templateService;
    }

    /**
     * Returns {@link MailService}
     * @return MailService
     */
    public MailService getMailService() {
        return mailService ;
    }

    ImcmsServices getInternal() {
        return service ;
    }

    /**
     * Runs {@link ContentManagementSystemRunnable#runWith(ContentManagementSystem)} with passed ContentManagementSystem's
     * user having super admin privileges.
     * @param runnable class implementing ContentManagementSystemRunnable
     * @throws NoPermissionException if ContentManagementSystemRunnable implementer is not signed with the key store
     */
    public void runAsSuperadmin( ContentManagementSystemRunnable runnable ) throws NoPermissionException {
        KeyStore keyStore = service.getKeyStore();
        Class clazz = runnable.getClass();
        if (!Utility.classIsSignedByCertificatesInKeyStore( clazz, keyStore )) {
            throw new NoPermissionException("Class "+clazz.getName()+" is not signed by certificates in keystore.") ;
        }
        DefaultContentManagementSystem cms = create( service, (UserDomainObject)currentUser.clone(), Imcms.getApiDataSource());
        cms.currentUser.addRoleId( RoleId.SUPERADMIN );
        runnable.runWith( cms );
        cms.currentUser = null;
    }
}
