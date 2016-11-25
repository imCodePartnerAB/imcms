package com.imcode.imcms.api;

import imcode.server.ImcmsServices;
import imcode.server.user.UserDomainObject;

public class MockContentManagementSystem extends ContentManagementSystem {

    private ImcmsServices imcmsServices;
    private User currentUser;

    public UserService getUserService() {
        return null;  // TODO
    }

    public DocumentService getDocumentService() {
        return null;  // TODO
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public DatabaseService getDatabaseService() {
        return null;  // TODO
    }

    public TemplateService getTemplateService() {
        return null;  // TODO
    }

    public MailService getMailService() {
        return null;  // TODO
    }

    public void runAsSuperadmin( ContentManagementSystemRunnable runnable ) throws NoPermissionException {
    }

    ImcmsServices getInternal() {
        return imcmsServices;
    }

    public void setInternal( ImcmsServices imcmsServices ) {
        this.imcmsServices = imcmsServices;
    }

    public void setCurrentUser( User user ) {
        currentUser = user;
    }

    public void setCurrentInternalUser( UserDomainObject user ) {
        currentUser = new User( user );
    }
}
