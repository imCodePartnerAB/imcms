package imcode.server.user;


public interface UserMapper {
   User getUser( String loginName );

   String[] getRoleNames( User user );

   String[] getAllRoleNames();
}
