package imcode.server.user;

import com.imcode.imcms.api.RoleConstants;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class RoleDomainObject implements Serializable, Comparable {

    public final static RoleDomainObject SUPERADMIN = new ImmutableRoleDomainObject( 0, RoleConstants.SUPER_ADMIN, 1 );
    public final static RoleDomainObject USERADMIN = new ImmutableRoleDomainObject( 1, RoleConstants.USER_ADMIN, 2 );
    public final static RoleDomainObject USERS = new ImmutableRoleDomainObject( 2, RoleConstants.USERS, 0 );

    public final static RolePermissionDomainObject PASSWORD_MAIL_PERMISSION = new RolePermissionDomainObject( 1 ) ;
    public final static RolePermissionDomainObject CONFERENCE_REGISTRATION_PERMISSION = new RolePermissionDomainObject( 2 ) ;

    final static RoleDomainObject.RolePermissionDomainObject[] ALL_ROLE_PERMISSIONS = new RoleDomainObject.RolePermissionDomainObject[]{
        PASSWORD_MAIL_PERMISSION,
        CONFERENCE_REGISTRATION_PERMISSION,
    };

    private int id;
    private String name;
    private int adminRoleId;
    private Set permissions = new HashSet() ;

    public static final int SUPERADMIN_ID = SUPERADMIN.getId() ;
    public static final int USERADMIN_ID = USERADMIN.getId() ;
    public static final int USERS_ID = USERS.getId() ;

    public RoleDomainObject( int roleId, String roleName, int adminRoleId ) {
        this.id = roleId;
        this.name = roleName;
        this.adminRoleId = adminRoleId;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public int getId() {
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

        return id == roleDomainObject.id;
    }

    public int hashCode() {
        if ( isAdminRole() ) {
            return -adminRoleId;
        }

        return id;
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

    public void addPermission(RoleDomainObject.RolePermissionDomainObject permission) {
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

    public static class RolePermissionDomainObject {
        private int id ;

        RolePermissionDomainObject( int id ) {
            this.id = id;
        }

        int getId() {
            return id;
        }

        public int hashCode() {
            return id ;
        }

        public boolean equals( Object obj ) {
            return obj instanceof RolePermissionDomainObject && ((RolePermissionDomainObject)obj).id == id ;
        }

    }
}
