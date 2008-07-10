package com.imcode.imcms.api;

public class UserAlreadyExistsException extends AlreadyExistsException {

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
