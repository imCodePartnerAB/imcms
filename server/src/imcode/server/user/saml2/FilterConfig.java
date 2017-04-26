package imcode.server.user.saml2;

import imcode.server.Config;
import imcode.server.Imcms;
import imcode.server.user.saml2.utils.OpenSamlBootstrap;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.BasicParserPool;

import java.util.Iterator;

public class FilterConfig {
	public static final String EXCLUDED_URL_PATTERN_PARAMETER = "excludedUrlPattern";
	public static final String SP_ACS_URL_PARAMETER = "acsUrl";
	public static final String SP_ID_PARAMETER = "spProviderId";
	public static final String SP_LOGOUT_URL_PARAMETER = "logoutUrl";
	public static final String AUTHENTICATION_METHOD_NAME_PROP = "cgi";
	private String excludedUrlPattern;
	private String acsUrl;
	private String spProviderId;
	private String logoutUrl;
	private String idpSSOLoginUrl;
	private String idpSSOLogoutUrl;
	private boolean isEnabled;

	private static FilterConfig instance;

	public static synchronized FilterConfig getInstance(javax.servlet.FilterConfig config) {
		if (instance == null)
		{
			instance = new FilterConfig(config);
		}
		return instance;
	}

	private FilterConfig(javax.servlet.FilterConfig config) {
		Config serverConfig = Imcms.getServices().getConfig();
		isEnabled = serverConfig.getAuthenticationConfiguration().containsKey(AUTHENTICATION_METHOD_NAME_PROP);
		if (isEnabled) {
			OpenSamlBootstrap.init();

			excludedUrlPattern = config.getInitParameter(EXCLUDED_URL_PATTERN_PARAMETER);
			spProviderId = serverConfig.getServerName();
			acsUrl = spProviderId + config.getServletContext().getContextPath() + "/acs";
			logoutUrl = "/servlet/LogOut";
			try { // code was decompiled because some guy made deploy but forgot to commit...
				HTTPMetadataProvider provider = new HTTPMetadataProvider(serverConfig.getCgiMetadataUrl(), 99999999);
				provider.setParserPool(new BasicParserPool());
				provider.initialize();
				EntityDescriptor entityDescriptor = (EntityDescriptor) provider.getMetadata();
				IDPSSODescriptor idpssoDescriptor = entityDescriptor.getIDPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol");
				Iterator services = idpssoDescriptor.getSingleSignOnServices().iterator();
				if (services.hasNext()) {
					SingleSignOnService singleLogoutService = (SingleSignOnService) services.next();
					idpSSOLoginUrl = singleLogoutService.getLocation();
				}

				services = idpssoDescriptor.getSingleLogoutServices().iterator();
				if (services.hasNext()) {
					SingleLogoutService singleLogoutService1 = (SingleLogoutService) services.next();
					idpSSOLogoutUrl = singleLogoutService1.getLocation();
				}
			} catch (MetadataProviderException e) {
				e.printStackTrace();
			}
		}
	}

	public String getExcludedUrlPattern() {
		return excludedUrlPattern;
	}

	public String getSpProviderId() {
		return spProviderId;
	}

	public String getIdpSSOLoginUrl() {
		return idpSSOLoginUrl;
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

	public String getIdpSSOLogoutUrl() {
		return idpSSOLogoutUrl;
	}

	public void setIdpSSOLogoutUrl(String idpSSOLogoutUrl) {
		this.idpSSOLogoutUrl = idpSSOLogoutUrl;
	}
}
