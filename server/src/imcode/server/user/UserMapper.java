package imcode.server.user;

import imcode.server.User;

public interface UserMapper {
   User getUser(String loginName) ;
   void update( String loginName, User user) ;
}
