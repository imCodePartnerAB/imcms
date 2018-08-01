package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ExternalRole;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 01.08.18.
 */
public interface ExternalToLocalRoleLinkService {

    void addLink(ExternalRole externalRole, int localRoleId);

    void removeLink(ExternalRole externalRole, int localRoleId);

    void removeLinks(ExternalRole externalRole);

}
