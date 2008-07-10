package imcode.server.user;

public class NameTooLongException extends UserAndRoleRegistryException {

    public NameTooLongException( String message ) {
        super(message) ;
    }
}
