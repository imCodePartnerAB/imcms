package com.imcode.imcms.domain.services.api;

import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.domain.services.ExternalToLocalRoleLinkService;
import imcode.server.user.RoleDomainObject;

import java.util.Set;

public class DefaultExternalToLocalRoleLinkService implements ExternalToLocalRoleLinkService {


    @Override
    public void setLinkedRoles(ExternalRole externalRole, Set<Integer> localRolesId) {

    }

    @Override
    public Set<RoleDomainObject> getLinkedLocalRoles(ExternalRole externalRole) {
        return null;
    }

    @Override
    public Set<RoleDomainObject> toLinkedLocalRoles(Set<ExternalRole> externalRoles) {
        return null;
    }
}
