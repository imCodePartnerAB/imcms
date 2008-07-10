package imcode.server.user;

public class RoleAlreadyExistsException extends UserAndRoleRegistryException {

    public RoleAlreadyExistsException( String message ) {
        super(message) ;
    }
}
