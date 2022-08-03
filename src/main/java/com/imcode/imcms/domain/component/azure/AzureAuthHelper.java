package com.imcode.imcms.domain.component.azure;

import com.imcode.imcms.model.AuthHelper;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

public final class AzureAuthHelper extends AuthHelper<AuthenticationResult> {
	private static final AzureAuthHelper INSTANCE = new AzureAuthHelper();

	private AzureAuthHelper() {
	}

	public static AzureAuthHelper getInstance() {
		return INSTANCE;
	}

	@Override
	public boolean isAuthenticated(HttpServletRequest request) {
		return request.getSession().getAttribute(PRINCIPAL_SESSION_NAME) != null;
	}

	@Override
	public boolean isAuthDataExpired(AuthenticationResult result) {
		return result.getExpiresOnDate().before(new Date());
	}

	@Override
	public AuthenticationResult getAuthenticationResult(HttpServletRequest request) {
		return (AuthenticationResult) request.getSession().getAttribute(PRINCIPAL_SESSION_NAME);
	}

	@Override
	public void setAuthenticationResult(HttpServletRequest httpRequest, AuthenticationResult result) {
		httpRequest.getSession().setAttribute(PRINCIPAL_SESSION_NAME, result);
	}

	@Override
	public boolean containsAuthenticationData(HttpServletRequest httpRequest) {
		final boolean isPostMethod = httpRequest.getMethod().equalsIgnoreCase("POST");

		final Map<String, String[]> map = httpRequest.getParameterMap();

		final boolean hasAnyRequiredParameter = map.containsKey(AuthParameterNames.ERROR)
				|| map.containsKey(AuthParameterNames.ID_TOKEN)
				|| map.containsKey(AuthParameterNames.CODE);

		return (isPostMethod && hasAnyRequiredParameter);
	}

	public boolean isAuthenticationSuccessful(AuthenticationResponse authResponse) {
		return (authResponse instanceof AuthenticationSuccessResponse);
	}
}
