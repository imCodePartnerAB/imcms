package com.imcode.imcms.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public abstract class RolePermissions implements Serializable {

    private static final long serialVersionUID = -556370529075451866L;

    protected Integer roleId;

    protected boolean getPasswordByEmail;
    protected boolean accessToAdminPages;
    protected boolean useImagesInImageArchive;
    protected boolean changeImagesInImageArchive;
    protected boolean accessToDocumentEditor;

    protected RolePermissions(RolePermissions from) {
        setRoleId(from.getRoleId());
        setAccessToAdminPages(from.isAccessToAdminPages());
        setGetPasswordByEmail(from.isGetPasswordByEmail());
        setUseImagesInImageArchive(from.isUseImagesInImageArchive());
        setChangeImagesInImageArchive(from.isChangeImagesInImageArchive());
        setAccessToDocumentEditor(from.isAccessToDocumentEditor());
    }
}
