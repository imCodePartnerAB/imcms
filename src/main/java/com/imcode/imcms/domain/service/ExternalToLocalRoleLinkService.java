package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.model.Role;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 01.08.18.
 */
public interface ExternalToLocalRoleLinkService {

    void addLink(ExternalRole externalRole, Role localRole);

    void removeLink(ExternalRole externalRole, Role localRole);

    void removeLinks(ExternalRole externalRole);

}
