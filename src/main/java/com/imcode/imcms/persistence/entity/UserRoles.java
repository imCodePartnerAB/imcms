package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@Table(name = "user_roles_crossref")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserRoles {

    @EmbeddedId
    private UserRoleId id;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private RoleJPA role;

    public UserRoles(User user, RoleJPA role) {
        this.user = user;
        this.role = role;

        this.id = new UserRoleId(user.getId(), role.getId());
    }
}
