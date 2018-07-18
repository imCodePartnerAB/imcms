package com.imcode.imcms.model;

import lombok.Getter;

import javax.servlet.http.HttpServletRequest;

public abstract class AuthenticationProvider {

    @Getter
    protected String authenticationURL;
    @Getter
    protected String providerId;
    @Getter
    protected String providerName;
    @Getter
    protected String iconPath;

    public abstract String buildAuthenticationURL(String redirectURL, String sessionId, String nextUrl);

    /**
     * Returns URI user should be redirected to
     */
    public abstract String processAuthentication(HttpServletRequest request) throws Exception;

}
