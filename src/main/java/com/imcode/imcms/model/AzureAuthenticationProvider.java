package com.imcode.imcms.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;
import java.util.Properties;

/**
 * Authentication provider for Azure Active Directory
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AzureAuthenticationProvider extends AuthenticationProvider {

    private final String tenant;
    private final String clientId;
    private final String secretKey;
    private final String authority = "https://login.microsoftonline.com/";

    {
        providerName = "Azure Active Directory";
        providerId = "azure-ad";
    }

    public AzureAuthenticationProvider(Properties properties) {
        authenticationURL = "https://imcode.com/";

        tenant = properties.getProperty("aad.tenant.name");
        clientId = properties.getProperty("aad.client.id");
        secretKey = properties.getProperty("aad.secret.key");

        Objects.requireNonNull(tenant, "Azure Active Directory tenant is null!");
        Objects.requireNonNull(clientId, "Azure Active Directory client id is null!");
        Objects.requireNonNull(secretKey, "Azure Active Directory secret directory is null!");
    }

}
