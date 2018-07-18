package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.component.AzureAuthenticationProvider;
import com.imcode.imcms.model.AuthenticationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Properties;

import static imcode.server.DefaultImcmsServices.EXTERNAL_AUTHENTICATOR_AZURE_AD;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationProvidersFactoryTest {

    private Properties properties = new Properties();
    private AuthenticationProvidersFactory authenticationProvidersFactory = new AuthenticationProvidersFactory(properties);

    @BeforeEach
    void setUp() {
        properties.clear();
    }

    @Test
    void getProviders_When_PropertiesDoesNotContainsExternalAuthenticator_Expect_EmptyListReturned() {
        properties.setProperty("ExternalAuthenticator", "");
        properties.setProperty("aad.tenant.name", "");
        properties.setProperty("aad.client.id", "");
        properties.setProperty("aad.secret.key", "");

        final List<AuthenticationProvider> providers = authenticationProvidersFactory.getProviders();

        assertTrue(providers.isEmpty());
    }

    @Test
    void getProviders_When_PropertiesContainsExternalAzureAuthenticator_Expect_ListWithAzureAuthenticatorReturned() {
        properties.setProperty("ExternalAuthenticator", EXTERNAL_AUTHENTICATOR_AZURE_AD);
        properties.setProperty("aad.tenant.name", "");
        properties.setProperty("aad.client.id", "");
        properties.setProperty("aad.secret.key", "");

        final List<AuthenticationProvider> providers = authenticationProvidersFactory.getProviders();

        assertFalse(providers.isEmpty());
        assertTrue(providers.get(0) instanceof AzureAuthenticationProvider);
    }

}
