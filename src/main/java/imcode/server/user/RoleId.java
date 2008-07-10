package imcode.server.user;

import java.io.Serializable;

public class RoleId implements Serializable {

    public static final int SUPERADMIN_ID = 0 ;
    public static final int USERADMIN_ID = 1 ;
    public static final int USERS_ID = 2 ;

    public final static RoleId SUPERADMIN = new RoleId(SUPERADMIN_ID);
    public final static RoleId USERADMIN = new RoleId(USERADMIN_ID);
    public final static RoleId USERS = new RoleId(USERS_ID);

    private final int roleId ;

    public RoleId(int roleId) {
        this.roleId = roleId;
    }

    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        final RoleId that = (RoleId) o;

        return roleId == that.roleId;

    }

    public int hashCode() {
        return roleId;
    }

    public int intValue() {
        return roleId;
    }

    public String toString() {
        return ""+roleId;
    }

}