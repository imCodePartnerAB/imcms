package com.imcode.imcms.api;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.security.KeyStore;

public class ContentManagementSystem implements Cloneable {

    protected ImcmsServices service;
    volatile UserDomainObject currentUser;
    private UserService userService;
    private DocumentService documentService;
    private TemplateService templateService;
    private DatabaseService databaseService;
    private MailService mailService;

    ContentManagementSystem(ImcmsServices service, UserDomainObject accessor) {
        this.service = service;
        currentUser = accessor;
    }

    /**
     * Get a ContentManagementSystem for the given username and password.
     */
    public static ContentManagementSystem getContentManagementSystem(String userName, String password) {
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = imcref.verifyUser(userName, password);
        return ContentManagementSystem.create(imcref, user, Imcms.getApiDataSource());
    }

    /**
     * Try to login the current user with the given name and password.
     *
     * @return The new ContentManagementSystem, or null if the login failed.
     */
    public static ContentManagementSystem login(HttpServletRequest request, String username, String password) {
        ImcmsServices services = Imcms.getServices();
        UserDomainObject user = services.verifyUser(username, password);

        if (null == user || user.isDefaultUser()) {
            return null;
        }

        ContentManagementSystem cms = Utility.initRequestWithApi(request, user);

        if (services.getConfig().isDenyMultipleUserLogin()) {
            User currentUser = cms.getCurrentUser();
            currentUser.setSessionId(request.getSession().getId());
            cms.getUserService().updateUserSession(currentUser);
        }

        Utility.makeUserLoggedIn(request, user);

        return cms;
    }

    /**
     * @return The ContentManagementSystem for the request
     * @since 2.0
     */
    public static ContentManagementSystem fromRequest(ServletRequest request) {
        return Utility.getContentManagementSystemFromRequest(request);
    }

    public static ContentManagementSystem create(ImcmsServices service, UserDomainObject accessor,
                                                 DataSource apiDataSource) {
        ContentManagementSystem contentManagementSystem = new ContentManagementSystem(service, accessor);
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

    protected ContentManagementSystem clone() throws CloneNotSupportedException {
        ContentManagementSystem clone = (ContentManagementSystem) super.clone();
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
        ContentManagementSystem cms = create(service, currentUser.clone(), Imcms.getApiDataSource());
        cms.currentUser.addRoleId(RoleId.SUPERADMIN);
        runnable.runWith(cms);
        cms.currentUser = null;
    }
}
