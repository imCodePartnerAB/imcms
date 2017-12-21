package com.imcode.imcms.model;

import com.imcode.imcms.persistence.entity.Meta.Permission;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class RestrictedPermission {

    protected RestrictedPermission(RestrictedPermission from) {
        setPermission(from.getPermission());
        setEditText(from.isEditText());
        setEditMenu(from.isEditMenu());
        setEditImage(from.isEditImage());
        setEditLoop(from.isEditLoop());
        setEditDocInfo(from.isEditDocInfo());
    }

    public abstract Permission getPermission();

    public abstract void setPermission(Permission permission);

    public abstract boolean isEditText();

    public abstract void setEditText(boolean editText);

    public abstract boolean isEditMenu();

    public abstract void setEditMenu(boolean editMenu);

    public abstract boolean isEditImage();

    public abstract void setEditImage(boolean editImage);

    public abstract boolean isEditLoop();

    public abstract void setEditLoop(boolean editLoop);

    public abstract boolean isEditDocInfo();

    public abstract void setEditDocInfo(boolean editDocInfo);
}
