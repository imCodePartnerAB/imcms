package com.imcode.imcms.api;

import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract class acting as a base for full ContentManagementSystem implementations. Provides methods for getting ContentManagementSystem
 * for a given user, and retrieval of previously created ContentManagementSystem.
 * Provides means of running {@link ContentManagementSystemRunnable} with super admin privileges
 */
public abstract class ContentManagementSystem {

    /**
     * Returns {@link UserService}
     * @return UserService
     */
    public abstract UserService getUserService();

    /**
     * Returns {@link DocumentService}
     * @return DocumentService
     */
    public abstract DocumentService getDocumentService();

    /**
     * Returns current {@link User}
     * @return current user
     */
    public abstract User getCurrentUser();

    /**
     * Returns {@link DatabaseService}
     * @return DatabaseService
     */
    public abstract DatabaseService getDatabaseService();

    /**
     * Returns {@link TemplateService}
     * @return TemplateService
     */
    public abstract TemplateService getTemplateService();

    /**
     * Returns {@link MailService}
     * @return MailService
     */
    public abstract MailService getMailService() ;

    /**
     * Runs {@link ContentManagementSystemRunnable#runWith(ContentManagementSystem)} with passed ContentManagementSystem's
     * user having super admin privileges.
     * @param runnable class implementing ContentManagementSystemRunnable
     * @throws NoPermissionException if ContentManagementSystemRunnable implementer is not signed with the key store
     */
    public abstract void runAsSuperadmin(ContentManagementSystemRunnable runnable) throws NoPermissionException;

    /**
     * Get a ContentManagementSystem for the given username and password.
     * @param userName user's login name
     * @param password user's password
    **/
    public static ContentManagementSystem getContentManagementSystem( String userName, String password ) {
        ImcmsServices imcref = Imcms.getServices();
        UserDomainObject user = imcref.verifyUser( userName, password );
        return DefaultContentManagementSystem.create( imcref, user, Imcms.getApiDataSource());
    }

    /**
     * Attempts to login the current user with the given name and password. Returns ContentManagementSystem for that user
     * if login is successful.
     *
     * @param request HttpServletRequest request
     * @param response HttpServletResponse response
     * @param username user's login name
     * @param password user's password
     * @return The new ContentManagementSystem, or null if the login failed.
     */
    public static ContentManagementSystem login(HttpServletRequest request, HttpServletResponse response, String username, String password) {
        ImcmsServices services = Imcms.getServices();
        UserDomainObject user = services.verifyUser(username, password);

        if ( null == user || user.isDefaultUser()) {
            return null ;
        }
        
        ContentManagementSystem cms = Utility.initRequestWithApi(request, user);

        if (services.getConfig().isDenyMultipleUserLogin()) {
            User currentUser = cms.getCurrentUser();
            currentUser.setSessionId(request.getSession().getId());
            cms.getUserService().updateUserSession(currentUser);
        }
        
        String rememberCd = user.getRememberCd();
        if (StringUtils.isEmpty(rememberCd)) {
        	cms.getUserService().updateUserRememberCd(user);
        }
        Utility.setRememberCdCookie(request, response, user.getRememberCd());
        
        Utility.makeUserLoggedIn(request, user);

        return cms;
    }

    /**
     * Returns ContentManagementSystem set in the given request.
     * @param request ServletRequest to get ContentManagementSystem from
     * @return The ContentManagementSystem for the request
     * @since 2.0
     */
    public static ContentManagementSystem fromRequest(ServletRequest request) {
        return Utility.getContentManagementSystemFromRequest(request) ;
    }

    abstract ImcmsServices getInternal() ;

}
