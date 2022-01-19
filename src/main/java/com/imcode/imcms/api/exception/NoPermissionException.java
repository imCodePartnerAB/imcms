package com.imcode.imcms.api.exception;

/**
 * This exception is thrown when the current logged in user doesn't have the appropriate rights to do that operation.
 * See the message for further information.
 */
public class NoPermissionException extends RuntimeException {

    public NoPermissionException(String message) {
        super(message);
    }
}
