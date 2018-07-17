package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.component.AzureAuthenticationProvider;
import com.imcode.imcms.model.AuthenticationProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Properties;

import static imcode.server.DefaultImcmsServices.EXTERNAL_AUTHENTICATOR_AZURE_AD;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthenticationProvidersFactoryTest {

    @Mock
    private Properties properties;

    @InjectMocks
    private AuthenticationProvidersFactory authenticationProvidersFactory;

    @Test
    void getProviders_When_PropertiesDoesNotContainsExternalAuthenticator_Expect_EmptyListReturned() {
        given(properties.getProperty("ExternalAuthenticator", "")).willReturn("");
        final List<AuthenticationProvider> providers = authenticationProvidersFactory.getProviders();
        assertTrue(providers.isEmpty());
    }

    @Test
    void getProviders_When_PropertiesContainsExternalAzureAuthenticator_Expect_ListWithAzureAuthenticatorReturned() {
        given(properties.getProperty("ExternalAuthenticator", "")).willReturn(EXTERNAL_AUTHENTICATOR_AZURE_AD);
        final List<AuthenticationProvider> providers = authenticationProvidersFactory.getProviders();

        assertFalse(providers.isEmpty());
        assertTrue(providers.get(0) instanceof AzureAuthenticationProvider);
    }

}
