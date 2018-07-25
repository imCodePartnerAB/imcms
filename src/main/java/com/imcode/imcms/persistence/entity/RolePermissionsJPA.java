package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
import java.io.Serializable;

@Entity
@Table(name = "roles_permissions")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RolePermissionsJPA implements Serializable {

    private static final long serialVersionUID = 6163509570147239505L;

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
    private Integer roleId;

    @Column(
            name = "get_password_by_email",
            nullable = false,
            columnDefinition = "tinyint"
    )
    private boolean getPasswordByEmail;

    @Column(
            name = "access_to_admin_pages",
            nullable = false,
            columnDefinition = "tinyint"
    )
    private boolean accessToAdminPages;

    @Column(
            name = "use_images_in_image_archive",
            nullable = false,
            columnDefinition = "tinyint"
    )
    private boolean useImagesInImageArchive;

    @Column(
            name = "change_images_in_image_archive",
            nullable = false,
            columnDefinition = "tinyint"
    )
    private boolean changeImagesInImageArchive;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private RoleJPA role;

}
