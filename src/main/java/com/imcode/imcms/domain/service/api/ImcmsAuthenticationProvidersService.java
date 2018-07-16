package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.factory.AuthenticationProvidersFactory;
import com.imcode.imcms.domain.service.AuthenticationProvidersService;
import com.imcode.imcms.model.AuthenticationProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
class ImcmsAuthenticationProvidersService implements AuthenticationProvidersService {

    private final AuthenticationProvidersFactory authenticationProvidersFactory;

    ImcmsAuthenticationProvidersService(AuthenticationProvidersFactory authenticationProvidersFactory) {
        this.authenticationProvidersFactory = authenticationProvidersFactory;
    }

    @Override
    public List<AuthenticationProvider> getAuthenticationProviders() {
        return authenticationProvidersFactory.getProviders();
    }

    @Override
    public Optional<AuthenticationProvider> getAuthenticationProvider(String identifierId) {
        return authenticationProvidersFactory.getProvider(identifierId);
    }
}
