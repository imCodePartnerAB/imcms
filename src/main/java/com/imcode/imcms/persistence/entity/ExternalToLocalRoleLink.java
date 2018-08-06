package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.domain.dto.ExternalRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
@Entity
@NoArgsConstructor
@Table(
        name = "external_to_local_roles_links",
        uniqueConstraints = {@UniqueConstraint(columnNames = {
                "provider_id", "external_role_id", "linked_local_role_id"
        })}
)
@ToString(exclude = "role")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ExternalToLocalRoleLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "external_role_id", nullable = false)
    private String externalRoleId;

    @Column(name = "linked_local_role_id", insertable = false, updatable = false, nullable = false)
    private Integer localRoleId;

    @OneToOne(fetch = FetchType.LAZY, optional = false, orphanRemoval = true)
    @JoinColumn(name = "linked_local_role_id", referencedColumnName = "role_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private RoleJPA role;

    public ExternalToLocalRoleLink(ExternalToLocalRoleLink from) {
        this.id = from.getId();
        this.providerId = from.getProviderId();
        this.externalRoleId = from.getExternalRoleId();
        this.role = from.getRole();
        this.localRoleId = from.getLocalRoleId();
    }

    public ExternalToLocalRoleLink(ExternalRole externalRole, RoleJPA localRole) {
        this(externalRole.getProviderId(), externalRole.getId(), localRole);
    }

    public ExternalToLocalRoleLink(String providerId, String externalRoleId, RoleJPA localRole) {
        this(null, providerId, externalRoleId, localRole);
    }

    public ExternalToLocalRoleLink(Integer id, String providerId, String externalRoleId, RoleJPA localRole) {
        this.id = id;
        this.providerId = providerId;
        this.externalRoleId = externalRoleId;
        this.role = localRole;
        this.localRoleId = localRole.getId();
    }
}
