package imcode.server.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.imcode.imcms.model.Roles;

import java.io.Serializable;

/**
 * @deprecated use {@link Roles}
 */
@Deprecated
public class RoleId implements Serializable {

    public static final int SUPERADMIN_ID = 0;
    public static final int USERADMIN_ID = 1;
    public static final int USERS_ID = 2;

    public final static RoleId SUPERADMIN = new RoleId(SUPERADMIN_ID);
    public final static RoleId USERADMIN = new RoleId(USERADMIN_ID);
    public final static RoleId USERS = new RoleId(USERS_ID);

    @JsonInclude
    @JsonProperty("roleId")
    private final int roleId;

    public RoleId(@JsonProperty("roleId") int roleId) {
        this.roleId = roleId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final RoleId that = (RoleId) o;

        return roleId == that.roleId;

    }

    public int getRoleId() {
        return roleId;
    }

    public String getName() {
        switch (roleId) {
            case 0:
                return "Superadmin";
            case 1:
                return "Useradmin";
            case 2:
                return "Users";
        }
        return "";
    }

    public int hashCode() {
        return roleId;
    }

    public int intValue() {
        return roleId;
    }

    public String toString() {
        return "" + roleId;
    }

}