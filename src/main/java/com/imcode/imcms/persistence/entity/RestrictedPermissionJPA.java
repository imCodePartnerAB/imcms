package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.RestrictedPermission;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * Restricted permissions (by default 1 and 2) class.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 14.11.17.
 * @see Permission#RESTRICTED_1
 * @see Permission#RESTRICTED_2
 */
@Data
@Embeddable
@NoArgsConstructor
@Table(name = "imcms_doc_restricted_permissions")
@EqualsAndHashCode(callSuper=false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RestrictedPermissionJPA extends RestrictedPermission {

    private static final long serialVersionUID = -4360158661094922508L;

    @Column(columnDefinition = "VARCHAR(16)")
    @Enumerated(EnumType.STRING)
    private Permission permission;

    @Column(name = "edit_text")
    private boolean editText;

    @Column(name = "edit_menu")
    private boolean editMenu;

    @Column(name = "edit_image")
    private boolean editImage;

    @Column(name = "edit_loop")
    private boolean editLoop;

    @Column(name = "edit_doc_info")
    private boolean editDocInfo;

    public RestrictedPermissionJPA(RestrictedPermission from) {
        super(from);
    }
}
