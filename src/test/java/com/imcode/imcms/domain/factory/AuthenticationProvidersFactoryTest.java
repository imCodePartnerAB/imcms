package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.component.azure.AzureAuthenticationProvider;
import com.imcode.imcms.model.AuthenticationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Properties;

import static com.imcode.imcms.domain.component.azure.AzureAuthenticationProvider.EXTERNAL_AUTHENTICATOR_AZURE_AD;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AuthenticationProvidersFactoryTest {

    private final Properties properties = new Properties();
    private final AuthenticationProvidersFactory authenticationProvidersFactory = new AuthenticationProvidersFactory(properties);

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
