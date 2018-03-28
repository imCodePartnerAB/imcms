package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Who knows what are the fields {@link #permissions} and {@link #adminRole}. If you don't know what are them,
 * so do not use. They're exist to be compatible with database, they're not null, so leave default value.
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class RoleJPA extends Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String name;

    @NotNull
    private Integer permissions = 0;

    @Column(name = "admin_role", nullable = false)
    private Integer adminRole = 1;

    public RoleJPA(Role from) {
        super(from);
    }
}
