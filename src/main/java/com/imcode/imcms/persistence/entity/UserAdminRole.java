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
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@Table(name = "useradmin_role_crossref")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserAdminRole implements Serializable {

    private static final long serialVersionUID = -4967498240170401817L;

    @EmbeddedId
    private UserRoleId id;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private RoleJPA role;

    public UserAdminRole(User user, RoleJPA role) {
        this.user = user;
        this.role = role;

        this.id = new UserRoleId(user.getId(), role.getId());
    }
}
