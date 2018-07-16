package com.imcode.imcms.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Properties;

@Data
@EqualsAndHashCode(callSuper = false)
public class AzureAuthenticationProvider extends AuthenticationProvider {

    {
        providerName = "Azure Active Directory";
        providerId = "azure-ad";
    }

    public AzureAuthenticationProvider(Properties properties) {
        authenticationURL = "https://imcode.com/";
    }

}
