package com.imcode.imcms.domain.component.cgi;

import com.imcode.imcms.domain.dto.cgi.CGIUserDTO;
import com.imcode.imcms.model.AuthHelper;

import javax.servlet.http.HttpServletRequest;

public class CGIAuthHelper extends AuthHelper<CGIUserDTO> {
	private static final CGIAuthHelper INSTANCE = new CGIAuthHelper();

	private CGIAuthHelper() {
	}

	public static CGIAuthHelper getInstance() {
		return INSTANCE;
	}

	@Override
	public boolean isAuthenticated(HttpServletRequest request) {
		return request.getSession().getAttribute(PRINCIPAL_SESSION_NAME) != null;
	}

	@Override
	public boolean isAuthDataExpired(CGIUserDTO result) {
		return result.getValidTo().isBeforeNow();
	}

	@Override
	public CGIUserDTO getAuthenticationResult(HttpServletRequest request) {
		return (CGIUserDTO) request.getSession().getAttribute(PRINCIPAL_SESSION_NAME);
	}

	@Override
	public void setAuthenticationResult(HttpServletRequest httpRequest, CGIUserDTO result) {
		httpRequest.getSession().setAttribute(PRINCIPAL_SESSION_NAME, result);
	}

	@Override
	public boolean containsAuthenticationData(HttpServletRequest httpRequest) {
		// noop
		return false;
	}

}
