package com.imcode.imcms.api;

import imcode.server.user.UserDomainObject;
import imcode.server.user.UserAndRoleMapper;

public class User {
    private UserDomainObject internalUser;
    private UserAndRoleMapper userAndRoleMapper;
    private SecurityChecker securityChecker;

    imcode.server.user.UserDomainObject getInternalUser() {
        return internalUser;
    }

    public User( UserDomainObject internalUser, UserAndRoleMapper userAndRoleMapper, SecurityChecker securityChecker ) {
        this.internalUser = internalUser;
        this.userAndRoleMapper = userAndRoleMapper;
        this.securityChecker = securityChecker;
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
        securityChecker.isSuperAdminOrSameUser(this);
        return internalUser.hasRole(userAndRoleMapper.getRoleByName( roleName )) ;
    }
}
