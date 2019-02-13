package com.imcode.imcms.api.exception;

/**
 * This exception is thrown from almost all methods in the service classes. It is thrown when the current logged
 * in user dosen't have the apropiate rights to do that operation. See the message for futher information.
 */
public class NoPermissionException extends RuntimeException {

    public NoPermissionException(String message) {
        super(message);
    }
}
