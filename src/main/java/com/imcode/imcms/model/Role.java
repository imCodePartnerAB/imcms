package com.imcode.imcms.model;

import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
public abstract class Role implements Serializable {

    private static final long serialVersionUID = -9182955164406114663L;

    protected Role(Role from) {
        setId(from.getId());
        setName(from.getName());
        setPermissions(from.getPermissions());
    }

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract RolePermissions getPermissions();

    public abstract void setPermissions(RolePermissions permissions);

}
