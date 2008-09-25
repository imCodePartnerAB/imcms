package imcode.server.user;

public class MockRoleGetter implements RoleGetter {

    public RoleDomainObject getRole(RoleId roleId) {
        return new RoleDomainObject(roleId, ""+roleId, 0);
    }
}
