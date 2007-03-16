package com.imcode.imcms.api;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public abstract class ContentManagementSystem {

    public abstract UserService getUserService();

    public abstract DocumentService getDocumentService();

    public abstract User getCurrentUser();

    public abstract DatabaseService getDatabaseService();

    public abstract TemplateService getTemplateService();

    public abstract MailService getMailService() ;

    public abstract void runAsSuperadmin(ContentManagementSystemRunnable runnable) throws NoPermissionException;

    /**
        Get a ContentManagementSystem for the given username and password.
    **/
    public static ContentManagementSystem getContentManagementSystem( String userName, String password ) {
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = imcref.verifyUser( userName, password );
        return DefaultContentManagementSystem.create( imcref, user, Imcms.getApiDataSource());
    }

    /**
     * Try to login the current user with the given name and password.
     *
     * @param request
     * @param username
     * @param password
     * @return The new ContentManagementSystem, or null if the login failed.
     */
    public static ContentManagementSystem login(HttpServletRequest request, String username, String password) {
        ImcmsServices services = Imcms.getServices();
        UserDomainObject user = services.verifyUser(username, password);

        if ( null == user ) {
            return null ;
        }

        Utility.makeUserLoggedIn(request, user);
        return Utility.initRequestWithApi(request, user) ;
    }

    /**
     * @return The ContentManagementSystem for the request
     * @since 2.0
     */
    public static ContentManagementSystem fromRequest(ServletRequest request) {
        return Utility.getContentManagementSystemFromRequest(request) ;
    }

    abstract ImcmsServices getInternal() ;

    abstract SecurityChecker getSecurityChecker() ;
}
