package com.imcode.imcms.components;

import imcode.server.user.UserDomainObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CSRFTokenManager {

    boolean isTimeExpired(HttpServletRequest req);

    void setUserToken(HttpServletRequest req, HttpServletResponse res);

    void deleteUserToken(HttpServletRequest req, HttpServletResponse res);

    boolean isExistTokenInCookies(HttpServletRequest req);

    boolean isCorrectTokenForCurrentUser(UserDomainObject user, HttpServletRequest req);
}
