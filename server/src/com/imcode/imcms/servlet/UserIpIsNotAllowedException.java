package com.imcode.imcms.servlet;

import imcode.server.user.UserDomainObject;

import javax.servlet.ServletRequest;

/**
 * Exception throws when user with role have no IP in white list.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 23.11.17.
 */
public class UserIpIsNotAllowedException extends RuntimeException {

    private final UserDomainObject user;

    public UserIpIsNotAllowedException(UserDomainObject user, ServletRequest request) {
        super("Access denied: user " + user.getLoginName() + " with " + (user.isSuperAdmin() ? "super" : "non")
                + "-admin role and with IP " + request.getRemoteAddr() + " is not in white list.");
        this.user = user;
    }

    public UserDomainObject getUser() {
        return user;
    }
}
