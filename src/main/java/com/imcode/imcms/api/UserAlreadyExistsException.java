package com.imcode.imcms.api;

public class UserAlreadyExistsException extends AlreadyExistsException {

    private static final long serialVersionUID = 6932287189129039304L;

    public UserAlreadyExistsException(Throwable e) {
        super(e);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
