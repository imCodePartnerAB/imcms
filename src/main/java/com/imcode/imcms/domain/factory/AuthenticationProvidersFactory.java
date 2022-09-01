package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.component.azure.AzureAuthenticationProvider;
import com.imcode.imcms.domain.component.cgi.CGIAuthenticationProvider;
import com.imcode.imcms.domain.exception.ExternalIdentifierNotEnabledException;
import com.imcode.imcms.model.AuthenticationProvider;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.imcode.imcms.domain.component.azure.AzureAuthenticationProvider.EXTERNAL_AUTHENTICATOR_AZURE_AD;
import static com.imcode.imcms.domain.component.cgi.CGIAuthenticationProvider.EXTERNAL_AUTHENTICATOR_CGI;

@Component
public class AuthenticationProvidersFactory {

    private final Properties properties;

    private final Map<String, AuthenticationProvider> idToProvider = new HashMap<>();

    public AuthenticationProvidersFactory(Properties imcmsProperties) {
        properties = imcmsProperties;
    }

    public List<AuthenticationProvider> getProviders() {
        final List<AuthenticationProvider> providers = new ArrayList<>();

        final String externalAuthenticator = properties.getProperty("ExternalAuthenticator", "");
	    final boolean cgiAuthenticatorEnabled = Boolean.parseBoolean(properties.getProperty("cgi.enabled"));

        if (!externalAuthenticator.isEmpty()) {
            providers.add(getProvider(externalAuthenticator));
        }

		if (cgiAuthenticatorEnabled){
			providers.add(getProvider(EXTERNAL_AUTHENTICATOR_CGI));
		}

        return providers;
    }

    public AuthenticationProvider getProvider(String identifierId) {
        identifierId = identifierId.toLowerCase();

        final AuthenticationProvider authenticationProvider = idToProvider.get(identifierId);

        if (authenticationProvider != null) return authenticationProvider;

        switch (identifierId) {
            case EXTERNAL_AUTHENTICATOR_AZURE_AD:
                final AzureAuthenticationProvider provider = new AzureAuthenticationProvider(properties);
                idToProvider.put(EXTERNAL_AUTHENTICATOR_AZURE_AD, provider);
                return provider;
	        case EXTERNAL_AUTHENTICATOR_CGI:
		        final CGIAuthenticationProvider cgiAuthenticationProvider = new CGIAuthenticationProvider(properties);
		        idToProvider.put(EXTERNAL_AUTHENTICATOR_CGI, cgiAuthenticationProvider);
		        return cgiAuthenticationProvider;
        }

        throw new ExternalIdentifierNotEnabledException(identifierId);
    }
}
