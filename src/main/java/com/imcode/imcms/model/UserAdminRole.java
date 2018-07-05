package com.imcode.imcms.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public final class UserAdminRole extends Role {

    private static final long serialVersionUID = -2770811256467291410L;

    private final Integer id = 1; // fixme: this is a tradition, but it would be better to be 2 (and 1 for super-admin)

    private final String name = "Useradmin"; // fixme: tradition, would be better to be "User Admin" or "user-admin"

    private final int permissions = 0;

    private final int adminRole = 2;

    @Override
    public void setId(Integer id) {
        throwException();
    }

    @Override
    public void setName(String name) {
        throwException();
    }

    public void setPermissions(int permissions) {
        throwException();
    }

    public void setAdminRole(int adminRole) {
        throwException();
    }

    private void throwException() {
        throw new UnsupportedOperationException("You can't change pre-defined role.");
    }
}
