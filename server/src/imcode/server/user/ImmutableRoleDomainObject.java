package imcode.server.user;

public class ImmutableRoleDomainObject extends RoleDomainObject {

    public ImmutableRoleDomainObject( int roleId, String roleName, int adminRoleId ) {
        super(roleId, roleName, adminRoleId);
    }

    public void setId( int id ) {
        return ;
    }

    public void setName( String name ) {
        return ;
    }
}
