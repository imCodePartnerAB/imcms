package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.service.AuthenticationProvidersService;
import com.imcode.imcms.model.AuthenticationProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth-providers")
class AuthenticationProvidersController {

    private final AuthenticationProvidersService authenticationProvidersService;

    AuthenticationProvidersController(AuthenticationProvidersService authenticationProvidersService) {
        this.authenticationProvidersService = authenticationProvidersService;
    }

    @GetMapping
    public List<AuthenticationProvider> getAll() {
        return authenticationProvidersService.getAuthenticationProviders();
    }

}
