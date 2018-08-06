package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.model.ExternalToLocalRoleLink;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "role", callSuper = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ExternalToLocalRoleLinkJPA extends ExternalToLocalRoleLink {

    private RoleJPA role;

    public ExternalToLocalRoleLinkJPA(ExternalToLocalRoleLink from) {
        super(from);
    }

    public ExternalToLocalRoleLinkJPA(ExternalRole externalRole, RoleJPA localRole) {
        this(externalRole.getProviderId(), externalRole.getId(), localRole);
    }

    public ExternalToLocalRoleLinkJPA(String providerId, String externalRoleId, RoleJPA localRole) {
        this(null, providerId, externalRoleId, localRole);
    }

    public ExternalToLocalRoleLinkJPA(Integer id, String providerId, String externalRoleId, RoleJPA localRole) {
        super(id, providerId, externalRoleId, localRole.getId());
        this.role = localRole;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    @Column(name = "provider_id", nullable = false)
    public String getProviderId() {
        return providerId;
    }

    @Column(name = "external_role_id", nullable = false)
    public String getExternalRoleId() {
        return externalRoleId;
    }

    @Column(name = "linked_local_role_id", insertable = false, updatable = false, nullable = false)
    public Integer getLocalRoleId() {
        return localRoleId;
    }

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "linked_local_role_id", referencedColumnName = "role_id")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public RoleJPA getRole() {
        return role;
    }
}
