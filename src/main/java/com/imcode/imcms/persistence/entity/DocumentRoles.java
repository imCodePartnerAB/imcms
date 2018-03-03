package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "roles_rights")
public class DocumentRoles {

    @EmbeddedId
    private DocumentRoleId id;

    @ManyToOne
    @JoinColumn(name = "meta_id", insertable = false, updatable = false)
    private Meta document;

    @ManyToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private RoleJPA role;

    @Column(name = "permission", nullable = false)
    @Enumerated(EnumType.STRING)
    private Meta.Permission permission;

    public DocumentRoles(Meta document, RoleJPA role, Meta.Permission permission) {
        this.document = document;
        this.role = role;
        this.permission = permission;

        this.id = new DocumentRoleId(document.getId(), role.getId());
    }
}
