package imcode.server.user;

/**
 * Exception that throws when user's password is {@code null} for some reasons.
 * <p/>
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * Fixed by Ruslan Popenko from Ubrainians for imCode
 * 1.06.17.
 */
public class MissingRequestDataException extends RuntimeException {
    public MissingRequestDataException() {
        super();
    }

    public MissingRequestDataException(MissingRequestDataException e) {
        super(e);
    }
}
