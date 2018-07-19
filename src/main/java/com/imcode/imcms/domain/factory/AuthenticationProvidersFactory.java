package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.component.AzureAuthenticationProvider;
import com.imcode.imcms.domain.exception.ExternalIdentifierNotEnabledException;
import com.imcode.imcms.model.AuthenticationProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static com.imcode.imcms.domain.component.AzureAuthenticationProvider.EXTERNAL_AUTHENTICATOR_AZURE_AD;

@Component
public class AuthenticationProvidersFactory {

    private final Properties properties;

    public AuthenticationProvidersFactory(Properties imcmsProperties) {
        properties = imcmsProperties;
    }

    public List<AuthenticationProvider> getProviders() {
        final List<AuthenticationProvider> providers = new ArrayList<>();

        final String externalAuthenticator = properties.getProperty("ExternalAuthenticator", "");

        if (!externalAuthenticator.isEmpty()) {
            Optional.ofNullable(getProvider(externalAuthenticator)).ifPresent(providers::add);
        }

        return providers;
    }

    @SuppressWarnings("unchecked")
    public AuthenticationProvider getProvider(String identifierId) {
        switch (identifierId.toLowerCase()) {
            case EXTERNAL_AUTHENTICATOR_AZURE_AD:
                return new AzureAuthenticationProvider(properties);
        }

        throw new ExternalIdentifierNotEnabledException(identifierId);
    }
}
