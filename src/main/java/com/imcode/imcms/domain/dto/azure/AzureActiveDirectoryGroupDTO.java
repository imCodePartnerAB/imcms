package com.imcode.imcms.domain.dto.azure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imcode.imcms.domain.dto.ExternalRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.imcode.imcms.domain.component.azure.AzureAuthenticationProvider.EXTERNAL_USER_AND_ROLE_AZURE_AD;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureActiveDirectoryGroupDTO extends ExternalRole {

    private String createdDateTime;
    private String description;
    private String displayName;
    private String mail;
    private String mailNickname;
    private String onPremisesLastSyncDateTime;
    private String onPremisesSecurityIdentifier;
    private String visibility;
    private String deletedDateTime;
    private String classification;
    private String preferredDataLocation;
    private String renewedDateTime;
    private String[] groupTypes;
    private String[] proxyAddresses;
    private String[] creationOptions;
    private String[] onPremisesProvisioningErrors;
    private String[] resourceBehaviorOptions;
    private String[] resourceProvisioningOptions;
    private boolean mailEnabled;
    private boolean securityEnabled;
    private boolean onPremisesSyncEnabled;

    {
        providerId = EXTERNAL_USER_AND_ROLE_AZURE_AD;
    }

}
