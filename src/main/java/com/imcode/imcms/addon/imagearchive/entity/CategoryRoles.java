package com.imcode.imcms.addon.imagearchive.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="category_roles")
@IdClass(CategoryRolesPK.class)
public class CategoryRoles implements Serializable {
    private static final long serialVersionUID = 3157190579405342495L;
    
    @Id
    @Column(name="category_id", nullable=false)
    private int categoryId;
    
    @ManyToOne
    @JoinColumn(name="category_id", referencedColumnName="category_id", insertable=false, updatable=false)
    private Categories category;
    
    @Id
    @Column(name="role_id", nullable=false)
    private int roleId;
    
    @ManyToOne
    @JoinColumn(name="role_id", referencedColumnName="role_id", insertable=false, updatable=false)
    private Roles role;
    
    @Column(name="created_dt", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDt = new Date();
    
    
    public CategoryRoles() {
    }

    public CategoryRoles(int categoryId, int roleId) {
        this.categoryId = categoryId;
        this.roleId = roleId;
    }

    
    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
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
        
        final CategoryRoles other = (CategoryRoles) obj;
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
        int hash = 7;
        hash = 43 * hash + this.categoryId;
        hash = 43 * hash + this.roleId;
        
        return hash;
    }

    @Override
    public String toString() {
        return String.format("com.imcode.imcms.addon.imagearchive.entity.CategoryRoles[categoryId: %d, roleId: %d]", 
                categoryId, roleId);
    }
}
