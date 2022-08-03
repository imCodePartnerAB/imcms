package com.imcode.imcms.domain.component.cgi;

import com.imcode.imcms.domain.component.AuthenticationDataStorage;
import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.domain.dto.cgi.CGIExternalRole;
import com.imcode.imcms.domain.dto.cgi.CGIUserDTO;
import com.imcode.imcms.model.AuthenticationProvider;
import com.imcode.imcms.model.ExternalUser;
import lombok.SneakyThrows;
import org.apache.commons.collections.map.LRUMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensaml.saml2.core.AuthnRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class CGIAuthenticationProvider extends AuthenticationProvider implements AuthenticationDataStorage {

	private static final Logger logger = LogManager.getLogger(CGIAuthenticationProvider.class);
	public static final String EXTERNAL_AUTHENTICATOR_CGI = "cgi";
	public static final String EXTERNAL_USER_AND_ROLE_CGI = "cgi";
	private final ExternalRole cgiRole;
	private final CGIAuthHelper cgiAuthHelper = CGIAuthHelper.getInstance();
	private final LRUMap STATES = new LRUMap(512);

	public CGIAuthenticationProvider(Properties properties) {
		super(
				properties.getProperty("cgi.metadata-url"),
				EXTERNAL_AUTHENTICATOR_CGI,
				"BankId",
				"/imcms/images/external_identifiers/bank-id.svg"
		);
		cgiRole = new CGIExternalRole(properties.getProperty("cgi.user-role-name"));
	}

	@Override
	public String buildAuthenticationURL(String redirectURL, String sessionId, String nextUrl) {
		final String id = CGIRequestStore.getInstance().storeRequest();
		final AuthnRequest authnRequest = CGIUtils.buildRequest(id, redirectURL);

		storeAuthenticationData(sessionId, nextUrl);

		return CGIURLEncoder.getInstance().encode(authnRequest);
	}

	@SneakyThrows
	@Override
	public String processAuthentication(HttpServletRequest request) {
		final CGIUserDTO userDTO = CGIResponseVerifier.verifyAndGet(CGIUtils.decodeCGIResponse(request));

		cgiAuthHelper.setAuthenticationResult(request, userDTO);

		return (String) STATES.get(request.getSession().getId());
	}

	@Override
	public ExternalUser getUser(HttpServletRequest request) {
		final CGIUserDTO result = cgiAuthHelper.getAuthenticationResult(request);

		if (result == null) {
			throw new RuntimeException("CGIUserDTO not found in session.");
		}

		final ExternalUser user = result.toExternalUser();

		user.setExternalRoles(Collections.singleton(cgiRole));

		return user;
	}

	@Override
	public void updateAuthData(HttpServletRequest request) {
		if (cgiAuthHelper.isAuthDataExpired(cgiAuthHelper.getAuthenticationResult(request))) {
			processAuthentication(request);
		}
	}

	@Override
	public List<ExternalRole> getRoles() {
		return Collections.singletonList(cgiRole);
	}

	@Override
	public void storeAuthenticationData(String sessionId, String nextUrl) {
		STATES.put(sessionId, nextUrl);
	}
}
