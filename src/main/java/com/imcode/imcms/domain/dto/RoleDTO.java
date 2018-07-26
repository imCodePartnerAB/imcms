package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Role;
import com.imcode.imcms.model.RolePermissions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class RoleDTO extends Role {

    private static final long serialVersionUID = -6429901776462985054L;

    private Integer id;
    private String name;
    private RolePermissionsDTO permissions;

    public RoleDTO(String name) {
        this.name = name;
    }

    public RoleDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoleDTO(Role from) {
        super(from);
    }

    @Override
    public void setPermissions(RolePermissions permissions) {
        if (permissions == null) {
            this.permissions = null;
            return;
        }

        this.permissions = new RolePermissionsDTO(permissions);
        this.permissions.setRoleId(getId());
    }
}
