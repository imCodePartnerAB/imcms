package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@Table(name = "roles_rights")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DocumentRole {

    @EmbeddedId
    private DocumentRoleId id;

    @ManyToOne
    @JoinColumn(name = "meta_id", insertable = false, updatable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Meta document;

    @ManyToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private RoleJPA role;

    @Column(name = "permission", nullable = false)
    @Enumerated(EnumType.STRING)
    private Meta.Permission permission;

    public DocumentRole(Meta document, RoleJPA role, Meta.Permission permission) {
        this.document = document;
        this.role = role;
        this.permission = permission;

        this.id = new DocumentRoleId(document.getId(), role.getId());
    }
}
