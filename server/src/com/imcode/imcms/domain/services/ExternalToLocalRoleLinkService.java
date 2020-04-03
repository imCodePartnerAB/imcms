package com.imcode.imcms.domain.services;

import com.imcode.imcms.domain.dto.ExternalRole;
import imcode.server.user.RoleDomainObject;

import java.util.Set;

public interface ExternalToLocalRoleLinkService {

    void setLinkedRoles(ExternalRole externalRole, Set<Integer> localRolesId);

    Set<RoleDomainObject> getLinkedLocalRoles(ExternalRole externalRole);

    Set<RoleDomainObject> toLinkedLocalRoles(Set<ExternalRole> externalRoles);
}
