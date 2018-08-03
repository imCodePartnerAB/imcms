package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.model.Role;

import java.util.Set;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 01.08.18.
 */
public interface ExternalToLocalRoleLinkService {

    void setLinkedRoles(ExternalRole externalRole, Set<Integer> localRolesId);

    void addLink(ExternalRole externalRole, int localRoleId);

    void removeLink(ExternalRole externalRole, int localRoleId);

    void removeLinks(ExternalRole externalRole);

    Set<Role> getLinkedLocalRoles(ExternalRole externalRole);
}
