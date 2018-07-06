package com.imcode.imcms.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public final class UserRole extends Role {

    private static final long serialVersionUID = -6049860278931110812L;

    private final Integer id = 2; // fixme: this is a tradition, but it would be better to be 3 (and 2 for super-admin)

    private final String name = "Users";

    private final int permissions = 1;

    private final int adminRole = 0;

    UserRole() {
    }

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
