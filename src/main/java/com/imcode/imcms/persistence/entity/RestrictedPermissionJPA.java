package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Restricted permissions (by default 1 and 2) class.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 14.11.17.
 * @see com.imcode.imcms.persistence.entity.Meta.Permission#RESTRICTED_1
 * @see com.imcode.imcms.persistence.entity.Meta.Permission#RESTRICTED_2
 */
@Data
@Embeddable
@NoArgsConstructor
@Table(name = "imcms_doc_restricted_permissions")
public class RestrictedPermissionJPA {

    @Column(columnDefinition = "VARCHAR(16)")
    @Enumerated(EnumType.STRING)
    private Meta.Permission permission;

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

}
