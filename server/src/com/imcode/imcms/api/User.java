package com.imcode.imcms.api;

import imcode.server.user.UserDomainObject;

public class User {
    private UserDomainObject internalUser;
    private ContentManagementSystem contentManagementSystem;

    imcode.server.user.UserDomainObject getInternal() {
        return internalUser;
    }

    public User( UserDomainObject internalUser, ContentManagementSystem contentManagementSystem ) {
        this.internalUser = internalUser;
        this.contentManagementSystem = contentManagementSystem;
    }

    public String getLoginName() {
        return internalUser.getLoginName();
    }

    public String getCompany() {
        return internalUser.getCompany();
    }

    public String getFirstName() {
        return internalUser.getFirstName();
    }

    public String getLastName() {
        return internalUser.getLastName();
    }

    public String getTitle() {
        return internalUser.getTitle();
    }

    public String getAddress() {
        return internalUser.getAddress();
    }

    public String getCity() {
        return internalUser.getCity();
    }

    public String getZip() {
        return internalUser.getZip();
    }

    public String getCountry() {
        return internalUser.getCountry();
    }

    public String getCountyCouncil() {
        return internalUser.getCountyCouncil();
    }

    public String getEmailAddress() {
        return internalUser.getEmailAddress();
    }

    public String getWorkPhone() {
        return internalUser.getWorkPhone();
    }

    public String getMobilePhone() {
        return internalUser.getMobilePhone();
    }

    public String getHomePhone() {
        return internalUser.getHomePhone();
    }

    public boolean isActive() {
        return internalUser.isActive();
    }

    public String toString() {
        return getLoginName();
    }

    public boolean hasRole(String roleName) throws NoPermissionException {
        contentManagementSystem.getSecurityChecker().isSuperAdminOrSameUser(this);
        return internalUser.hasRole(contentManagementSystem.getInternal().getImcmsAuthenticatorAndUserAndRoleMapper().getRoleByName( roleName )) ;
    }

    public boolean isSuperAdmin() {
        return internalUser.isSuperAdmin() ;
    }
}
