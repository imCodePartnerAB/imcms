package com.imcode.imcms.domain.services;

import com.imcode.imcms.model.AuthenticationProvider;

import java.util.List;

public interface AuthenticationProviderService {

    List<AuthenticationProvider> getAuthenticationProviders();

    AuthenticationProvider getAuthenticationProvider(String identifierId);
}
