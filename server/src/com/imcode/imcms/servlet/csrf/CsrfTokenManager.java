package com.imcode.imcms.servlet.csrf;

import imcode.server.user.UserDomainObject;

import javax.servlet.http.HttpServletRequest;

public interface CsrfTokenManager {

    boolean isExistTokenInCookies(HttpServletRequest request);

    boolean isCorrectTokenForCurrentUser(UserDomainObject user, HttpServletRequest request);

    boolean isTimeExpired(HttpServletRequest request);
}
