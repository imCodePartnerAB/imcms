package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.component.AzureAuthenticationProvider;
import com.imcode.imcms.domain.exception.ExternalIdentifierNotEnabledException;
import com.imcode.imcms.model.AuthenticationProvider;
import imcode.server.DefaultImcmsServices;

import java.util.*;

import static com.imcode.imcms.domain.component.AzureAuthenticationProvider.EXTERNAL_AUTHENTICATOR_AZURE_AD;

public class AuthenticationProvidersFactory {

    private final Properties properties;
    private final Map<String, AuthenticationProvider> idToProvider = new HashMap<>();

    public AuthenticationProvidersFactory(Properties imcmsProperties) {
        properties = imcmsProperties;
    }

    public List<AuthenticationProvider> getProviders() {
        final List<AuthenticationProvider> providers = new ArrayList<>();
        final String externalAuthenticator = properties.getProperty("ExternalAuthenticator", "");

		//ldap causes errors because there are no such provider...
        if (!externalAuthenticator.isEmpty() && !externalAuthenticator.equalsIgnoreCase(DefaultImcmsServices.EXTERNAL_AUTHENTICATOR_LDAP)) {
            providers.add(getProvider(externalAuthenticator));
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
        }

        throw new ExternalIdentifierNotEnabledException(identifierId);
    }
}
