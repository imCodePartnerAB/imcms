package imcode.server.user.saml2;

import imcode.server.AuthenticationMethodConfiguration;
import imcode.server.Imcms;

import java.util.Map;


/**
 * Created by Shadowgun on 20.11.2014.
 */
public class FilterConfig {
    /**
     * The parameters below should be defined in web.xml file of Java Web Application
     */
    public static final String EXCLUDED_URL_PATTERN_PARAMETER = "excludedUrlPattern";
    public static final String SP_ACS_URL_PARAMETER = "acsUrl";
    public static final String SP_ID_PARAMETER = "spProviderId";
    public static final String SP_LOGOUT_URL_PARAMETER = "logoutUrl";
    public static final String AUTHENTICATION_METHOD_NAME_PROP = "cgi";

    private String excludedUrlPattern;
    private String acsUrl;
    private String spProviderId;
    private String logoutUrl;
    private String idpSSOUrl;
    private Boolean isEnabled;

    public FilterConfig(javax.servlet.FilterConfig config) {
        Map<String, AuthenticationMethodConfiguration> configurationMap = Imcms.getAuthenticationConfiguration();
        isEnabled = configurationMap.containsKey(AUTHENTICATION_METHOD_NAME_PROP);
        excludedUrlPattern = config.getInitParameter(EXCLUDED_URL_PATTERN_PARAMETER);
        acsUrl = config.getInitParameter(SP_ACS_URL_PARAMETER);
        spProviderId = config.getInitParameter(SP_ID_PARAMETER);
        idpSSOUrl = isEnabled ? configurationMap.get(AUTHENTICATION_METHOD_NAME_PROP).getUrl():"";//config.getInitParameter(IDP_SSO_URL_PARAMETER);
        logoutUrl = config.getInitParameter(SP_LOGOUT_URL_PARAMETER);
    }

    public String getExcludedUrlPattern() {
        return excludedUrlPattern;
    }

    public String getSpProviderId() {
        return spProviderId;
    }

    public String getIdpSSOUrl() {
        return idpSSOUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public String getAcsUrl() {
        return acsUrl;
    }


    public Boolean isEnabled() {
        return isEnabled;
    }
    // getters and should be defined below
}
