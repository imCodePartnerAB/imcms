package imcode.server.user;

/**
 * @author kreiger
 */
public class RoleDomainObject {

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

}
