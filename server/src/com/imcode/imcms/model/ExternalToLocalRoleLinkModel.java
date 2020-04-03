package com.imcode.imcms.model;

import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.domain.dto.ExternalToLocalRoleLink;
import imcode.server.user.RoleDomainObject;

public class ExternalToLocalRoleLinkModel extends ExternalToLocalRoleLink {

    private RoleDomainObject role;

    public ExternalToLocalRoleLinkModel(ExternalToLocalRoleLink from) {
        super(from);
    }

    public ExternalToLocalRoleLinkModel(ExternalRole externalRole, RoleDomainObject localRole) {
        this(externalRole.getProviderId(), externalRole.getId(), localRole);
    }

    public ExternalToLocalRoleLinkModel(String providerId, String externalRoleId, RoleDomainObject localRole) {
        this(null, providerId, externalRoleId, localRole);
    }

    public ExternalToLocalRoleLinkModel(Integer id, String providerId, String externalRoleId, RoleDomainObject localRole) {
        super(id, providerId, externalRoleId, localRole.getId().intValue());
        this.role = localRole;
    }

    public Integer getId() {
        return id;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getExternalRoleId() {
        return externalRoleId;
    }

    public Integer getLocalRoleId() {
        return localRoleId;
    }

    public RoleDomainObject getRole() {
        return role;
    }

}
