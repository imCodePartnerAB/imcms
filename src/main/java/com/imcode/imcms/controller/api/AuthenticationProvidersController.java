package com.imcode.imcms.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping("/auth-providers")
class AuthenticationProvidersController {

    private final Properties properties;

    AuthenticationProvidersController(Properties imcmsProperties) {
        this.properties = imcmsProperties;
    }

    @GetMapping
    public List<Object> getAll() {
//        modelAndView.addObject("externalAuthenticator", properties.getProperty("ExternalAuthenticator"));
//        modelAndView.addObject("externalUserAndRoleMapper", properties.getProperty("ExternalUserAndRoleMapper"));
        return Collections.emptyList();
    }

}
