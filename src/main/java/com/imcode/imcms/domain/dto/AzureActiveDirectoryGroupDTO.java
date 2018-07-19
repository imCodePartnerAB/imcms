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
    private String[] groupTypes;
    private boolean mailEnabled;
    private boolean securityEnabled;

}
