package com.imcode.imcms.model;

import com.imcode.imcms.domain.dto.ExternalRole;
import imcode.server.user.UserDomainObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExternalUser extends UserDomainObject {

    /**
     * in UUID form
     */

    private String externalId;

    private Set<ExternalRole> externalRoles;

    {
        setImcmsExternal(true);
    }

    public ExternalUser(String externalProviderId) {
        setExternalProviderId(externalProviderId);
    }
}
