package imcode.server.user;

/**
 * Exception that throws when user's password is {@code null} for some reasons.
 * <p/>
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * 26.12.16.
 */
public class MissingPasswordException extends RuntimeException {
    public MissingPasswordException() {
        super();
    }

    public MissingPasswordException(MissingPasswordException e) {
        super(e);
    }
}
