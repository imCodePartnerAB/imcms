package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.RolePermissions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "roles_permissions")
@Data
@NoArgsConstructor
@ToString(exclude = "role", callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RolePermissionsJPA extends RolePermissions {

    private static final long serialVersionUID = 6163509570147239505L;

    private RoleJPA role;

    public RolePermissionsJPA(RolePermissions from) {
        super(from);
    }

    @GenericGenerator(
            name = "generator",
            strategy = "foreign",
            parameters = @Parameter(name = "property", value = "role")
    )
    @Id
    @GeneratedValue(generator = "generator")
    @Column(
            name = "role_id",
            unique = true,
            nullable = false
    )
    @Override
    public Integer getRoleId() {
        return roleId;
    }

    @Column(
            name = "get_password_by_email",
            nullable = false,
            columnDefinition = "tinyint"
    )
    public boolean isGetPasswordByEmail() {
        return getPasswordByEmail;
    }

    @Column(
            name = "access_to_admin_pages",
            nullable = false,
            columnDefinition = "tinyint"
    )
    public boolean isAccessToAdminPages() {
        return accessToAdminPages;
    }

    @Column(
            name = "use_images_in_image_archive",
            nullable = false,
            columnDefinition = "tinyint"
    )
    public boolean isUseImagesInImageArchive() {
        return useImagesInImageArchive;
    }

    @Column(
            name = "change_images_in_image_archive",
            nullable = false,
            columnDefinition = "tinyint"
    )
    public boolean isChangeImagesInImageArchive() {
        return changeImagesInImageArchive;
    }

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn
    public RoleJPA getRole() {
        return role;
    }
}
