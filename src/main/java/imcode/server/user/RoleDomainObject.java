package imcode.server.user;

import com.imcode.imcms.util.l10n.LocalizedMessage;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class RoleDomainObject implements Serializable, Comparable<RoleDomainObject>, Cloneable {

    public final static RolePermissionDomainObject PASSWORD_MAIL_PERMISSION = new RolePermissionDomainObject(1, new LocalizedMessage("role_permission/password_by_email/description"));
    public static final RolePermissionDomainObject ADMIN_PAGES_PERMISSION = new RolePermissionDomainObject(4, new LocalizedMessage("role_permission/admin_pages_access/desciption"));
    public static final RolePermissionDomainObject USE_IMAGES_IN_ARCHIVE_PERMISSION = new RolePermissionDomainObject(1 << 22, new LocalizedMessage("role_permission/use_images_from_image_archive/description"));
    public static final RolePermissionDomainObject CHANGE_IMAGES_IN_ARCHIVE_PERMISSION = new RolePermissionDomainObject(1 << 23, new LocalizedMessage("role_permission/change_images_in_image_archive/description"));


    private final static RolePermissionDomainObject[] ALL_ROLE_PERMISSIONS = new RolePermissionDomainObject[]{
            PASSWORD_MAIL_PERMISSION,
            ADMIN_PAGES_PERMISSION,
            USE_IMAGES_IN_ARCHIVE_PERMISSION,
            CHANGE_IMAGES_IN_ARCHIVE_PERMISSION,
    };

    private Integer id;
    private String name;
    private int adminRoleId;
    private Set<RolePermissionDomainObject> permissions = new HashSet<>();


    public RoleDomainObject(String name) {
        this(0, name, 0);
    }

    public RoleDomainObject(Integer roleId, String roleName, int adminRoleId) {
        this.id = roleId;
        this.name = roleName;
        this.adminRoleId = adminRoleId;
    }

    public static RolePermissionDomainObject[] getAllRolePermissions() {
        return ALL_ROLE_PERMISSIONS;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RoleDomainObject)) {
            return false;
        }

        final RoleDomainObject roleDomainObject = (RoleDomainObject) o;

        if (isAdminRole() || roleDomainObject.isAdminRole()) {
            return adminRoleId == roleDomainObject.adminRoleId;
        }

        return id.equals(roleDomainObject.id);
    }

    public int hashCode() {
        if (isAdminRole()) {
            return -adminRoleId;
        }

        return id;
    }

    public String toString() {
        return "(role " + id + " \"" + name + "\" " + adminRoleId + ")";
    }

    public boolean isAdminRole() {
        return 0 != adminRoleId;
    }

    public int compareTo(RoleDomainObject roleDomainObject) {
        return name.compareToIgnoreCase(roleDomainObject.name);
    }

    public void addPermission(RolePermissionDomainObject permission) {
        permissions.add(permission);
    }

    public boolean hasPermission(RolePermissionDomainObject permission) {
        return permissions.contains(permission);
    }

    public void removePermission(RolePermissionDomainObject permission) {
        permissions.remove(permission);
    }

    public void removeAllPermissions() {
        permissions.clear();
    }

    public RolePermissionDomainObject[] getPermissions() {
        return permissions.toArray(new RolePermissionDomainObject[0]);
    }

    public void addUnionOfPermissionIdsToRole(int unionOfRolePermissionIds) {
        for (int i = 0; i < RoleDomainObject.ALL_ROLE_PERMISSIONS.length; i++) {
            RolePermissionDomainObject permission = RoleDomainObject.ALL_ROLE_PERMISSIONS[i];
            if (bitIsSet(unionOfRolePermissionIds, permission.getId())) {
                addPermission(permission);
            }
        }
    }

    private boolean bitIsSet(int unionOfRolePermissionIds, int bitValue) {
        return 0 != (unionOfRolePermissionIds & bitValue);
    }

    @Override
    public RoleDomainObject clone() throws CloneNotSupportedException {
        RoleDomainObject clone = (RoleDomainObject) super.clone();
        clone.id = id;
        clone.permissions = new HashSet<>(permissions);

        return clone;
    }
}
