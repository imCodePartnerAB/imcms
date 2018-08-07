package com.imcode.imcms.model;

import com.imcode.imcms.domain.dto.RolePermissionsDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public final class UserRole extends Role {

    private static final long serialVersionUID = -6049860278931110812L;

    private final Integer id = 3;

    private final String name = "Users";

    private RolePermissions permissions = new RolePermissionsDTO();

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

    public void setAdminRole(int adminRole) {
        throwException();
    }

    private void throwException() {
        throw new UnsupportedOperationException("You can't change pre-defined role.");
    }
}
