package com.imcode.imcms.domain.services;

import com.imcode.imcms.domain.dto.ExternalRole;
import imcode.server.user.RoleDomainObject;

import java.sql.SQLException;
import java.util.Set;

public interface ExternalToLocalRoleLinkService {

    void setLinkedRoles(ExternalRole externalRole, Set<Integer> localRolesId) throws SQLException;

    Set<RoleDomainObject> getLinkedLocalRoles(ExternalRole externalRole) throws SQLException;

    Set<RoleDomainObject> toLinkedLocalRoles(Set<ExternalRole> externalRoles) throws SQLException;
}
