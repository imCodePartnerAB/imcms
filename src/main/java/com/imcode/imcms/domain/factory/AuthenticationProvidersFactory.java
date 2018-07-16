package com.imcode.imcms.domain.factory;

import com.imcode.imcms.model.AuthenticationProvider;
import com.imcode.imcms.model.AzureAuthenticationProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static imcode.server.DefaultImcmsServices.EXTERNAL_AUTHENTICATOR_AZURE_AD;

@Component
public class AuthenticationProvidersFactory {

    private final Properties properties;

    public AuthenticationProvidersFactory(Properties imcmsProperties) {
        properties = imcmsProperties;
    }

    public List<AuthenticationProvider> getProviders() {
        final List<AuthenticationProvider> providers = new ArrayList<>();

        final String externalAuthenticator = properties.getProperty("ExternalAuthenticator", "");

        getProvider(externalAuthenticator).ifPresent(providers::add);

        return providers;
    }

    public Optional<AuthenticationProvider> getProvider(String identifierId) {
        switch (identifierId.toLowerCase()) {
            case EXTERNAL_AUTHENTICATOR_AZURE_AD:
                return Optional.of(new AzureAuthenticationProvider(properties));
        }

        return Optional.empty();
    }
}
