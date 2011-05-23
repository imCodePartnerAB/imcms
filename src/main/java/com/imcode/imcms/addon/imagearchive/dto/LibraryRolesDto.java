package com.imcode.imcms.addon.imagearchive.dto;

import com.imcode.imcms.addon.imagearchive.entity.LibraryRoles;
import java.io.Serializable;

public class LibraryRolesDto implements Serializable {
    private static final long serialVersionUID = -5763624822654311529L;
    
    private int roleId;
    private int permissions;
    private String roleName;
    private boolean canUse;
    private boolean canChange;

    
    public LibraryRolesDto() {
    }

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

        public boolean isCanUse() {
        return canUse;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public boolean isCanChange() {
        return canChange;
    }

    public void setCanChange(boolean canChange) {
        this.canChange = canChange;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final LibraryRolesDto other = (LibraryRolesDto) obj;
        if (this.roleId != other.roleId) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.roleId;
        
        return hash;
    }
}
