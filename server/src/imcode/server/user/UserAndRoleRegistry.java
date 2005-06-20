package imcode.server.user;

public interface UserAndRoleRegistry {

    UserDomainObject getUser( String loginName );

    String[] getRoleNames( UserDomainObject user );

    String[] getAllRoleNames();

}
