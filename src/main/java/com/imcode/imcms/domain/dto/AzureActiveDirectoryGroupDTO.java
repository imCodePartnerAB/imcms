package com.imcode.imcms.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureActiveDirectoryGroupDTO {

    private String id;
    private String createdDateTime;
    private String description;
    private String displayName;
    private String mail;
    private String mailNickname;
    private String onPremisesLastSyncDateTime;
    private String onPremisesSecurityIdentifier;
    private String visibility;
    private String[] groupTypes;
    private String[] proxyAddresses;
    private boolean mailEnabled;
    private boolean securityEnabled;
    private boolean onPremisesSyncEnabled;

}
