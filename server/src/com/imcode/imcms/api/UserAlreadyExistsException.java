package com.imcode.imcms.api;

/**
 * Thrown when saving users, indicating that a user with given name already exists.
 */
public class UserAlreadyExistsException extends AlreadyExistsException {

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
