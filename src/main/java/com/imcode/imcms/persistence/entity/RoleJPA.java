package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Role;
import com.imcode.imcms.model.RolePermissions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RoleJPA extends Role {

    private static final long serialVersionUID = -6026648104017419915L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String name;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private RolePermissionsJPA permissions;

    @Column(name = "admin_role", nullable = false)
    private Integer adminRole = 0;

    public RoleJPA(String name) {
        this(null, name);
    }

    public RoleJPA(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoleJPA(Role from) {
        super(from);
    }

    @Override
    public void setPermissions(RolePermissions permissions) {
        if (permissions == null) {
            this.permissions = null;
            return;
        }

        this.permissions = new RolePermissionsJPA(permissions);
        this.permissions.setRoleId(getId());
        this.permissions.setRole(this);
    }
}
