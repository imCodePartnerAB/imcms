package com.imcode.imcms.addon.imagearchive.dto;

import java.io.Serializable;

public class RoleCategoriesDto implements Serializable {
    private static final long serialVersionUID = -5793624922654319529L;

    private int roleId;
    private int categoryId;
    private boolean canUse;
    private boolean canChange;


    public RoleCategoriesDto() {
    }

    public boolean isCanChange() {
        return canChange;
    }

    public void setCanChange(boolean canChange) {
        this.canChange = canChange;
    }

    public boolean isCanUse() {
        return canUse;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final RoleCategoriesDto other = (RoleCategoriesDto) obj;
        if (this.roleId != other.roleId && this.categoryId != other.categoryId
                && this.canUse != other.canUse && this.canChange != other.canChange) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.roleId + this.categoryId;

        return hash;
    }
}
