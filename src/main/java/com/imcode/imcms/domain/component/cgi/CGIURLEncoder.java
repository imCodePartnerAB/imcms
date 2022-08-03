package com.imcode.imcms.domain.component.cgi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensaml.common.SAMLObject;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;
import org.opensaml.util.URLBuilder;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.util.Pair;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

public final class CGIURLEncoder extends HTTPRedirectDeflateEncoder {
	private final Logger logger = LogManager.getLogger(CGIURLEncoder.class);
	private static final CGIURLEncoder INSTANCE = new CGIURLEncoder();

	private CGIURLEncoder() {
	}

	public static CGIURLEncoder getInstance() {
		return INSTANCE;
	}

	public String encode(SAMLObject message) {
		try {
			final CGIConfig cgiConfig = CGIConfig.getInstance();
			final String relayState = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();

			final URLBuilder urlBuilder = new URLBuilder(cgiConfig.getIdpSSOLoginUrl());
			final List<Pair<String, String>> queryParams = urlBuilder.getQueryParams();

			queryParams.clear();
			queryParams.add(new Pair<>("mgvhostparam", "0"));
			queryParams.add(new Pair<>("SAMLRequest", deflateAndBase64Encode(message)));

			if (checkRelayState(relayState)) {
				queryParams.add(new Pair<>("RelayState", relayState));
			}

			return urlBuilder.buildURL();
		} catch (MessageEncodingException e) {
			logger.error("Error during message compression", e);
			throw new RuntimeException("Error during message compression", e);
		}
	}
}
