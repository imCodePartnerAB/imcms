package imcode.server.user;

public class MockRoleGetter implements RoleGetter {

    @Override
    public RoleDomainObject getRole(Integer roleId) {
        return new RoleDomainObject(roleId, "" + roleId, 0);
    }
}
