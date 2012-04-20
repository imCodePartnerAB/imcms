package com.imcode.imcms.addon.imagearchive.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="archive_library_roles")
@IdClass(LibraryRolesPK.class)
public class LibraryRoles implements Serializable {
    private static final long serialVersionUID = 5775826104758310402L;
    
    public static final int PERMISSION_USE = 0;
    public static final int PERMISSION_CHANGE = (1 << 1);
    
    
    @Id    
    @Column(name="library_id", nullable=false)
    private int libraryId;
    
    @ManyToOne
    @JoinColumn(name="library_id", referencedColumnName="id", insertable=false, updatable=false)
    private Libraries library;
    
    @Id
    @Column(name="role_id", nullable=false)
    private int roleId;
    
    @ManyToOne
    @JoinColumn(name="role_id", referencedColumnName="role_id", insertable=false, updatable=false)
    private Roles role;
    
    @Column(name="permissions", nullable=false)
    private int permissions;
    
    @Column(name="created_dt", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDt = new Date();
    
    @Column(name="updated_dt", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDt = new Date();

    @Column(name = "canUse", nullable = false)
    private Boolean canUse;

    @Column(name = "canChange", nullable = false)
    private Boolean canChange;
    
    
    public LibraryRoles() {
    }
    
    
    public Libraries getLibrary() {
        return library;
    }

    public void setLibrary(Libraries library) {
        this.library = library;
    }

    public int getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(int libraryId) {
        this.libraryId = libraryId;
    }

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
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

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public Date getUpdatedDt() {
        return updatedDt;
    }

    public void setUpdatedDt(Date updatedDt) {
        this.updatedDt = updatedDt;
    }

    /* 'use' implies see in the listByNamedParams(tree) on external files page and add to archive */
    public Boolean getCanUse() {
        return canUse;
    }

    public void setCanUse(Boolean canUse) {
        this.canUse = canUse;
    }

    /* Ability to change(upload, delete) also gives permission to use.
    * DEPRECATED: since users are only allowed to upload file to their own library, and other libraries are read-only,
    * this property is not used anymore. */
    public Boolean getCanChange() {
        return canChange;
    }

    public void setCanChange(Boolean canChange) {
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
        
        final LibraryRoles other = (LibraryRoles) obj;
        if (this.libraryId != other.libraryId) {
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
        hash = 59 * hash + this.libraryId;
        hash = 59 * hash + this.roleId;
        
        return hash;
    }

    @Override
    public String toString() {
        return String.format("com.imcode.imcms.addon.imagearchive.entity.LibraryRoles[libraryId: %d, roleId: %d]", 
                libraryId, roleId);
    }
}
