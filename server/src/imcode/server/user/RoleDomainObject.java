package imcode.server.user;

import com.imcode.imcms.api.RoleConstants;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * @author kreiger
 */
public class RoleDomainObject implements Serializable {

    public final static RoleDomainObject SUPERADMIN = new RoleDomainObject( 0, RoleConstants.SUPER_ADMIN, 1 );
    public final static RoleDomainObject USERADMIN = new RoleDomainObject( 0, RoleConstants.USER_ADMIN, 2 );
    public static final RoleDomainObject USERS = new RoleDomainObject( 2, RoleConstants.USERS, 0 );

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

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof RoleDomainObject ) ) {
            return false;
        }

        final RoleDomainObject roleDomainObject = (RoleDomainObject)o;

        if ( 0 != adminRoleId || 0 != roleDomainObject.adminRoleId ) {
            return adminRoleId == roleDomainObject.adminRoleId;
        }

        return id == roleDomainObject.id;
    }

    public int hashCode() {
        if ( 0 != adminRoleId ) {
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
}
