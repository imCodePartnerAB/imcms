package imcode.server.user;

public class UserAndRoleRegistryException extends Exception {

    public UserAndRoleRegistryException( String message ) {
        super(message) ;
    }

    public UserAndRoleRegistryException( Throwable e ) {
        super(e) ;
    }
}
