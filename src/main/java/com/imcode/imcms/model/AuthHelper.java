package com.imcode.imcms.model;

import javax.servlet.http.HttpServletRequest;

public abstract class AuthHelper<T> {
	public static final String PRINCIPAL_SESSION_NAME = "principal";

	public abstract boolean isAuthenticated(HttpServletRequest request);

	public abstract boolean isAuthDataExpired(T result);

	public abstract T getAuthenticationResult(HttpServletRequest request);

	public abstract void setAuthenticationResult(HttpServletRequest httpRequest, T result);

	public abstract boolean containsAuthenticationData(HttpServletRequest httpRequest);
}
