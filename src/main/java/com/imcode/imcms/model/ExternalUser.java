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
     * In UUID form
     */
    private String externalId;

    private Set<ExternalRole> externalRoles;

    {// default id for external users
        id = -1; // not sure is it good idea =) but it's better than default 0 (which is superadmin's id)
        setImcmsExternal(true);
    }

    public ExternalUser(String externalProviderId) {
        setExternalProviderId(externalProviderId);
    }
}
