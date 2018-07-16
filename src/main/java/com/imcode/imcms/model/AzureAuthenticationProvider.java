package com.imcode.imcms.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Properties;

@Data
@EqualsAndHashCode(callSuper = false)
public class AzureAuthenticationProvider extends AuthenticationProvider {

    {
        authenticatorName = "Azure Active Directory";
    }

    public AzureAuthenticationProvider(Properties properties) {
        authenticationURL = "http://localhost:8080/dummy";
    }

}
