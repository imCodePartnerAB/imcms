package com.imcode.imcms.components.impl;

import com.imcode.imcms.components.CSRFTokenManager;
import imcode.server.user.UserDomainObject;
import imcode.util.Utility;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Component
public class ImcmsCSRFTokenManager implements CSRFTokenManager {

    public static final String IM_TOKEN = "im_token";
    public static final String IM_TOKEN_DATE = "im_token_date";
    private final int TOKEN_EXPIRATION_TIME_MINUTES = 2;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    @Override
    public boolean isTimeExpired(HttpServletRequest request) {
        return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - (long)request.getSession().getAttribute(IM_TOKEN_DATE)) > TOKEN_EXPIRATION_TIME_MINUTES;
    }

    @Override
    public void setUserToken(HttpServletRequest request, HttpServletResponse response) {
        final String token = generateToken();
        setTokenToSession(request, token);
        setTokenCookie(request, response, token);
    }

    @Override
    public void deleteUserToken(HttpServletRequest request, HttpServletResponse response) {
        removeTokenSession(request);
        removeTokenCookie(request, response);
    }

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

    private void setTokenToSession(HttpServletRequest request, String token) {
        final HttpSession currentSession = request.getSession();
        currentSession.setAttribute(IM_TOKEN, token);
        currentSession.setAttribute(IM_TOKEN_DATE, System.currentTimeMillis());
    }

    private void removeTokenSession(HttpServletRequest request) {
        request.getSession().setAttribute(IM_TOKEN, "");
    }

    private void setTokenCookie(HttpServletRequest request, HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(IM_TOKEN, token);
        cookie.setMaxAge(60 * 60 * 2);
        cookie.setPath("/");

        Utility.setCookieDomain(request, cookie);
        response.addCookie(cookie);
    }

    private void removeTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(IM_TOKEN, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");

        Utility.setCookieDomain(request, cookie);
        response.addCookie(cookie);
    }

    public String generateToken(){
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
