package com.imcode.imcms.domain.dto;

import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.RestrictedPermissionJPA;

import java.io.Serializable;

/**
 * Permission set type.
 * <p/>
 * Permission set is assigned per role per document.
 *
 * @see imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings
 * <p/>
 * Permission set with lower type id (EDIT) is most privileged.
 * Any new permission defined in the system is automatically included into that set.
 * <p/>
 * Permission set with higher type id (NONE) has no privileges at all.
 * This set is always empty.
 * <p/>
 * VIEW permission set defines permissions only for document viewing.
 * <p/>
 * EDIT, VIEW and NONE sets are sealed - i.e each of them contains predefined and unmodifiable permissions.
 * Those sets are shared by all documents in a system.
 * <p/>
 * RESTRICTED_1 and RESTRICTED_2 are sets customizable per document,
 * however, they also contain the fixed subset of permissions - VIEW.
 * Additionally any document may extend a restricted set of permissions with permissions from the EDIT set.
 * <p/>
 * Please note:
 * By definition RESTRICTED_2 is more restrictive than RESTRICTED_1 but this can be changed at a document level (why?).
 */
public enum PermissionDTO implements Serializable {

    EDIT(Permission.EDIT),
    RESTRICTED_1(Permission.RESTRICTED_1),
    RESTRICTED_2(Permission.RESTRICTED_2),
    VIEW(Permission.VIEW),
    NONE(Permission.NONE);

    private final Permission permission;

    PermissionDTO(Permission permission) {
        this.permission = permission;
    }

    public static PermissionDTO fromPermission(Permission permission) {
        switch (permission) {
            case EDIT:
                return EDIT;
            case RESTRICTED_1:
                return RESTRICTED_1;
            case RESTRICTED_2:
                return RESTRICTED_2;
            case VIEW:
                return VIEW;
            default:
                return NONE;
        }
    }

    public static PermissionDTO fromRestrictedPermission(RestrictedPermissionJPA restrictedPermission) {
        return fromPermission(restrictedPermission.getPermission());
    }

    public Permission getPermission() {
        return permission;
    }

    public int getId() {
        return permission.getId();
    }

    public String toString() {
        return permission.toString();
    }

    public String getName() {
        return super.toString().toLowerCase();
    }

    public boolean isMorePrivilegedThan(PermissionDTO type) {
        return this.permission.isMorePrivilegedThan(type.permission);
    }

    public boolean isAtLeastAsPrivilegedAs(PermissionDTO type) {
        return this.permission.isAtLeastAsPrivilegedAs(type.permission);
    }
}
