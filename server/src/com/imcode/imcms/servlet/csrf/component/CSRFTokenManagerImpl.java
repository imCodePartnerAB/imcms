package com.imcode.imcms.servlet.csrf.component;

import com.imcode.imcms.servlet.csrf.CsrfTokenManager;
import imcode.server.user.UserDomainObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static imcode.util.Utility.IM_TOKEN;
import static imcode.util.Utility.IM_TOKEN_DATE;

public class CSRFTokenManagerImpl implements CsrfTokenManager {

    private final int TOKEN_EXPIRATION_TIME = 2;

    @Override
    public boolean isExistTokenInCookies(HttpServletRequest request) {
         return Arrays.stream(request.getCookies())
                .anyMatch(cookie -> cookie.getName().equals(IM_TOKEN));
    }

    @Override
    public boolean isCorrectTokenForCurrentUser(UserDomainObject user, HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(IM_TOKEN))
                .anyMatch(cookie -> cookie.getValue().equals(request.getSession().getAttribute(IM_TOKEN)));
    }

    @Override
    public boolean isTimeExpired(HttpServletRequest request) {
        return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - (long)request.getSession().getAttribute(IM_TOKEN_DATE)) > TOKEN_EXPIRATION_TIME;
    }
}
