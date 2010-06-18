package com.imcode.imcms.addon.imagearchive.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LibraryRolesPK implements Serializable {
    private static final long serialVersionUID = -291154922220606569L;
    
    @Column(name="library_id", nullable=false)
    private int libraryId;
    
    @Column(name="role_id", nullable=false)
    private int roleId;

    
    public LibraryRolesPK() {
    }

    public LibraryRolesPK(int libraryId, int roleId) {
        this.libraryId = libraryId;
        this.roleId = roleId;
    }

    
    public int getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(int libraryId) {
        this.libraryId = libraryId;
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
        
        final LibraryRolesPK other = (LibraryRolesPK) obj;
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
        hash = 79 * hash + this.libraryId;
        hash = 79 * hash + this.roleId;
        
        return hash;
    }

    @Override
    public String toString() {
        return String.format("com.imcode.imcms.addon.imagearchive.entity.LibraryRolesPK[libraryId: %d, roleId: %d]", 
                libraryId, roleId);
    }
}
