package com.imcode.imcms.servlet.csrf.component;

import com.imcode.imcms.servlet.csrf.CsrfTokenManager;
import imcode.server.user.UserDomainObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static imcode.util.Utility.IM_REMEMBER_CD;

public class CSRFTokenManagerImpl implements CsrfTokenManager {


    @Override
    public boolean isExistTokenInCookies(HttpServletRequest request) {
         return Arrays.stream(request.getCookies())
                .anyMatch(cookie -> cookie.getName().equals(IM_REMEMBER_CD));
    }

    @Override
    public boolean isCorrectTokenForCurrentUser(UserDomainObject user, HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(IM_REMEMBER_CD))
                .anyMatch(cookie -> cookie.getValue().equals(user.getRememberCd()));
    }
}
