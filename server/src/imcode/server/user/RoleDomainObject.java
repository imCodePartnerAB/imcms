package imcode.server.user;

import com.imcode.imcms.util.l10n.LocalizedMessage;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class RoleDomainObject implements Serializable, Comparable {

    public final static RolePermissionDomainObject PASSWORD_MAIL_PERMISSION = new RolePermissionDomainObject( 1, new LocalizedMessage( "role_permission/password_by_email/description" ) ) ;
    public static final RolePermissionDomainObject ADMIN_PAGES_PERMISSION = new RolePermissionDomainObject( 4, new LocalizedMessage( "role_permission/admin_pages_access/desciption" ) );

    private final static RolePermissionDomainObject[] ALL_ROLE_PERMISSIONS = new RolePermissionDomainObject[]{
        PASSWORD_MAIL_PERMISSION,
        ADMIN_PAGES_PERMISSION,
    };

    private RoleId id;
    private String name;
    private int adminRoleId;
    private Set permissions = new HashSet() ;


    public RoleDomainObject( String name ) {
        this(new RoleId(0), name,0) ;
    }

    public RoleDomainObject( RoleId roleId, String roleName, int adminRoleId ) {
        this.id = roleId;
        this.name = roleName;
        this.adminRoleId = adminRoleId;
    }

    public void setId( RoleId id ) {
        this.id = id;
    }

    public RoleId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name ;
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof RoleDomainObject ) ) {
            return false;
        }

        final RoleDomainObject roleDomainObject = (RoleDomainObject)o;

        if ( isAdminRole() || roleDomainObject.isAdminRole() ) {
            return adminRoleId == roleDomainObject.adminRoleId;
        }

        return id.equals(roleDomainObject.id);
    }

    public int hashCode() {
        if ( isAdminRole() ) {
            return -adminRoleId;
        }

        return id.intValue();
    }

    public String toString() {
        return "(role " + id + " \"" + name + "\" " + adminRoleId + ")";
    }

    public boolean isAdminRole() {
        return 0 != adminRoleId ;
    }

    public int compareTo( Object o ) {
        return name.compareToIgnoreCase(((RoleDomainObject)o).name) ;
    }

    public void addPermission(RolePermissionDomainObject permission) {
        permissions.add( permission ) ;
    }

    public boolean hasPermission(RolePermissionDomainObject permission) {
        return permissions.contains( permission ) ;
    }

    public void removePermission(RolePermissionDomainObject permission) {
        permissions.remove( permission ) ;
    }

    public RolePermissionDomainObject[] getPermissions() {
        return (RolePermissionDomainObject[])permissions.toArray( new RolePermissionDomainObject[permissions.size()] );
    }

    public void addUnionOfPermissionIdsToRole( int unionOfRolePermissionIds ) {
        for ( int i = 0; i < RoleDomainObject.ALL_ROLE_PERMISSIONS.length; i++ ) {
            RolePermissionDomainObject permission = RoleDomainObject.ALL_ROLE_PERMISSIONS[i];
            if ( bitIsSet( unionOfRolePermissionIds, permission.getId() ) ) {
                addPermission( permission );
            }
        }
    }

    private boolean bitIsSet( int unionOfRolePermissionIds, int bitValue ) {
        return 0 != ( unionOfRolePermissionIds & bitValue );
    }

    public static RolePermissionDomainObject[] getAllRolePermissions() {
        return ALL_ROLE_PERMISSIONS;
    }

}
