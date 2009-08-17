package imcode.server.user;

public class UserAlreadyExistsException extends UserAndRoleRegistryException {

    public UserAlreadyExistsException( Throwable e ) {
        super(e);
    }

    public UserAlreadyExistsException(String message) {
        super(message) ;
    }
}
