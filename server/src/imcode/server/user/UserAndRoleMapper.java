package imcode.server.user;


public interface UserAndRoleMapper {
   UserDomainObject getUser( String loginName );

   String[] getRoleNames( UserDomainObject user );

   String[] getAllRoleNames();
}
