package imcode.server.user;

import com.imcode.imcms.api.RoleConstants;

import java.io.Serializable;

public class RoleDomainObject implements Serializable, Comparable {

    public final static RoleDomainObject SUPERADMIN = new ImmutableRoleDomainObject( 0, RoleConstants.SUPER_ADMIN, 1 );
    public final static RoleDomainObject USERADMIN = new ImmutableRoleDomainObject( 1, RoleConstants.USER_ADMIN, 2 );
    public final static RoleDomainObject USERS = new ImmutableRoleDomainObject( 2, RoleConstants.USERS, 0 );

    private int id;
    private String name;
    private int adminRoleId;

    public RoleDomainObject( int roleId, String roleName, int adminRoleId ) {
        this.id = roleId;
        this.name = roleName;
        this.adminRoleId = adminRoleId;
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
}
