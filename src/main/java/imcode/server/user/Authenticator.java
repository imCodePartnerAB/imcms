package imcode.server.user;

public interface Authenticator {
   boolean authenticate( String loginName, String password );
}
