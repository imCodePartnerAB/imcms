package imcode.server.user;


public interface UserMapper {
   User getUser(String loginName) ;
   User getUser( int id );
   void update( String loginName, imcode.server.user.User user) ;

}
