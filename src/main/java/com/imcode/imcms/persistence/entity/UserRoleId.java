package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserRoleId implements Serializable {

    private static final long serialVersionUID = -9073844111480922262L;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "role_id", nullable = false)
    private int roleId;

    public UserRoleId(int userId, int roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}
