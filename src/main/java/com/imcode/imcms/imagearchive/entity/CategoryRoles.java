package com.imcode.imcms.imagearchive.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "archive_category_roles")
@NamedQueries({
        @NamedQuery(name = "availableImageCategories",
                query = "SELECT c.id AS id, c.name AS name " +
                        "FROM CategoryRoles cr INNER JOIN cr.category c " +
                        "WHERE cr.roleId IN (:roleIds) AND cr.canChange = 1 AND NOT EXISTS " +
                        "(FROM ImageCategories ic " +
                        "WHERE ic.imageId = :imageId AND ic.categoryId = cr.categoryId) " +
                        "AND c.type.name = 'Images'")
})
@IdClass(CategoryRolesPK.class)
public class CategoryRoles implements Serializable {
    private static final long serialVersionUID = 3157190579405342495L;

    @Id
    @Column(name = "category_id", nullable = false)
    private int categoryId;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", insertable = false, updatable = false)
    private Categories category;

    @Id
    @Column(name = "role_id", nullable = false)
    private int roleId;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", insertable = false, updatable = false)
    private Roles role;

    @Column(name = "created_dt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDt = new Date();

    @Column(name = "canUse", nullable = false)
    private Boolean canUse;

    @Column(name = "canChange", nullable = false)
    private Boolean canChange;

    public CategoryRoles() {
    }

    public CategoryRoles(int categoryId, int roleId, boolean canUse, boolean canChange) {
        this.categoryId = categoryId;
        this.roleId = roleId;
        this.canUse = canUse;
        this.canChange = canChange;
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

    public Boolean getCanUse() {
        return canUse;
    }

    public void setCanUse(Boolean canUse) {
        this.canUse = canUse;
    }

    public Boolean getCanChange() {
        return canChange;
    }

    public void setcanChange(Boolean canChange) {
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
        return String.format("CategoryRoles[categoryId: %d, roleId: %d]",
                categoryId, roleId);
    }
}
