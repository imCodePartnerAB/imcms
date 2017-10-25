package com.imcode.imcms.api;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.sql.DataSource;
import java.security.KeyStore;

public class DefaultContentManagementSystem extends ContentManagementSystem implements Cloneable {

    protected ImcmsServices service;
    volatile UserDomainObject currentUser;
    private UserService userService;
    private DocumentService documentService;
    private TemplateService templateService;
    private DatabaseService databaseService;
    private MailService mailService;

    public DefaultContentManagementSystem(ImcmsServices service, UserDomainObject accessor) {
        this.service = service;
        currentUser = accessor;
    }

    public static DefaultContentManagementSystem create(ImcmsServices service, UserDomainObject accessor,
                                                        DataSource apiDataSource) {
        DefaultContentManagementSystem contentManagementSystem = new DefaultContentManagementSystem(service, accessor);
        contentManagementSystem.init(apiDataSource);
        return contentManagementSystem;
    }

    private void init(DataSource apiDataSource) {
        userService = new UserService(this);
        documentService = new DocumentService(this);
        templateService = new TemplateService(this);
        databaseService = new DatabaseService(apiDataSource);
        mailService = new MailService(service.getSMTP());
    }

    protected DefaultContentManagementSystem clone() throws CloneNotSupportedException {
        DefaultContentManagementSystem clone = (DefaultContentManagementSystem) super.clone();
        clone.currentUser = currentUser.clone();
        return clone;
    }

    public UserService getUserService() {
        return userService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public User getCurrentUser() {
        return new User(currentUser.clone());
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public TemplateService getTemplateService() {
        return templateService;
    }

    public MailService getMailService() {
        return mailService;
    }

    ImcmsServices getInternal() {
        return service;
    }

    public void runAsSuperadmin(ContentManagementSystemRunnable runnable) throws NoPermissionException {
        KeyStore keyStore = service.getKeyStore();
        Class clazz = runnable.getClass();
        if (!Utility.classIsSignedByCertificatesInKeyStore(clazz, keyStore)) {
            throw new NoPermissionException("Class " + clazz.getName() + " is not signed by certificates in keystore.");
        }
        DefaultContentManagementSystem cms = create(service, currentUser.clone(), Imcms.getApiDataSource());
        cms.currentUser.addRoleId(RoleId.SUPERADMIN);
        runnable.runWith(cms);
        cms.currentUser = null;
    }
}
