package com.imcode.imcms.domain.service;

import com.imcode.imcms.model.AuthenticationProvider;

import java.util.List;
import java.util.Optional;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 16.07.18.
 */
public interface AuthenticationProvidersService {

    List<AuthenticationProvider> getAuthenticationProviders();

    Optional<AuthenticationProvider> getAuthenticationProvider(String identifierId);
}
