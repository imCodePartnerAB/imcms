package imcode.server.user;


public interface UserMapper {
   User getUser(String loginName) ;
   User getUser( int id );
   void updateUser( String loginName, User user) ;
   void addUser( User newUser );
}
