package imcode.server.user;


public interface UserMapper {
   User getUser(String loginName) ;
   User getUser( int id );
   void updateUser( String loginName, imcode.server.user.User user) ;
   void addUser( User newUser );
}
