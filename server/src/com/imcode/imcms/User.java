package com.imcode.imcms;

import imcode.server.user.UserDomainObject;

public class User {
    private UserDomainObject internalUser;

    imcode.server.user.UserDomainObject getInternalUser() {
        return internalUser;
    }

    public User( UserDomainObject internalUser ) {
        this.internalUser = internalUser;
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
}
