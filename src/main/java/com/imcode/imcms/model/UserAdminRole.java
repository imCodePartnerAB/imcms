package com.imcode.imcms.model;

import com.imcode.imcms.domain.dto.RolePermissionsDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public final class UserAdminRole extends Role {

    private static final long serialVersionUID = -2770811256467291410L;

    private final Integer id = 2;

    private final String name = "Useradmin"; // fixme: tradition, would be better to be "User Admin" or "user-admin"

    private RolePermissions permissions = new RolePermissionsDTO();

    private final int adminRole = 2;

    UserAdminRole() {
    }

    @Override
    public void setId(Integer id) {
        throwException();
    }

    @Override
    public void setName(String name) {
        throwException();
    }

    public void setAdminRole(int adminRole) {
        throwException();
    }

    private void throwException() {
        throw new UnsupportedOperationException("You can't change pre-defined role.");
    }
}
