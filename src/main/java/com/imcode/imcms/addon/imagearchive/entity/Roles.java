package com.imcode.imcms.addon.imagearchive.entity;

import imcode.server.user.RoleDomainObject;
import imcode.server.user.RolePermissionDomainObject;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name="roles")
public class Roles implements Serializable {
    private static final long serialVersionUID = -9207677278215773638L;
    
    public static final RolePermissionDomainObject[] ALL_PERMISSIONS = {
        RoleDomainObject.USE_IMAGES_IN_ARCHIVE_PERMISSION, 
        RoleDomainObject.CHANGE_IMAGES_IN_ARCHIVE_PERMISSION
    };
    
    public static final int SUPERADMIN_ID = 0;
    public static final int USERADMIN_ID = 1;
    public static final int USERS_ID = 2;
    
    public static final int PERMISSION_USE_IMAGE = RoleDomainObject.USE_IMAGES_IN_ARCHIVE_PERMISSION.getId();
    public static final int PERMISSION_CHANGE_IMAGE = RoleDomainObject.CHANGE_IMAGES_IN_ARCHIVE_PERMISSION.getId();
    
    
    @Id
    @Column(name="role_id", nullable=false)
    @GeneratedValue
    private int id;
    
    @Column(name="role_name", length=60, nullable=false, unique=true)
    private String roleName;
    
    @Column(name="permissions", nullable=false)
    private int permissions;
    
    @Column(name="admin_role", nullable=false)
    private int adminRole;

    @OneToMany
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", insertable=false, updatable=false)
    private Set<CategoryRoles> categoryRoles;
    
    public Roles() {
    }

    public Roles(int id) {
        this.id = id;
    }
    
    
    public boolean isSuperadmin() {
        return id == SUPERADMIN_ID;
    }
    
    public boolean isCanUseImage() {
        return (permissions & PERMISSION_USE_IMAGE) == PERMISSION_USE_IMAGE;
    }
    
    public boolean isCanChangeImage() {
        return (permissions & PERMISSION_CHANGE_IMAGE) == PERMISSION_CHANGE_IMAGE;
    }
    
    public int getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(int adminRole) {
        this.adminRole = adminRole;
    }

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<CategoryRoles> getCategoryRoles() {
        return categoryRoles;
    }

    public void setCategoryRoles(Set<CategoryRoles> categoryRoles) {
        this.categoryRoles = categoryRoles;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final Roles other = (Roles) obj;
        if (this.id != other.id) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.id;
        
        return hash;
    }

    @Override
    public String toString() {
        return String.format("com.imcode.imcms.addon.imagearchive.entity.Roles[id: %d, roleName: %s, permissions: %d]", 
                id, roleName, permissions);
    }
}
