package com.imcode.imcms.domain.component;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;
import java.util.Properties;

import static imcode.server.DefaultImcmsServices.EXTERNAL_AUTHENTICATOR_AZURE_AD;

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
        providerId = EXTERNAL_AUTHENTICATOR_AZURE_AD;
        iconPath = "/images_new/external_identifiers/azure-active-directory.svg";
    }

    public AzureAuthenticationProvider(Properties properties) {
        authenticationURL = "https://login.microsoftonline.com/";

        tenant = properties.getProperty("aad.tenant.name");
        clientId = properties.getProperty("aad.client.id");
        secretKey = properties.getProperty("aad.secret.key");

        Objects.requireNonNull(tenant, "Azure Active Directory tenant is null!");
        Objects.requireNonNull(clientId, "Azure Active Directory client id is null!");
        Objects.requireNonNull(secretKey, "Azure Active Directory secret directory is null!");
    }

}
