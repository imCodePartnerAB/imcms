package com.imcode.imcms.model;

import com.imcode.imcms.domain.dto.ExternalRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExternalToLocalRoleLink {

    protected Integer id;
    protected String providerId;
    protected String externalRoleId;
    protected Integer localRoleId;

    public ExternalToLocalRoleLink(ExternalToLocalRoleLink from) {
        setId(from.getId());
        setProviderId(from.getProviderId());
        setExternalRoleId(from.getExternalRoleId());
        setLocalRoleId(from.getLocalRoleId());
    }

    public ExternalToLocalRoleLink(ExternalRole externalRole, Integer localRoleId) {
        this(externalRole.getProviderId(), externalRole.getId(), localRoleId);
    }

    public ExternalToLocalRoleLink(String providerId, String externalRoleId, Integer localRoleId) {
        this(null, providerId, externalRoleId, localRoleId);
    }

    public ExternalToLocalRoleLink(Integer id, String providerId, String externalRoleId, Integer localRoleId) {
        this.id = id;
        this.providerId = providerId;
        this.externalRoleId = externalRoleId;
        this.localRoleId = localRoleId;
    }
}
