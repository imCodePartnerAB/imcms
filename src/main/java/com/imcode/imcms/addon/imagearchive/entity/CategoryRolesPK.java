package com.imcode.imcms.addon.imagearchive.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CategoryRolesPK implements Serializable {
    private static final long serialVersionUID = -4975712786681516776L;
    
    @Column(name="category_id", nullable=false)
    private int categoryId;
    
    @Column(name="role_id", nullable=false)
    private int roleId;
    
    
    public CategoryRolesPK() {
    }

    public CategoryRolesPK(int categoryId, int roleId) {
        this.categoryId = categoryId;
        this.roleId = roleId;
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
        
        final CategoryRolesPK other = (CategoryRolesPK) obj;
        if (this.categoryId != other.categoryId) {
            return false;
        }
        
        if (this.roleId != other.roleId) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + this.categoryId;
        hash = 83 * hash + this.roleId;
        
        return hash;
    }

    @Override
    public String toString() {
        return String.format("com.imcode.imcms.addon.imagearchive.entity.CategoryRolesPK[categoryId: %d, roleId: %d]", 
                categoryId, roleId);
    }
}
