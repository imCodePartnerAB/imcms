package imcode.server.user.saml2.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;

/**
 * Created by Shadowgun on 20.11.2014.
 */
public class OpenSamlBootstrap extends DefaultBootstrap {
    private static Logger log = LogManager.getLogger(OpenSamlBootstrap.class);
    private static boolean initialized;
    private static String[] xmlToolingConfigs = {
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
                log.error("Unable to initialize opensaml DefaultBootstrap", e);
            }
            initializeGlobalSecurityConfiguration();
            initialized = true;
        }
    }
}
