package imcode.server.user;

/**
 * Exception that throws when user's password is {@code null} for some reasons.
 * <p/>
 * Created by Serhii Maksymchuk from Ubrainians for imCode
 * Updated by Ruslan Popenko from Ubrainians for imCode
 * 1.06.17.
 */
public class MissingLoginDataException extends RuntimeException {
    public MissingLoginDataException() {
        super();
    }

    public MissingLoginDataException(MissingLoginDataException e) {
        super(e);
    }
}
