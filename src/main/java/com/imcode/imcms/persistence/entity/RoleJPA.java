package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Who knows what are the fields {@link #permissions} and {@link #adminRole}. If you don't know what are them,
 * so do not use. They exist to be compatible with database, they're not null, so leave default value.
 */
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

    @NotNull
    private Integer permissions = 0;

    @Column(name = "admin_role", nullable = false)
    private Integer adminRole = 0;

    public RoleJPA(Role from) {
        super(from);
    }
}
