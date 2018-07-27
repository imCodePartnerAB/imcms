package com.imcode.imcms.model;

import com.imcode.imcms.domain.dto.RolePermissionsDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public final class SuperAdminRole extends Role {

    private static final long serialVersionUID = 1675994734975791294L;

    private final Integer id = 0; // fixme: this is a tradition, but it would be better to start from 1 for MySQL ID

    private final String name = "Superadmin"; // fixme: tradition, would be better "Super Admin" or "super-admin"

    private RolePermissions permissions = new RolePermissionsDTO();

    private final int adminRole = 1;

    SuperAdminRole() {
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
