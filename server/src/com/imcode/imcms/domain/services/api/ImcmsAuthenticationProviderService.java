package com.imcode.imcms.domain.services.api;

import com.imcode.imcms.domain.factory.AuthenticationProvidersFactory;
import com.imcode.imcms.domain.services.AuthenticationProviderService;
import com.imcode.imcms.model.AuthenticationProvider;

import java.util.List;

public class ImcmsAuthenticationProviderService implements AuthenticationProviderService {

    private final AuthenticationProvidersFactory authenticationProvidersFactory;

    public ImcmsAuthenticationProviderService(AuthenticationProvidersFactory authenticationProvidersFactory) {
        this.authenticationProvidersFactory = authenticationProvidersFactory;
    }

    @Override
    public List<AuthenticationProvider> getAuthenticationProviders() {
        return authenticationProvidersFactory.getProviders();
    }

    @Override
    public AuthenticationProvider getAuthenticationProvider(String identifierId) {
        return authenticationProvidersFactory.getProvider(identifierId);
    }
}
