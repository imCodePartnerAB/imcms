package com.imcode.imcms.domain.component.cgi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;

public final class OpenSamlBootstrap extends DefaultBootstrap {
	private static final Logger logger = LogManager.getLogger(OpenSamlBootstrap.class);
	private static boolean initialized;
	private static final String[] xmlToolingConfigs = {
			"/default-config.xml",
			"/encryption-validation-config.xml",
			"/saml2-assertion-config.xml",
			"/saml2-assertion-delegation-restriction-config.xml",
			"/saml2-core-validation-config.xml",
			"/saml2-metadata-config.xml",
			"/saml2-metadata-idp-discovery-config.xml",
			"/saml2-metadata-query-config.xml",
			"/saml2-metadata-validation-config.xml",
			"/saml2-protocol-config.xml",
			"/saml2-protocol-thirdparty-config.xml",
			"/schema-config.xml",
			"/signature-config.xml",
			"/signature-validation-config.xml"
	};

	public static synchronized void init() {
		if (!initialized) {
			try {
				initializeXMLTooling(xmlToolingConfigs);
				bootstrap();
			} catch (ConfigurationException e) {
				logger.error("Unable to initialize opensaml DefaultBootstrap", e);
			}
			initializeGlobalSecurityConfiguration();
			initialized = true;
		}
	}
}
