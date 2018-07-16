package com.imcode.imcms.domain.factory;

import com.imcode.imcms.model.AuthenticationProvider;
import com.imcode.imcms.model.AzureAuthenticationProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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

        switch (externalAuthenticator.toLowerCase()) {
            case EXTERNAL_AUTHENTICATOR_AZURE_AD:
                providers.add(new AzureAuthenticationProvider(properties));
        }

        return providers;
    }

}
