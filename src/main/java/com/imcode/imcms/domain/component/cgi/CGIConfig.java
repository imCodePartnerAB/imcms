package com.imcode.imcms.domain.component.cgi;

import imcode.server.Imcms;
import lombok.Data;
import org.apache.commons.httpclient.HttpClient;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.BasicParserPool;

import java.util.Timer;

@Data
public class CGIConfig {
	private static CGIConfig instance;
	private String spProviderId;
	private String idpSSOLoginUrl;
	private String idpSSOLogoutUrl;

	public static synchronized CGIConfig getInstance() {
		if (instance == null) {
			instance = new CGIConfig();
		}
		return instance;
	}

	private CGIConfig() {
		final String cgiMetadataUrl = Imcms.getServerProperties().getProperty("cgi.metadata-url");
		try {
			OpenSamlBootstrap.init();
			final HTTPMetadataProvider metadataResolver = new HTTPMetadataProvider(new Timer(), new HttpClient(), cgiMetadataUrl);
			metadataResolver.setParserPool(new BasicParserPool());
			metadataResolver.initialize();

			final EntityDescriptor entityDescriptor = (EntityDescriptor) metadataResolver.getMetadata();
			final IDPSSODescriptor idpssoDescriptor = entityDescriptor.getIDPSSODescriptor("urn:oasis:names:tc:SAML:2.0:protocol");

			idpssoDescriptor.getSingleSignOnServices().forEach(singleSignOnService -> {
				if(singleSignOnService.getBinding().equals(SAMLConstants.SAML2_REDIRECT_BINDING_URI)){
					idpSSOLoginUrl = singleSignOnService.getLocation();
				}
			});

//			idpssoDescriptor.getSingleLogoutServices().forEach(singleLogoutService -> {
//				idpSSOLogoutUrl = singleLogoutService.getLocation();
//			});

			this.spProviderId = Imcms.getServerProperties().getProperty("cgi.entity-id");
		} catch (MetadataProviderException e) {
			throw new RuntimeException(e);
		}
	}

}
