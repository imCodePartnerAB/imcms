package imcode.server.user;

import com.imcode.imcms.api.RoleConstants;

/**
 * @author kreiger
 */
public class RoleDomainObject {

    public final static RoleDomainObject SUPERADMIN = new RoleDomainObject(0, RoleConstants.SUPER_ADMIN) ;

    private int id;
    private String name;

    public RoleDomainObject( int roleId, String roleName ) {
        this.id = roleId;
        this.name = roleName;
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

        if ( id != roleDomainObject.id ) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return id;
    }

}
