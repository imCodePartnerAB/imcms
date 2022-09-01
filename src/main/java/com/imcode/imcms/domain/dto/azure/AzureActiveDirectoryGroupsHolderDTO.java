package com.imcode.imcms.domain.dto.azure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AzureActiveDirectoryGroupsHolderDTO {

    private AzureActiveDirectoryGroupDTO[] value;

    public List<AzureActiveDirectoryGroupDTO> getGroups() {
        return Arrays.asList(value);
    }

}
