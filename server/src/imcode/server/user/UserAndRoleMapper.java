package imcode.server.user;


public interface UserAndRoleMapper {
   User getUser( String loginName );

   String[] getRoleNames( User user );

   String[] getAllRoleNames();
}
