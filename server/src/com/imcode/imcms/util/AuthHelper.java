package com.imcode.imcms.util;

import com.microsoft.aad.adal4j.AuthenticationResult;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

public final class AuthHelper {

    public static final String PRINCIPAL_SESSION_NAME = "principal";

    private AuthHelper() {
    }

    public static boolean isAuthenticated(HttpServletRequest request) {
        return request.getSession().getAttribute(PRINCIPAL_SESSION_NAME) != null;
    }

    public static boolean isAuthDataExpired(AuthenticationResult result) {
        return result.getExpiresOnDate().before(new Date());
    }

    public static AuthenticationResult getAuthenticationResult(HttpServletRequest request) {
        return (AuthenticationResult) request.getSession().getAttribute(PRINCIPAL_SESSION_NAME);
    }

    public static void setAuthenticationResult(HttpServletRequest httpRequest, AuthenticationResult result) {
        httpRequest.getSession().setAttribute(AuthHelper.PRINCIPAL_SESSION_NAME, result);
    }

    public static boolean containsAuthenticationData(HttpServletRequest httpRequest) {
        final boolean isPostMethod = httpRequest.getMethod().equalsIgnoreCase("POST");
        final Map<String, String[]> map = httpRequest.getParameterMap();
        final boolean hasAnyRequiredParameter = map.containsKey(AuthParameterNames.ERROR)
                || map.containsKey(AuthParameterNames.ID_TOKEN)
                || map.containsKey(AuthParameterNames.CODE);
        return (isPostMethod && hasAnyRequiredParameter);
    }

    public static boolean isAuthenticationSuccessful(AuthenticationResponse authResponse) {
        return (authResponse instanceof AuthenticationSuccessResponse);
    }
}
